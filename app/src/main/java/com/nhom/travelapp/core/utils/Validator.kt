package com.nhom.travelapp.core.utils

import android.util.Patterns

object Validator {

    fun validateLogin(email: String, password: String): String? {
        val emailTrimmed = email.trim()
        val passwordTrimmed = password.trim()

        if (emailTrimmed.isEmpty()) return "Vui lòng nhập email"
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) return "Email không hợp lệ"
        if (passwordTrimmed.isEmpty()) return "Vui lòng nhập mật khẩu"
        if (passwordTrimmed.length < 6) return "Mật khẩu phải có ít nhất 6 ký tự"

        return null
    }

    fun validateRegister(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        val fullNameTrimmed = fullName.trim()
        val emailTrimmed = email.trim()
        val passwordTrimmed = password.trim()
        val confirmPasswordTrimmed = confirmPassword.trim()

        if (fullNameTrimmed.isEmpty()) return "Vui lòng nhập họ tên"
        if (emailTrimmed.isEmpty()) return "Vui lòng nhập email"
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) return "Email không hợp lệ"
        if (passwordTrimmed.isEmpty()) return "Vui lòng nhập mật khẩu"
        if (passwordTrimmed.length < 6) return "Mật khẩu phải có ít nhất 6 ký tự"
        if (confirmPasswordTrimmed.isEmpty()) return "Vui lòng xác nhận mật khẩu"
        if (passwordTrimmed != confirmPasswordTrimmed) return "Mật khẩu xác nhận không khớp"

        return null
    }

    fun validateResetPassword(email: String): String? {
        val emailTrimmed = email.trim()

        if (emailTrimmed.isEmpty()) return "Vui lòng nhập email"
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) return "Email không hợp lệ"

        return null
    }
}