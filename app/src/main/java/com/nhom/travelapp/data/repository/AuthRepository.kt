package com.nhom.travelapp.data.repository

import com.google.firebase.auth.FirebaseUser
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.User
import com.nhom.travelapp.data.remote.AuthRemoteDataSource
import com.nhom.travelapp.data.remote.UserRemoteDataSource

class AuthRepository(
    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource(),
    private val userRemoteDataSource: UserRemoteDataSource = UserRemoteDataSource()
) {

    suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val firebaseUser = authRemoteDataSource.login(email, password)
            val profile = userRemoteDataSource.getUserProfile(firebaseUser.uid)
                ?: buildFallbackUser(firebaseUser)

            Resource.Success(
                data = profile,
                message = "Đăng nhập thành công"
            )
        } catch (e: Exception) {
            Resource.Error(
                message = e.localizedMessage ?: "Đăng nhập thất bại",
                throwable = e
            )
        }
    }

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        allowLocationAccess: Boolean
    ): Resource<User> {
        return try {
            val firebaseUser = authRemoteDataSource.register(email, password)

            val user = User(
                uid = firebaseUser.uid,
                fullName = fullName.trim(),
                email = email.trim(),
                phone = "",
                avatarUrl = "",
                createdAt = System.currentTimeMillis(),
                allowLocationAccess = allowLocationAccess
            )

            userRemoteDataSource.createUserProfile(user)

            Resource.Success(
                data = user,
                message = "Đăng ký thành công"
            )
        } catch (e: Exception) {
            Resource.Error(
                message = e.localizedMessage ?: "Đăng ký thất bại",
                throwable = e
            )
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            authRemoteDataSource.sendPasswordResetEmail(email)
            Resource.Success(
                data = Unit,
                message = "Đã gửi email đặt lại mật khẩu"
            )
        } catch (e: Exception) {
            Resource.Error(
                message = e.localizedMessage ?: "Không gửi được email đặt lại mật khẩu",
                throwable = e
            )
        }
    }

    suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return try {
            // Xác thực với Firebase Auth
            val firebaseUser = authRemoteDataSource.loginWithGoogle(idToken)

            // Kiểm tra xem user này đã có profile trong Database chưa
            var profile = userRemoteDataSource.getUserProfile(firebaseUser.uid)

            // Nếu chưa có (đăng nhập lần đầu), tạo profile mới
            if (profile == null) {
                profile = User(
                    uid = firebaseUser.uid,
                    fullName = firebaseUser.displayName ?: "Người dùng Google",
                    email = firebaseUser.email ?: "",
                    phone = firebaseUser.phoneNumber ?: "",
                    avatarUrl = firebaseUser.photoUrl?.toString() ?: "",
                    createdAt = System.currentTimeMillis(),
                    allowLocationAccess = false
                )
                userRemoteDataSource.createUserProfile(profile)
            }

            Resource.Success(
                data = profile,
                message = "Đăng nhập Google thành công"
            )
        } catch (e: Exception) {
            Resource.Error(
                message = e.localizedMessage ?: "Đăng nhập Google thất bại",
                throwable = e
            )
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return authRemoteDataSource.getCurrentUser()
    }

    fun logout() {
        authRemoteDataSource.logout()
    }

    private fun buildFallbackUser(firebaseUser: FirebaseUser): User {
        return User(
            uid = firebaseUser.uid,
            fullName = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            phone = "",
            avatarUrl = "",
            createdAt = 0L,
            allowLocationAccess = false
        )
    }
}