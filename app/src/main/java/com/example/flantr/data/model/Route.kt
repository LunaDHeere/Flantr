package com.example.flantr.data.model

data class Route(
    val id: String = "",
    val name: String = "",
    val theme: String = "",
    val description: String = "",
    val totalTimeMinutes: Int = 0,
    val distance: String = "",
    val stops: List<Stop> = emptyList(), // Firebase handles lists automatically
    val imageUrl: String? = null,
    val authorId: String = "" // Good practice: know who created it
)

data class Stop(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val estimatedTimeMinutes: Int = 0,
    val notes: String? = null,
    val geoPoint: GeoPoint? = null // Preparing for maps later (create a dummy class or use Firebase's)
)

data class GeoPoint(val lat: Double = 0.0, val lng: Double = 0.0)