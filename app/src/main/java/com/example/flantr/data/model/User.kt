package com.example.flantr.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val accessibilityMode: Boolean = false,
    val avoidStairs: Boolean = false,
    val tripCount: Int = 0,
    val placesVisited: Int =0,
    val memberSince: String = "",
)