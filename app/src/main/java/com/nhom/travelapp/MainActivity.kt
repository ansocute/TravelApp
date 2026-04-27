package com.nhom.travelapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.nhom.travelapp.core.firebase.FirebaseProvider
import com.nhom.travelapp.databinding.ActivityMainBinding
import com.nhom.travelapp.ui.map.MapsFragment
import com.nhom.travelapp.ui.discovery.DiscoveryFragment
import com.nhom.travelapp.ui.planner.PlannerFragment
import com.nhom.travelapp.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Cài đặt thanh top bar (Avatar)
        setupTopBar()

        // 2. Cấu hình điều hướng (Bottom Navigation)
        setupNavigation()

        // 3. Thiết lập màn hình mặc định khi mở app
        if (savedInstanceState == null) {
            // Tự động chọn tab Explore (nạp DiscoveryFragment)
            binding.bottomNavigation.selectedItemId = R.id.nav_explore
        }
    }

    private fun setupTopBar() {
        val user = FirebaseProvider.auth.currentUser
        val photoUrl = user?.photoUrl

        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(binding.layoutTopBar.ivTopAvatar)
        } else {
            binding.layoutTopBar.ivTopAvatar.setImageResource(R.drawable.ic_profile)
        }

        binding.layoutTopBar.ivTopAvatar.setOnClickListener {
            binding.bottomNavigation.selectedItemId = R.id.nav_profile
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // Tránh nạp lại Fragment nếu đang ở chính tab đó
            if (item.itemId == binding.bottomNavigation.selectedItemId &&
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) != null) {
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {
                R.id.nav_explore -> {
                    replaceFragment(DiscoveryFragment())
                    true
                }
                R.id.nav_map -> {
                    replaceFragment(MapsFragment())
                    true
                }
                R.id.nav_planner -> {
                    replaceFragment(PlannerFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}