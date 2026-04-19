package com.nhom.travelapp.data.local

import androidx.room.*

@Dao
interface TripDao {

    // Thêm chuyến đi
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: Trip)

    // Lấy tất cả chuyến đi (sắp xếp theo ngày tăng dần)
    @Query("SELECT * FROM trips ORDER BY day ASC")
    suspend fun getAll(): List<Trip>

    // (OPTIONAL - cộng điểm) Xóa tất cả
    @Query("DELETE FROM trips")
    suspend fun deleteAll()
}