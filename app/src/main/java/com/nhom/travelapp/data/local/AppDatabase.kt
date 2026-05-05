package com.nhom.travelapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// THÊM Activity vào database + tăng version
@Database(
    entities = [Trip::class, Activity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Liên kết với DAO
    abstract fun tripDao(): TripDao

    // THÊM DAO mới
    abstract fun activityDao(): ActivityDao
}