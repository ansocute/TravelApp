package com.nhom.travelapp.data.repository

import com.nhom.travelapp.data.local.Trip
import com.nhom.travelapp.data.local.TripDao

class PlannerRepository(private val dao: TripDao) {

    suspend fun insertTrip(trip: Trip) {
        dao.insert(trip)
    }

    suspend fun getTrips(): List<Trip> {
        return dao.getAll()
    }
}