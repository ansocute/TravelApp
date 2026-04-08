package com.nhom.travelapp.data.remote

import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.data.model.User
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource {

    suspend fun createUserProfile(user: User) {
        FirebaseProvider.usersRef
            .child(user.uid)
            .setValue(user)
            .await()
    }

    suspend fun getUserProfile(uid: String): User? {
        val snapshot = FirebaseProvider.usersRef
            .child(uid)
            .get()
            .await()

        return snapshot.getValue(User::class.java)
    }

    suspend fun updateLocationAccess(uid: String, allowLocationAccess: Boolean) {
        FirebaseProvider.usersRef
            .child(uid)
            .child("allowLocationAccess")
            .setValue(allowLocationAccess)
            .await()
    }
}