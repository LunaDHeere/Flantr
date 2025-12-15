package com.example.flantr.data.repository

import com.example.flantr.data.model.Route
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class RouteRepository {
    private val db = FirebaseFirestore.getInstance()
    private val routesCollection = db.collection("routes")

    // 1. Create (Upload) a new Route
    suspend fun createRoute(route: Route): String {
        val newDocRef = routesCollection.document()

        val routeWithId = route.copy(id = newDocRef.id)

        newDocRef.set(routeWithId).await()
        return newDocRef.id
    }

    // 2. Get All Routes (For Home Screen)
    suspend fun getAllRoutes(): List<Route> {
        val snapshot = routesCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .await()
        return snapshot.toObjects(Route::class.java)
    }

    // 3. Get Specific Route (For Active Route Screen)
    suspend fun getRouteById(routeId: String): Route? {
        val snapshot = routesCollection.document(routeId).get().await()
        return snapshot.toObject(Route::class.java)
    }

    //4. Get Routes by Author (For Profile Screen)
    suspend fun getRoutesByAuthor(authorId: String): List<Route> {
        return routesCollection
            .whereEqualTo("authorId", authorId)
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(Route::class.java)
    }
}