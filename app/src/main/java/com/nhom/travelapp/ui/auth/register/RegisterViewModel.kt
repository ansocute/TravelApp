package com.nhom.travelapp.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.core.utils.Validator
import com.nhom.travelapp.data.model.User
import com.nhom.travelapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _registerState = MutableLiveData<Resource<User>>(Resource.Idle)
    val registerState: LiveData<Resource<User>> = _registerState

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        allowLocationAccess: Boolean
    ) {
        val validationError = Validator.validateRegister(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword
        )

        if (validationError != null) {
            _registerState.value = Resource.Error(validationError)
            return
        }

        viewModelScope.launch {
            _registerState.value = Resource.Loading
            _registerState.value = authRepository.register(
                fullName = fullName.trim(),
                email = email.trim(),
                password = password.trim(),
                allowLocationAccess = allowLocationAccess
            )
        }
    }

    fun resetState() {
        _registerState.value = Resource.Idle
    }
}