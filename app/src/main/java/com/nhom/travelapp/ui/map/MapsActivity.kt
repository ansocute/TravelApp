package com.nhom.travelapp.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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

    // Mã định danh cho yêu cầu quyền (Số bất kỳ)
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        locationService = LocationService(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupLogout()
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            // Nên xóa session ở đây nếu An đã có SessionManager
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkPermissionAndGetLocation()
    }

    private fun checkPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, hiện bảng hỏi xin quyền
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Đã có quyền, lấy vị trí luôn
            getUserLocation()
        }
    }

    private fun getUserLocation() {
        // Hiện chấm xanh vị trí của Google (nếu có quyền)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }

        locationService.getCurrentLocation(
            onSuccess = { lat, lng ->
                val myLocation = LatLng(lat, lng)
                mMap.clear() // Xóa marker cũ nếu có
                mMap.addMarker(MarkerOptions().position(myLocation).title("Vị trí của bạn"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            },
            onFailure = {
                Toast.makeText(this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show()
                val hcm = LatLng(10.762622, 106.660172)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcm, 10f))
            }
        )
    }

    // Xử lý sau khi người dùng nhấn "Cho phép" hoặc "Từ chối" trên bảng hỏi
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền vị trí để dùng bản đồ", Toast.LENGTH_LONG).show()
            }
        }
    }
}