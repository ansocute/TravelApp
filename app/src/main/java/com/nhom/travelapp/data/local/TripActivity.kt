package com.nhom.travelapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class TripActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tripId: Int,
    val title: String,
    val time: String,
    val location: String,
    val image: String
)