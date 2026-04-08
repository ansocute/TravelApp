package com.nhom.travelapp.ui.auth.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.core.utils.Validator
import com.nhom.travelapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _resetPasswordState = MutableLiveData<Resource<Unit>>(Resource.Idle)
    val resetPasswordState: LiveData<Resource<Unit>> = _resetPasswordState

    fun sendResetPasswordEmail(email: String) {
        val validationError = Validator.validateResetPassword(email)
        if (validationError != null) {
            _resetPasswordState.value = Resource.Error(validationError)
            return
        }

        viewModelScope.launch {
            _resetPasswordState.value = Resource.Loading
            _resetPasswordState.value = authRepository.sendPasswordResetEmail(email.trim())
        }
    }

    fun resetState() {
        _resetPasswordState.value = Resource.Idle
    }
}