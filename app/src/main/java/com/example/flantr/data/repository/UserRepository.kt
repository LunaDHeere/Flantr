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

    // Add a route ID to the user's "savedRoutes" list
    suspend fun bookmarkRoute(routeId: String) {
        val userId = auth.currentUser?.uid ?: return

        // ArrayUnion adds the element ONLY if it doesn't exist yet (no duplicates)
        db.collection("users").document(userId)
            .update("savedRouteIds", com.google.firebase.firestore.FieldValue.arrayUnion(routeId))
            .await()
    }

    // Remove bookmark
    suspend fun removeBookmark(routeId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .update("savedRouteIds", com.google.firebase.firestore.FieldValue.arrayRemove(routeId))
            .await()
    }
}
