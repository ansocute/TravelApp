package com.nhom.travelapp.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nhom.travelapp.R
import com.nhom.travelapp.services.LocationService

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationService: LocationService
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Sử dụng layout fragment_maps (phải chứa id: map và id: bottomSheet)
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationService = LocationService(requireContext())

        // 1. Ánh xạ Map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // 2. Khởi tạo Bottom Sheet (Lấy view từ tham số 'view')
        val bottomSheet = view.findViewById<CardView>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Áp dụng style Silver của An
        try {
            mMap.setMapStyle(com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        } catch (e: Exception) { e.printStackTrace() }

        addTravelMarkers()
        checkPermissionAndGetLocation()

        mMap.setOnMarkerClickListener { marker ->
            if (marker.title != "Vị trí của bạn") displayLocationInfo(marker)
            false
        }

        mMap.setOnMapClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun displayLocationInfo(marker: Marker) {
        // Tìm view trong Fragment phải thông qua view?.
        val tvTitle = view?.findViewById<TextView>(R.id.tvLocationName)
        val tvSnippet = view?.findViewById<TextView>(R.id.tvDistance)
        val btnDirections = view?.findViewById<Button>(R.id.btnGetDirections)

        tvTitle?.text = marker.title
        tvSnippet?.text = marker.snippet
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        btnDirections?.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    private fun addTravelMarkers() {
        val locations = listOf(
            LatLng(10.7769, 106.7009) to "Dinh Độc Lập",
            LatLng(10.7798, 106.6990) to "Nhà thờ Đức Bà",
            LatLng(10.7725, 106.6980) to "Chợ Bến Thành",
            LatLng(10.7750, 106.7068) to "Bitexco Financial Tower",
            LatLng(10.7543, 106.6639) to "Sân vận động Thống Nhất"
        )

        for (location in locations) {
            mMap.addMarker(MarkerOptions()
                .position(location.first)
                .title(location.second)
                .snippet("1.2 km away • HCM City")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
        }
    }

    private fun checkPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Trong Fragment, sử dụng requestPermissions trực tiếp của Fragment hoặc thông qua requireActivity()
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getUserLocation()
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        locationService.getCurrentLocation(
            onSuccess = { lat, lng ->
                val myLocation = LatLng(lat, lng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            },
            onFailure = { /* Xử lý khi lỗi */ }
        )
    }
}