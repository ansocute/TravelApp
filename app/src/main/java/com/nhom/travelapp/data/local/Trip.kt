package com.nhom.travelapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity = bảng trong database
@Entity(tableName = "trips")
data class Trip(

    // Khóa chính, tự tăng
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Tiêu đề chuyến đi
    val title: String,

    // Địa điểm
    val location: String,

    // Ngày (Day 1, Day 2...)
    val day: Int
)