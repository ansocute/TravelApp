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
            val places = mutableListOf<Place>()

            for (child in snapshot.children) {
                val place = child.getValue(Place::class.java)
                if (place != null) {
                    places.add(place)
                }
            }
            Resource.Success(places)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối Realtime Database")
        }
    }

    suspend fun searchPlaces(query: String): Resource<List<Place>> {
        return try {
            val snapshot = database.get().await()
            val places = mutableListOf<Place>()

            for (child in snapshot.children) {
                val place = child.getValue(Place::class.java)
                if (place != null && place.name.contains(query, ignoreCase = true)) {
                    places.add(place)
                }
            }
            Resource.Success(places)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi tìm kiếm")
        }
    }
}