package com.nhom.travelapp.ui.auth.register

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nhom.travelapp.MainActivity
import com.nhom.travelapp.databinding.ActivityRegisterBinding
import com.nhom.travelapp.ui.auth.common.AuthState
import com.nhom.travelapp.ui.auth.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text?.toString().orEmpty()
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            val confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()

            viewModel.register(fullName, email, password, confirmPassword)
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnGoogleLater.setOnClickListener {
            Toast.makeText(this, "Đăng ký bằng Google sẽ tích hợp sau", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebookLater.setOnClickListener {
            Toast.makeText(this, "Đăng ký bằng Facebook sẽ tích hợp sau", Toast.LENGTH_SHORT).show()
        }

        binding.switchLocationAccess.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(
                    this,
                    "Tùy chọn vị trí đã bật. Phần xin quyền sẽ tích hợp sau.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is AuthState.Idle -> setLoading(false)

                is AuthState.Loading -> setLoading(true)

                is AuthState.Success -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    goToMain()
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

        binding.btnRegister.isEnabled = !isLoading
        binding.etFullName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.switchLocationAccess.isEnabled = !isLoading
        binding.tvGoToLogin.isEnabled = !isLoading
        binding.btnGoogleLater.isEnabled = !isLoading
        binding.btnFacebookLater.isEnabled = !isLoading
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}