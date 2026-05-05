package com.nhom.travelapp.data.remote

import com.google.firebase.auth.FirebaseUser
import com.nhom.travelapp.core.firebase.FirebaseProvider
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.GoogleAuthProvider

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

    suspend fun loginWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = FirebaseProvider.auth.signInWithCredential(credential).await()

        return result.user ?: throw IllegalStateException("Không lấy được thông tin người dùng từ Google")
    }

    fun logout() {
        FirebaseProvider.auth.signOut()
    }
}