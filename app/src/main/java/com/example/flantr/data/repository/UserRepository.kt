package com.example.flantr.data.repository

import com.example.flantr.data.model.Collection
import com.example.flantr.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    suspend fun bookmarkRoute(routeId: String) {
        val userId = auth.currentUser?.uid ?: return

        // ArrayUnion adds the element ONLY if it doesn't exist yet (no duplicates)
        db.collection("users").document(userId)
            .update("savedRouteIds", com.google.firebase.firestore.FieldValue.arrayUnion(routeId))
            .await()
    }

    suspend fun removeBookmark(routeId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .update("savedRouteIds", com.google.firebase.firestore.FieldValue.arrayRemove(routeId))
            .await()
    }

    // shouldn't this be added to a seperate CollectionRepository?

    suspend fun createCollection(collection: Collection) {
        val userId = auth.currentUser?.uid ?: return
        val newDoc = db.collection("users").document(userId).collection("collections").document()

        //why tf would you call it a "finalCollection"
        val finalCollection = collection.copy(
            id = newDoc.id,
            authorId = userId,
            lastUpdated = System.currentTimeMillis()
        )
        //why tf would i use an await here?
        newDoc.set(finalCollection).await()
    }

    suspend fun getUserCollections(): List<Collection>{
        val userId = auth.currentUser?.uid ?: return emptyList()
        return db.collection("users").document(userId)
            .collection("collections")
            .orderBy("lastUpdated", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Collection::class.java) // i have no idea why i need to do this

    }

    suspend fun addRouteToCollection(collectionId: String, routeId: String) {
        val userId = auth.currentUser?.uid ?: return
        val colRef = db.collection("users").document(userId)
            .collection("collections").document(collectionId)

        //wtf does a runbatch code block do?
        db.runBatch { batch ->
            batch.update(colRef, "routeIds", FieldValue.arrayUnion(routeId))
            batch.update(colRef, "lastUpdated", System.currentTimeMillis())
        }.await()
    }

    suspend fun deleteCollection(collectionId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .collection("collections").document(collectionId)
            .delete()
            .await()

    }

}
