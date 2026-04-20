package com.nhom.travelapp.core.extensions

import android.content.Context
import android.widget.Toast

fun Context.showFirebaseErrorToast(errorMessage: String?) {
    val viMessage = when {
        errorMessage == null -> "Đã có lỗi xảy ra. Vui lòng thử lại!"
        errorMessage.contains("INVALID_LOGIN_CREDENTIALS", true) || errorMessage.contains("wrong password", true) || errorMessage.contains("user not found", true) -> "Email hoặc mật khẩu không chính xác!"

        errorMessage.contains("badly formatted", true) -> "Định dạng email không hợp lệ!"

        errorMessage.contains("blocked", true) || errorMessage.contains("too many requests", true) -> "Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau!"

        errorMessage.contains("network error", true) -> "Lỗi kết nối mạng. Vui lòng kiểm tra lại internet!"

        errorMessage.contains("disabled", true) -> "Tài khoản của bạn đã bị khóa!"

        else -> "Lỗi: $errorMessage"
    }

    Toast.makeText(this, viMessage, Toast.LENGTH_LONG).show()
}