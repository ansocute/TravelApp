package com.nhom.travelapp.ui.auth.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.ui.auth.common.AuthState

class LoginViewModel : ViewModel() {

    private val _loginState = MutableLiveData<AuthState>(AuthState.Idle)
    val loginState: LiveData<AuthState> = _loginState

    fun login(email: String, password: String) {
        val emailTrimmed = email.trim()
        val passwordTrimmed = password.trim()

        if (emailTrimmed.isEmpty()) {
            _loginState.value = AuthState.Error("Vui lòng nhập email")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            _loginState.value = AuthState.Error("Email không hợp lệ")
            return
        }

        if (passwordTrimmed.isEmpty()) {
            _loginState.value = AuthState.Error("Vui lòng nhập mật khẩu")
            return
        }

        if (passwordTrimmed.length < 6) {
            _loginState.value = AuthState.Error("Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        _loginState.value = AuthState.Loading

        FirebaseProvider.auth.signInWithEmailAndPassword(emailTrimmed, passwordTrimmed)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = AuthState.Success("Đăng nhập thành công")
                } else {
                    _loginState.value =
                        AuthState.Error(task.exception?.message ?: "Đăng nhập thất bại")
                }
            }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
    }
}