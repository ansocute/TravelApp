package com.nhom.travelapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.User
import com.nhom.travelapp.data.remote.UserRemoteDataSource
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRemoteDataSource: UserRemoteDataSource = UserRemoteDataSource()
) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    private val _updateStatus = MutableLiveData<Resource<String>>()
    val updateStatus: LiveData<Resource<String>> = _updateStatus

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val currentUser = FirebaseProvider.auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    val userProfile = userRemoteDataSource.getUserProfile(currentUser.uid)
                    val authEmail = currentUser.email ?: ""

                    if (userProfile != null) {
                        _userData.value = userProfile.copy(
                            email = userProfile.email.takeIf { it.isNotEmpty() } ?: authEmail
                        )
                    }
                    else {
                        _userData.value = User(
                            uid = currentUser.uid,
                            email = authEmail,
                            fullName = currentUser.displayName ?: ""
                        )
                    }
                } catch (e: Exception) {
                    _updateStatus.value = Resource.Error("Lỗi tải dữ liệu")
                }
            }
        }
    }

    fun updateProfile(fullName: String, phone: String, aboutMe: String) {
        val currentUser = FirebaseProvider.auth.currentUser ?: return
        _updateStatus.value = Resource.Loading

        viewModelScope.launch {
            try {
                userRemoteDataSource.updateUserDetails(currentUser.uid, fullName, phone, aboutMe)
                _updateStatus.value = Resource.Success("Hồ sơ của bạn đã được cập nhật!")
            } catch (e: Exception) {
                _updateStatus.value = Resource.Error(e.localizedMessage ?: "Lỗi khi cập nhật")
            }
        }
    }

    fun updateUserAvatar(base64Image: String) {
        val currentUser = FirebaseProvider.auth.currentUser ?: return
        _updateStatus.value = Resource.Loading

        viewModelScope.launch {
            try {
                userRemoteDataSource.updateUserAvatar(currentUser.uid, base64Image)
                _updateStatus.value = Resource.Success("Cập nhật ảnh đại diện thành công!")
            } catch (e: Exception) {
                _updateStatus.value = Resource.Error("Lỗi khi lưu ảnh: ${e.message}")
            }
        }
    }
}