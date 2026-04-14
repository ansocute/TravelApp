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

        // 1. Vừa vào là cắm các Marker địa điểm du lịch ngay
        addTravelMarkers()

        // 2. Sau đó mới kiểm tra quyền để hiện vị trí của An
        checkPermissionAndGetLocation()
    }

    private fun addTravelMarkers() {
        // Danh sách các địa điểm tiêu biểu (An có thể thêm bớt tọa độ tùy ý)
        val locations = listOf(
            LatLng(10.7769, 106.7009) to "Dinh Độc Lập",
            LatLng(10.7798, 106.6990) to "Nhà thờ Đức Bà",
            LatLng(10.7725, 106.6980) to "Chợ Bến Thành",
            LatLng(10.7750, 106.7068) to "Bitexco Financial Tower",
            LatLng(10.7543, 106.6639) to "Sân vận động Thống Nhất" // Thêm địa danh An thích nè
        )

        for (location in locations) {
            mMap.addMarker(
                MarkerOptions()
                    .position(location.first)
                    .title(location.second)
                    .snippet("Điểm đến hấp dẫn trong chuyến đi")
            )
        }
    }

    private fun checkPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getUserLocation()
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }

        locationService.getCurrentLocation(
            onSuccess = { lat, lng ->
                val myLocation = LatLng(lat, lng)

                // KHÔNG dùng mMap.clear() ở đây để giữ lại các Marker du lịch

                mMap.addMarker(MarkerOptions()
                    .position(myLocation)
                    .title("Vị trí của bạn")
                    // Có thể đổi màu Marker này để phân biệt với điểm du lịch
                    .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE))
                )

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