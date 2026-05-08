package com.quoteday.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.quoteday.app.SettingsPrefs
import com.quoteday.app.data.FirestoreRepository
import com.quoteday.app.data.Quote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = Firebase.auth

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

    init {
        auth.addAuthStateListener { _currentUser.value = it.currentUser }
        auth.currentUser?.let { SettingsPrefs.setUid(getApplication(), it.uid) }
    }

    fun onGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { user ->
                    _currentUser.value = user
                    SettingsPrefs.setUid(getApplication(), user.uid)
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
}
