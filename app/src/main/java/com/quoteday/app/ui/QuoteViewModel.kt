package com.quoteday.app.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.quoteday.app.SettingsPrefs
import com.quoteday.app.data.BillingRepository
import com.quoteday.app.data.FirestoreRepository
import com.quoteday.app.data.Quote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = Firebase.auth

    private val billingRepository = BillingRepository(
        context = application,
        scope = viewModelScope,
        onPurchaseSuccess = {
            auth.currentUser?.uid?.let { FirestoreRepository.setPremium(it) }
        },
        onProductPrice = { _productPrice.value = it }
    )

    private val _productPrice = MutableStateFlow<String?>(null)
    val productPrice: StateFlow<String?> = _productPrice.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    val quotes: StateFlow<List<Quote>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else FirestoreRepository.observe(user.uid)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val isPremium: StateFlow<Boolean> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(false)
            else FirestoreRepository.observeUser(user.uid).map { it.isPremium }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val todayQuote: StateFlow<Quote?> = quotes
        .map { list ->
            if (list.isEmpty()) return@map null
            val today = LocalDate.now().toString()
            val storedDate = SettingsPrefs.getTodayQuoteDate(getApplication())
            val storedId = SettingsPrefs.getTodayQuoteId(getApplication())
            if (storedDate == today && storedId != null) {
                list.find { it.firestoreId == storedId } ?: run {
                    val pick = list.random()
                    SettingsPrefs.setTodayQuote(getApplication(), today, pick.firestoreId)
                    pick
                }
            } else {
                val pick = list.random()
                SettingsPrefs.setTodayQuote(getApplication(), today, pick.firestoreId)
                pick
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val limitReached: StateFlow<Boolean> = combine(quotes, isPremium) { q, premium ->
        !premium && q.size >= FREE_QUOTE_LIMIT
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    private val _showUpgradePrompt = MutableStateFlow(false)
    val showUpgradePrompt: StateFlow<Boolean> = _showUpgradePrompt.asStateFlow()

    fun triggerUpgradePrompt() { _showUpgradePrompt.value = true }
    fun dismissUpgradePrompt() { _showUpgradePrompt.value = false }

    init {
        auth.addAuthStateListener { _currentUser.value = it.currentUser }
        auth.currentUser?.let { user ->
            SettingsPrefs.setUid(getApplication(), user.uid)
            viewModelScope.launch { FirestoreRepository.createUserIfAbsent(user.uid) }
        }
        billingRepository.connect()
    }

    override fun onCleared() {
        super.onCleared()
        billingRepository.disconnect()
    }

    fun launchPurchase(activity: Activity) {
        viewModelScope.launch { billingRepository.launchPurchase(activity) }
    }

    fun restorePurchases() = billingRepository.triggerRestore()

    fun onGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { user ->
                    _currentUser.value = user
                    SettingsPrefs.setUid(getApplication(), user.uid)
                    FirestoreRepository.createUserIfAbsent(user.uid)
                    maybeSeeds(user.uid)
                }
            } catch (_: Exception) { }
        }
    }

    private suspend fun maybeSeeds(uid: String) {
        if (FirestoreRepository.fetchAll(uid).isNotEmpty()) return
        listOf(
            "The only way to do great work is to love what you do." to "Steve Jobs",
            "In the middle of every difficulty lies opportunity." to "Albert Einstein",
            "It does not matter how slowly you go as long as you do not stop." to "Confucius",
            "Life is what happens when you're busy making other plans." to "John Lennon",
            "The future belongs to those who believe in the beauty of their dreams." to "Eleanor Roosevelt",
        ).forEach { (text, author) -> FirestoreRepository.add(uid, text, author) }
    }

    fun addQuote(text: String, author: String) {
        if (text.isBlank()) return
        if (limitReached.value) { _showUpgradePrompt.value = true; return }
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch { FirestoreRepository.add(uid, text.trim(), author.trim()) }
    }

    fun updateQuote(quote: Quote) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            FirestoreRepository.update(uid, quote.firestoreId, quote.text, quote.author)
        }
    }

    fun deleteQuote(quote: Quote) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            FirestoreRepository.delete(uid, quote.firestoreId)
        }
    }

    fun signOut() {
        SettingsPrefs.setUid(getApplication(), null)
        auth.signOut()
        _currentUser.value = null
    }

    companion object {
        const val FREE_QUOTE_LIMIT = 20
    }
}
