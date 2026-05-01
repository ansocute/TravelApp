package com.nhom.travelapp.ui.auth.forgotpassword

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nhom.travelapp.databinding.ActivityForgotPasswordBinding
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.core.extensions.showFirebaseErrorToast

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyBackgroundBlur()
        setupViews()
        observeViewModel()
    }

    private fun applyBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.ivBackground.setRenderEffect(
                RenderEffect.createBlurEffect(
                    20f,
                    20f,
                    Shader.TileMode.CLAMP
                )
            )
        }
    }

    private fun setupViews() {
        binding.btnSendResetEmail.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            viewModel.sendResetPasswordEmail(email)
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.resetPasswordState.observe(this) { state ->
            when (state) {
                is Resource.Idle -> setLoading(false)

                is Resource.Loading -> setLoading(true)

                is Resource.Success -> {
                    setLoading(false)
                    Toast.makeText(
                        this,
                        state.message ?: "Đã gửi email đặt lại mật khẩu",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

                is Resource.Error -> {
                    setLoading(false)
                    showFirebaseErrorToast(state.message)
                    viewModel.resetState()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSendResetEmail.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.tvBackToLogin.isEnabled = !isLoading
    }
}