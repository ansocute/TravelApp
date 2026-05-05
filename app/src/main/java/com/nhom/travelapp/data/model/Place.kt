package com.nhom.travelapp.data.model

import java.io.Serializable

// Data class đại diện cho một địa điểm du lịch
data class Place(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val location: String = "",
    val category: String = "", // Ví dụ: "Ăn uống", "Tham quan", "Khách sạn"
    val rating: Float = 0f,
    val imageUrl: String = "", // Link ảnh từ Internet
    val description: String = ""
) : Serializable
