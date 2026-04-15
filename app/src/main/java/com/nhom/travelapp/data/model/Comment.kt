package com.nhom.travelapp.data.model

data class Comment(
    val userId: String = "",
    val placeId: String = "",
    val content: String = "",
    val timestamp: Long = 0L
)
