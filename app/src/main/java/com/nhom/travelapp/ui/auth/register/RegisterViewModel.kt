package com.nhom.travelapp.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.data.model.User
import com.nhom.travelapp.ui.auth.common.AuthState

class RegisterViewModel : ViewModel() {

    private val _registerState = MutableLiveData<AuthState>(AuthState.Idle)
    val registerState: LiveData<AuthState> = _registerState

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val fullNameTrimmed = fullName.trim()
        val emailTrimmed = email.trim()
        val passwordTrimmed = password.trim()
        val confirmPasswordTrimmed = confirmPassword.trim()

        if (fullNameTrimmed.isEmpty()) {
            _registerState.value = AuthState.Error("Vui lòng nhập họ tên")
            return
        }

        if (emailTrimmed.isEmpty()) {
            _registerState.value = AuthState.Error("Vui lòng nhập email")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            _registerState.value = AuthState.Error("Email không hợp lệ")
            return
        }

        if (passwordTrimmed.isEmpty()) {
            _registerState.value = AuthState.Error("Vui lòng nhập mật khẩu")
            return
        }

        if (passwordTrimmed.length < 6) {
            _registerState.value = AuthState.Error("Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        if (confirmPasswordTrimmed.isEmpty()) {
            _registerState.value = AuthState.Error("Vui lòng xác nhận mật khẩu")
            return
        }

        if (passwordTrimmed != confirmPasswordTrimmed) {
            _registerState.value = AuthState.Error("Mật khẩu xác nhận không khớp")
            return
        }

        _registerState.value = AuthState.Loading

        FirebaseProvider.auth.createUserWithEmailAndPassword(emailTrimmed, passwordTrimmed)
            .addOnCompleteListener { authTask ->
                if (!authTask.isSuccessful) {
                    _registerState.value =
                        AuthState.Error(authTask.exception?.message ?: "Đăng ký thất bại")
                    return@addOnCompleteListener
                }

                val uid = FirebaseProvider.auth.currentUser?.uid
                if (uid.isNullOrEmpty()) {
                    _registerState.value = AuthState.Error("Không lấy được UID người dùng")
                    return@addOnCompleteListener
                }

                val user = User(
                    uid = uid,
                    fullName = fullNameTrimmed,
                    email = emailTrimmed,
                    phone = "",
                    avatarUrl = "",
                    createdAt = System.currentTimeMillis()
                )

                FirebaseProvider.usersRef.child(uid).setValue(user)
                    .addOnSuccessListener {
                        _registerState.value = AuthState.Success("Đăng ký thành công")
                    }
                    .addOnFailureListener { e ->
                        _registerState.value =
                            AuthState.Error(e.message ?: "Lưu thông tin người dùng thất bại")
                    }
            }
    }

    fun resetState() {
        _registerState.value = AuthState.Idle
    }
}