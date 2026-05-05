package com.nhom.travelapp.core.utils

sealed class Resource<out T> {
    data object Idle : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
    data class Success<T>(
        val data: T? = null,
        val message: String? = null
    ) : Resource<T>()

    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : Resource<Nothing>()
}