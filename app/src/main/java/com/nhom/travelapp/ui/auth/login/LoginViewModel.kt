package com.nhom.travelapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.core.utils.Validator
import com.nhom.travelapp.data.model.User
import com.nhom.travelapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<User>>(Resource.Idle)
    val loginState: LiveData<Resource<User>> = _loginState

    fun login(email: String, password: String) {
        val validationError = Validator.validateLogin(email, password)
        if (validationError != null) {
            _loginState.value = Resource.Error(validationError)
            return
        }

        viewModelScope.launch {
            _loginState.value = Resource.Loading
            _loginState.value = authRepository.login(email.trim(), password.trim())
        }
    }

    fun resetState() {
        _loginState.value = Resource.Idle
    }
}