package com.nhom.travelapp.ui.auth.login

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.session.SessionManager
import com.nhom.travelapp.databinding.ActivityLoginBinding
import com.nhom.travelapp.ui.auth.forgotpassword.ForgotPasswordActivity
import com.nhom.travelapp.ui.auth.register.RegisterActivity
import com.nhom.travelapp.ui.map.MapsActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        applyBackgroundBlur()
        loadRememberedLogin()
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

    private fun loadRememberedLogin() {
        val isRememberMe = sessionManager.isRememberMeEnabled()
        val savedEmail = sessionManager.getSavedEmail()

        binding.cbRememberMe.isChecked = isRememberMe

        if (isRememberMe && savedEmail.isNotEmpty()) {
            binding.etEmail.setText(savedEmail)
            binding.etPassword.requestFocus()
        }
    }

    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            viewModel.login(email, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnGoogleLater.setOnClickListener {
            Toast.makeText(this, "Đăng nhập Google sẽ tích hợp sau", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebookLater.setOnClickListener {
            Toast.makeText(this, "Đăng nhập Facebook sẽ tích hợp sau", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is Resource.Idle -> setLoading(false)

                is Resource.Loading -> setLoading(true)

                is Resource.Success -> {
                    setLoading(false)
                    handleRememberMe()
                    Toast.makeText(
                        this,
                        state.message ?: "Đăng nhập thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToMain()
                }

                is Resource.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
            }
        }
    }

    private fun handleRememberMe() {
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val isRememberMe = binding.cbRememberMe.isChecked

        if (isRememberMe) {
            sessionManager.saveRememberMe(true)
            sessionManager.saveEmail(email)
        } else {
            sessionManager.clearRememberedLogin()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.cbRememberMe.isEnabled = !isLoading
        binding.tvForgotPassword.isEnabled = !isLoading
        binding.tvGoToRegister.isEnabled = !isLoading
        binding.btnGoogleLater.isEnabled = !isLoading
        binding.btnFacebookLater.isEnabled = !isLoading
    }

    private fun goToMain() {
        // Thay đổi MainActivity thành MapsActivity của An
        val intent = Intent(this, MapsActivity::class.java)

        // Cờ này giúp xóa sạch các Activity cũ (như Login) khỏi hàng chờ
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}