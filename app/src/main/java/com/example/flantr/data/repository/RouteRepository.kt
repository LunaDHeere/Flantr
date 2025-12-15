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
        // Generate a random ID for the document first
        val newDocRef = routesCollection.document()

        // Copy the route but set the correct ID
        val routeWithId = route.copy(id = newDocRef.id)

        // Save it to Firestore
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
}