package com.example.flantr.data.model

data class Route(
    val id: String,
    val name: String,
    val theme: String,
    val description: String,
    val totalTimeMinutes: Int,
    val distance: String,
    val stops: List<Stop>,
    val imageUrl: String? = null
)

data class Stop(
    val id: String,
    val name: String,
    val address: String,
    val description: String,
    val estimatedTimeMinutes: Int,
    val notes: String? = null
)