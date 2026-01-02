package com.example.flantr.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    // Stats
    val memberSince: Long = System.currentTimeMillis(),
    val tripCount: Int = 0,

    // Preferences - Transportation
    val includePublicTransport: Boolean = true,
    val walkingPace: String = "moderate", // "slow", "moderate", "fast"
    val maxWalkingDistance: Float = 5f, // Miles

    // Preferences - Accessibility
    val accessibilityMode: Boolean = false,
    val avoidStairs: Boolean = false,

    // Preferences - Route
    val preferScenic: Boolean = true,

    // Preferences - Notifications
    val notifyRouteReminders: Boolean = true,
    val notifyNewRoutes: Boolean = true,
    val notifyNearbyPlaces: Boolean = false,

    // Preferences - Appearance
    val appTheme: String = "auto", // "light", "dark", "auto"

    val savedRouteIds: List<String> = emptyList()
)