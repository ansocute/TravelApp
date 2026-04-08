package com.nhom.travelapp.ui.auth.forgotpassword

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.ui.auth.common.AuthState

class ForgotPasswordViewModel : ViewModel() {

    private val _resetPasswordState = MutableLiveData<AuthState>(AuthState.Idle)
    val resetPasswordState: LiveData<AuthState> = _resetPasswordState

    fun sendResetPasswordEmail(email: String) {
        val emailTrimmed = email.trim()

        if (emailTrimmed.isEmpty()) {
            _resetPasswordState.value = AuthState.Error("Vui lòng nhập email")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            _resetPasswordState.value = AuthState.Error("Email không hợp lệ")
            return
        }

        _resetPasswordState.value = AuthState.Loading

        FirebaseProvider.auth.sendPasswordResetEmail(emailTrimmed)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _resetPasswordState.value =
                        AuthState.Success("Đã gửi email đặt lại mật khẩu")
                } else {
                    _resetPasswordState.value =
                        AuthState.Error(task.exception?.message ?: "Không gửi được email đặt lại mật khẩu")
                }
            }
    }

    fun resetState() {
        _resetPasswordState.value = AuthState.Idle
    }
}