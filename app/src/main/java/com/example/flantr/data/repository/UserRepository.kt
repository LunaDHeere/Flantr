package com.example.flantr.data.repository

import com.example.flantr.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val usersRef = db.collection("users")

    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return usersRef.document(uid).get().await().toObject(User::class.java)
    }

    suspend fun saveUser(user: User) {
        usersRef.document(user.id).set(user).await()
    }

    suspend fun updateUser(uid: String, updates: Map<String, Any>) {
        usersRef.document(uid).update(updates).await()
    }
}
