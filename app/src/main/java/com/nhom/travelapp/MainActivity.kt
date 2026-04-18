package com.nhom.travelapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nhom.travelapp.databinding.ActivityMainBinding
import com.nhom.travelapp.ui.map.MapsFragment // Lưu ý: An sẽ cần chuyển MapsActivity thành MapsFragment
// Import thêm các Fragment của các bạn khác khi xong:
// import com.nhom.travelapp.ui.discovery.DiscoveryFragment
// import com.nhom.travelapp.ui.planner.PlannerFragment
// import com.nhom.travelapp.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Mặc định khi mở App sẽ hiện trang Explore (Discovery) của Đức/Tùng
        // Hiện tại mình tạm để hiện trang Map của An nhé
        replaceFragment(MapsFragment())

        // 2. Thiết lập sự kiện click cho Bottom Navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    // replaceFragment(DiscoveryFragment())
                    true
                }
                R.id.nav_map -> {
                    replaceFragment(MapsFragment())
                    true
                }
                R.id.nav_planner -> {
                    // replaceFragment(PlannerFragment())
                    true
                }
                R.id.nav_profile -> {
                    // replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }
}