package com.nhom.travelapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TripActivityDao {

    @Insert
    suspend fun insert(tripActivity: TripActivity)

    @Query("SELECT * FROM activities WHERE tripId = :tripId")
    suspend fun getByTripId(tripId: Int): List<TripActivity>

    @Delete
    suspend fun delete(tripActivity: TripActivity)
}