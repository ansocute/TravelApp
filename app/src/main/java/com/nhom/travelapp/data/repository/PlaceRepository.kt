package com.nhom.travelapp.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.Place
import kotlinx.coroutines.tasks.await

class PlaceRepository {

    private val database = FirebaseDatabase.getInstance().getReference("places")

    suspend fun getAllPlaces(): Resource<List<Place>> {
        return try {
            val snapshot = database.get().await()
            val places = snapshot.children.mapNotNull { it.getValue(Place::class.java) }
            Resource.Success(places)
        } catch (e: Exception) {
            // Sử dụng e.message để hết cảnh báo "unused parameter e"
            Resource.Error(e.message ?: "Lỗi kết nối dữ liệu")
        }
    }

    suspend fun searchPlaces(query: String): Resource<List<Place>> {
        return try {
            val snapshot = database.get().await()
            val places = snapshot.children.mapNotNull { it.getValue(Place::class.java) }
                .filter { it.name.contains(query, ignoreCase = true) }
            Resource.Success(places)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi tìm kiếm địa điểm")
        }
    }

    suspend fun getPlacesByCategory(category: String): Resource<List<Place>> {
        return try {
            val snapshot = database.orderByChild("category").equalTo(category).get().await()
            val places = snapshot.children.mapNotNull { it.getValue(Place::class.java) }
            Resource.Success(places)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi lọc theo danh mục")
        }
    }
}