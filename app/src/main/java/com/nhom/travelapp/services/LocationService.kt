package com.nhom.travelapp.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationService(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onSuccess: (Double, Double) -> Unit, onFailure: () -> Unit) {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Truyền latitude và longitude vào hàm callback
                    onSuccess(location.latitude, location.longitude)
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}