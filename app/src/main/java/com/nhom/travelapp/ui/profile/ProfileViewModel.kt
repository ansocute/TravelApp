package com.nhom.travelapp.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.remote.UserRemoteDataSource
import com.nhom.travelapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRemoteDataSource: UserRemoteDataSource = UserRemoteDataSource()
) : ViewModel() {

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userAvatar = MutableLiveData<String>()
    val userAvatar: LiveData<String> = _userAvatar

    private val _userPhone = MutableLiveData<String>()
    val userPhone: LiveData<String> = _userPhone

    private val _userAboutMe = MutableLiveData<String>()
    val userAboutMe: LiveData<String> = _userAboutMe

    private val _passwordResetStatus = MutableLiveData<Resource<String>>()
    val passwordResetStatus: LiveData<Resource<String>> = _passwordResetStatus

    private val _allowLocation = MutableLiveData<Boolean>()
    val allowLocation: LiveData<Boolean> = _allowLocation

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = FirebaseProvider.auth.currentUser

        if (currentUser != null) {
            _userEmail.value = currentUser.email ?: "Chưa có email"

            viewModelScope.launch {
                try {
                    val userProfile = userRemoteDataSource.getUserProfile(currentUser.uid)
                    if (userProfile != null) {
                        _userName.value = userProfile.fullName.takeIf { it.isNotEmpty() } ?: "Nhà lữ hành"
                        _userAvatar.value = userProfile.avatarUrl
                        _userPhone.value = userProfile.phone.takeIf { it.isNotEmpty() } ?: "Chưa có số điện thoại"
                        _userAboutMe.value = userProfile.aboutMe.takeIf { it.isNotEmpty() } ?: "Hãy thêm vài dòng giới thiệu về bản thân bạn nhé!"
                        _allowLocation.value = userProfile.allowLocationAccess

                    }
                } catch (e: Exception) {
                    _userName.value = currentUser.displayName?.takeIf { it.isNotEmpty() } ?: "Nhà lữ hành"
                }
            }
        }
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

    fun toggleLocationAccess(isAllowed: Boolean) {
        val currentUser = FirebaseProvider.auth.currentUser ?: return
        viewModelScope.launch {
            try {
                userRemoteDataSource.updateLocationAccess(currentUser.uid, isAllowed)
                _allowLocation.value = isAllowed
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Lỗi cập nhật trạng thái vị trí: ${e.message}", e)
                _allowLocation.value = !isAllowed
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}