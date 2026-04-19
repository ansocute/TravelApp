package com.nhom.travelapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Khai báo database gồm bảng Trip
@Database(entities = [Trip::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Liên kết với DAO
    abstract fun tripDao(): TripDao
}