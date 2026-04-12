package com.nhom.travelapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nhom.travelapp.MainActivity
import com.nhom.travelapp.data.repository.AuthRepository
import com.nhom.travelapp.databinding.ActivitySplashBinding
import com.nhom.travelapp.ui.auth.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserSession()
    }

    private fun checkUserSession() {
        if (authRepository.getCurrentUser() != null) {
            goToMain()
        } else {
            goToLogin()
        }
    }

    private fun goToMain() {
        // Thay đổi MainActivity thành MapsActivity
        val intent = Intent(this, com.nhom.travelapp.ui.map.MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}