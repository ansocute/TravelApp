package com.nhom.travelapp.data.remote

import com.google.firebase.auth.FirebaseUser
import com.nhom.travelapp.core.firebase.FirebaseProvider
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSource {

    suspend fun login(email: String, password: String): FirebaseUser {
        val result = FirebaseProvider.auth
            .signInWithEmailAndPassword(email, password)
            .await()

        return result.user ?: throw IllegalStateException("Không lấy được thông tin người dùng")
    }

    suspend fun register(email: String, password: String): FirebaseUser {
        val result = FirebaseProvider.auth
            .createUserWithEmailAndPassword(email, password)
            .await()

        return result.user ?: throw IllegalStateException("Không lấy được UID người dùng")
    }

    suspend fun sendPasswordResetEmail(email: String) {
        FirebaseProvider.auth.sendPasswordResetEmail(email).await()
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseProvider.auth.currentUser
    }

    fun logout() {
        FirebaseProvider.auth.signOut()
    }
}