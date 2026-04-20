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
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.ui.auth.login.LoginActivity
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import com.nhom.travelapp.core.extensions.showFirebaseErrorToast

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isFineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        val isCoarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        if (isFineLocationGranted || isCoarseLocationGranted) {
            Toast.makeText(this, "Đã cấp quyền chia sẻ vị trí", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bạn đã từ chối cấp quyền vị trí", Toast.LENGTH_SHORT).show()
            binding.switchLocationAccess.isChecked = false
        }
    }

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
            val allowLocationAccess = binding.switchLocationAccess.isChecked

            viewModel.register(
                fullName = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                allowLocationAccess = allowLocationAccess
            )
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnGoogleLater.setOnClickListener {
            Toast.makeText(this, "Đăng ký bằng Google sẽ tích hợp sau", Toast.LENGTH_SHORT).show()
        }


        binding.switchLocationAccess.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    Toast.makeText(this, "Đã tắt chia sẻ vị trí", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is Resource.Idle -> setLoading(false)

                is Resource.Loading -> setLoading(true)

                is Resource.Success -> {
                    setLoading(false)
                    Toast.makeText(
                        this,
                        state.message ?: "Đăng ký thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToMain()
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

        binding.btnRegister.isEnabled = !isLoading
        binding.etFullName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.switchLocationAccess.isEnabled = !isLoading
        binding.tvGoToLogin.isEnabled = !isLoading
        binding.btnGoogleLater.isEnabled = !isLoading
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}