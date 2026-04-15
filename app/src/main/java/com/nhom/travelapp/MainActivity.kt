package com.nhom.travelapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nhom.travelapp.ui.discovery.DiscoveryFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Mở app lên thì mặc định vào thẳng tab Khám Phá
        if (savedInstanceState == null) {
            replaceFragment(DiscoveryFragment())

            bottomNav.selectedItemId = R.id.nav_discovery
        }

        // Sự kiện bấm đổi Tab
        // Mỗi tab nên sử dụng fragment
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_discovery -> replaceFragment(DiscoveryFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}