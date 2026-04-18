package com.nhom.travelapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.databinding.ActivityMainBinding
import com.nhom.travelapp.ui.auth.login.LoginActivity
import com.nhom.travelapp.data.repository.AuthRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        val currentUser = FirebaseProvider.auth.currentUser
        val email = currentUser?.email ?: "Người dùng"

        binding.tvWelcome.text = "Xin chào, $email"

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        authRepository.logout()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}