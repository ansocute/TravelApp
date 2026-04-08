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
import com.nhom.travelapp.ui.auth.common.AuthState
import com.nhom.travelapp.ui.auth.login.LoginActivity

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
                is AuthState.Idle -> setLoading(false)

                is AuthState.Loading -> setLoading(true)

                is AuthState.Success -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }

                is AuthState.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
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