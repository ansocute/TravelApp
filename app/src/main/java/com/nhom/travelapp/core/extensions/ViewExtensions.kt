package com.nhom.travelapp.core.extensions

import android.content.Context
import android.widget.Toast

fun Context.showFirebaseErrorToast(errorMessage: String?) {
    val viMessage = when {
        errorMessage.isNullOrEmpty() -> "Đã có lỗi xảy ra. Vui lòng thử lại!"

        // 1. Nhóm lỗi Đăng nhập (Login)
        errorMessage.contains("INVALID_LOGIN_CREDENTIALS", true) ||
                errorMessage.contains("wrong password", true) ||
                errorMessage.contains("user not found", true) ->
            "Email hoặc mật khẩu không chính xác!"

        errorMessage.contains("disabled", true) ->
            "Tài khoản của bạn đã bị khóa hoặc vô hiệu hóa!"

        // 2. Nhóm lỗi Đăng ký (Register)
        errorMessage.contains("email already in use", true) ||
                errorMessage.contains("email-already-in-use", true) ->
            "Email này đã được đăng ký. Vui lòng sử dụng email khác!"

        errorMessage.contains("weak password", true) ||
                errorMessage.contains("at least 6 characters", true) ->
            "Mật khẩu quá yếu! Vui lòng nhập ít nhất 6 ký tự."

        // 3. Nhóm lỗi định dạng (Validation)
        errorMessage.contains("badly formatted", true) ||
                errorMessage.contains("invalid-email", true) ->
            "Định dạng email không hợp lệ!"

        errorMessage.contains("missing email", true) ->
            "Vui lòng nhập địa chỉ email!"

        // 4. Nhóm lỗi Quên mật khẩu & Xác thực email
        errorMessage.contains("expired-action-code", true) ||
                errorMessage.contains("invalid-action-code", true) ->
            "Đường dẫn đã hết hạn hoặc không hợp lệ. Vui lòng gửi lại yêu cầu!"

        // 5. Nhóm lỗi Hệ thống & Mạng (System & Network)
        errorMessage.contains("network error", true) ||
                errorMessage.contains("network-request-failed", true) ->
            "Lỗi kết nối mạng. Vui lòng kiểm tra lại Wifi/4G!"

        errorMessage.contains("blocked", true) ||
                errorMessage.contains("too many requests", true) ->
            "Bạn đã thao tác quá nhiều lần. Vui lòng đợi một lát rồi thử lại!"

        errorMessage.contains("operation-not-allowed", true) ->
            "Phương thức đăng nhập này chưa được quản trị viên kích hoạt!"

        errorMessage.contains("internal-error", true) ->
            "Lỗi hệ thống máy chủ. Vui lòng thử lại sau!"

        // Nếu Firebase đẻ ra một lỗi lạ nào đó chưa kịp cập nhật
        else -> "Lỗi: $errorMessage"
    }

    Toast.makeText(this, viMessage, Toast.LENGTH_LONG).show()
}