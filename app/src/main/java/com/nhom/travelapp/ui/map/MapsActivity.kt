package com.nhom.travelapp.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nhom.travelapp.R
import com.nhom.travelapp.databinding.ActivityMapsBinding
import com.nhom.travelapp.services.LocationService
import com.nhom.travelapp.ui.auth.login.LoginActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationService: LocationService
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        locationService = LocationService(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Code đăng xuất quay về LoginActivity như mình đã hướng dẫn
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        locationService.getCurrentLocation(
            onSuccess = { lat, lng ->
                val myLocation = LatLng(lat, lng)
                // Thêm Marker tại vị trí của An
                mMap.addMarker(MarkerOptions().position(myLocation).title("Vị trí của tôi"))
                // Phóng to bản đồ vào vị trí đó (độ zoom 15f là vừa đẹp)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            },
            onFailure = {
                // Nếu không lấy được vị trí, mặc định hiện ở TP.HCM
                val hcm = LatLng(10.762622, 106.660172)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcm, 10f))
            }
        )
    }
}