package com.nhom.travelapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.repository.AuthRepository

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _passwordResetStatus = MutableLiveData<Resource<String>>()
    val passwordResetStatus: LiveData<Resource<String>> = _passwordResetStatus

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = FirebaseProvider.auth.currentUser

        _userName.value = currentUser?.displayName?.takeIf { it.isNotEmpty() } ?: "Người khám phá"
        _userEmail.value = currentUser?.email ?: "Chưa có email"
    }

    // Hàm xử lý gửi email đặt lại mật khẩu
    fun sendPasswordResetEmail() {
        val email = _userEmail.value
        if (email.isNullOrEmpty() || email == "Chưa có email") {
            _passwordResetStatus.value = Resource.Error("Không tìm thấy địa chỉ email hợp lệ.")
            return
        }

        _passwordResetStatus.value = Resource.Loading

        FirebaseProvider.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _passwordResetStatus.value = Resource.Success("Đã gửi liên kết đổi mật khẩu. Vui lòng kiểm tra hộp thư của bạn!")
                } else {
                    _passwordResetStatus.value = Resource.Error(task.exception?.message ?: "Có lỗi xảy ra khi gửi email.")
                }
            }
    }

    fun logout() {
        authRepository.logout()
    }
}