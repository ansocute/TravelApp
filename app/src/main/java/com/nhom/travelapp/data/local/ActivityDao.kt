package com.nhom.travelapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
@Dao
interface ActivityDao {

    @Insert
    suspend fun insert(activity: Activity)

    @Query("SELECT * FROM activities WHERE tripId = :tripId")
    suspend fun getByTripId(tripId: Int): List<Activity>

    @Delete
    suspend fun delete(activity: Activity)
    }