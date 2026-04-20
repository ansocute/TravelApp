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

        setupTopBar()

        replaceFragment(MapsFragment())
        binding.bottomNavigation.selectedItemId = R.id.nav_explore

        setupNavigation()
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
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }
}