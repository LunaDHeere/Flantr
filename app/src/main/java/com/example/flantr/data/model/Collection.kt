package com.example.flantr.data.model

import java.util.TreeMap

data class Collection(
    val id: String = "",
    val title: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val authorId: String = "",
    val routeIds: List<String> = emptyList(),
    val color: String = "blue",
    val description: String = "",
)