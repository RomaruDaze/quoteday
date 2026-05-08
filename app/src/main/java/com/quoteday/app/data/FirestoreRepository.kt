package com.quoteday.app.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirestoreRepository {
    private val db get() = Firebase.firestore

    private fun userDoc(uid: String) =
        db.collection("users").document(uid)

    private fun col(uid: String) =
        userDoc(uid).collection("quotes")

    // ── User document ────────────────────────────────────────────────────────

    fun observeUser(uid: String): Flow<User> = callbackFlow {
        val listener = userDoc(uid).addSnapshotListener { snap, err ->
            if (err != null) { trySend(User()); return@addSnapshotListener }
            trySend(User(isPremium = snap?.getBoolean("isPremium") ?: false))
        }
        awaitClose { listener.remove() }
    }

    suspend fun createUserIfAbsent(uid: String) {
        try {
            val ref = userDoc(uid)
            if (!ref.get().await().exists()) {
                ref.set(mapOf("isPremium" to false, "createdAt" to System.currentTimeMillis())).await()
            }
        } catch (_: Exception) { }
    }

    suspend fun setPremium(uid: String) {
        try { userDoc(uid).update("isPremium", true).await() }
        catch (_: Exception) { }
    }

    // ── Quotes collection ────────────────────────────────────────────────────

    fun observe(uid: String): Flow<List<Quote>> = callbackFlow {
        val listener = col(uid).addSnapshotListener { snap, err ->
            if (err != null || snap == null) { trySend(emptyList()); return@addSnapshotListener }
            trySend(snap.documents.mapNotNull { doc ->
                val text = doc.getString("text") ?: return@mapNotNull null
                Quote(firestoreId = doc.id, text = text, author = doc.getString("author") ?: "")
            })
        }
        awaitClose { listener.remove() }
    }

    suspend fun fetchAll(uid: String): List<Quote> = try {
        col(uid).get().await().documents.mapNotNull { doc ->
            val text = doc.getString("text") ?: return@mapNotNull null
            Quote(firestoreId = doc.id, text = text, author = doc.getString("author") ?: "")
        }
    } catch (_: Exception) { emptyList() }

    suspend fun add(uid: String, text: String, author: String) {
        try { col(uid).add(mapOf("text" to text, "author" to author)).await() }
        catch (_: Exception) { }
    }

    suspend fun update(uid: String, firestoreId: String, text: String, author: String) {
        if (firestoreId.isEmpty()) return
        try {
            col(uid).document(firestoreId)
                .set(mapOf("text" to text, "author" to author)).await()
        } catch (_: Exception) { }
    }

    suspend fun delete(uid: String, firestoreId: String) {
        if (firestoreId.isEmpty()) return
        try { col(uid).document(firestoreId).delete().await() }
        catch (_: Exception) { }
    }
}
