package com.nhom.travelapp.data.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val aboutMe: String = "",
    val avatarUrl: String = "",
    val createdAt: Long = 0L,
    val allowLocationAccess: Boolean = false
)