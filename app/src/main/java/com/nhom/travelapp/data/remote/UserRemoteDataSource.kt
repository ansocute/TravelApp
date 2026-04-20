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

    suspend fun updateUserDetails(uid: String, fullName: String, phone: String, aboutMe: String) {
        val updates = mapOf<String, Any>(
            "fullName" to fullName,
            "phone" to phone,
            "aboutMe" to aboutMe
        )
        FirebaseProvider.usersRef
            .child(uid)
            .updateChildren(updates)
            .await()
    }

    suspend fun updateUserAvatar(uid: String, avatarUrl: String) {
        FirebaseProvider.usersRef
            .child(uid)
            .child("avatarUrl")
            .setValue(avatarUrl)
            .await()
    }

    suspend fun deleteUserProfile(uid: String) {
        FirebaseProvider.usersRef
            .child(uid)
            .removeValue()
            .await()
    }
}