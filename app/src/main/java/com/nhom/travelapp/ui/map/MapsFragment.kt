package com.nhom.travelapp.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nhom.travelapp.R
import com.nhom.travelapp.databinding.FragmentMapsBinding
import com.nhom.travelapp.services.LocationService
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var locationService: LocationService
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private var searchMarker: Marker? = null // Khai báo ở đầu class

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationService = LocationService(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setupBottomSheet()
        setupZoomControls()
        setupSearchBar()
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.root.findViewById<View>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupZoomControls() {
        binding.btnZoomIn.setOnClickListener {
            if (::mMap.isInitialized) mMap.animateCamera(CameraUpdateFactory.zoomIn())
        }
        binding.btnZoomOut.setOnClickListener {
            if (::mMap.isInitialized) mMap.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }

    private fun setupSearchBar() {
        val searchAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        binding.edtSearch.setAdapter(searchAdapter)

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.length >= 2) {
                    Thread {
                        try {
                            val geocoder = Geocoder(requireContext(), Locale("vi", "VN"))
                            // Tìm kiếm với từ khóa + "Việt Nam" để thu hẹp phạm vi
                            val addresses = geocoder.getFromLocationName("$query, Việt Nam", 10)

                            val suggestions = addresses?.mapNotNull { it.getAddressLine(0) } ?: emptyList()

                            activity?.runOnUiThread {
                                if (suggestions.isNotEmpty()) {
                                    // 1. Xóa dữ liệu cũ
                                    searchAdapter.clear()
                                    // 2. Thêm dữ liệu mới từ Geocoder
                                    searchAdapter.addAll(suggestions)
                                    // 3. Thông báo thay đổi
                                    searchAdapter.notifyDataSetChanged()

                                    // 4. QUAN TRỌNG: Gọi lệnh này để bảng gợi ý hiện ra ngay lập tức
                                    // mà không bị hệ thống tự động lọc lại theo text hiện tại
                                    binding.edtSearch.showDropDown()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtSearch.setOnItemClickListener { parent, _, position, _ ->
            val selectedAddress = parent.getItemAtPosition(position) as String
            // Xóa text hiện tại và set address đầy đủ cho chuyên nghiệp
            binding.edtSearch.setText(selectedAddress)
            moveToAddress(selectedAddress)
        }
    }

    private fun moveToAddress(addressName: String) {
        val geocoder = Geocoder(requireContext(), Locale("vi", "VN"))
        try {
            val locations = geocoder.getFromLocationName(addressName, 1)
            if (!locations.isNullOrEmpty()) {
                val target = LatLng(locations[0].latitude, locations[0].longitude)

                searchMarker?.remove() // Xóa marker cũ nếu có
                searchMarker = mMap.addMarker(MarkerOptions()
                    .position(target)
                    .title(addressName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 17f))
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Lỗi kết nối tìm kiếm", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 1. Cấu hình giao diện bản đồ cơ bản
        mMap.uiSettings.apply {
            isZoomControlsEnabled = false // Tắt nút zoom mặc định vì bạn đã có FAB riêng
            isMyLocationButtonEnabled = true
            isCompassEnabled = true
        }

        // 2. Thêm Padding để logo Google không bị che bởi thanh SearchCard và Bottom Navigation
        // Top padding khoảng 250-300px tùy độ cao SearchBar của bạn
        mMap.setPadding(0, 280, 0, 0)

        // 3. Đưa Camera về vị trí mặc định ngay lập tức (TP.HCM)
        // Việc này giúp bản đồ có nội dung hiển thị ngay cả khi chưa load xong Style hoặc Vị trí
        val hcmc = LatLng(10.7769, 106.7009)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmc, 15f))

        // 4. Thêm các địa điểm du lịch mẫu (Dinh Độc Lập, Nhà thờ Đức Bà...)
        addTravelMarkers()

        // 5. Cố gắng tải Map Style từ file JSON
        // Đặt trong try-catch để nếu file style lỗi (gây màn hình nâu), app vẫn chạy bình thường
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) {
                android.util.Log.e("MapsError", "Không thể phân giải file Map Style JSON.")
            }
        } catch (e: Exception) {
            android.util.Log.e("MapsError", "Lỗi khi tải Style: ${e.message}")
        }

        // 6. Kiểm tra quyền và lấy vị trí thực tế của người dùng
        checkPermissionAndGetLocation()

        // 7. Thiết lập các sự kiện tương tác trên bản đồ
        mMap.setOnMarkerClickListener { marker ->
            // Nếu không phải marker vị trí hiện tại (chấm xanh), thì hiện Bottom Sheet
            if (marker.title != "Vị trí của bạn") {
                displayLocationInfo(marker)
            }
            false // Trả về false để sự kiện click mặc định của Google Maps vẫn chạy (hiện title)
        }

        mMap.setOnMapClickListener {
            // Ẩn Bottom Sheet khi người dùng chạm ra ngoài bản đồ
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun displayLocationInfo(marker: Marker) {
        val tvTitle = binding.root.findViewById<android.widget.TextView>(R.id.tvLocationName)
        val tvDistance = binding.root.findViewById<android.widget.TextView>(R.id.tvDistance)
        val btnDirections = binding.root.findViewById<android.widget.Button>(R.id.btnGetDirections)

        tvTitle?.text = marker.title
        tvDistance?.text = marker.snippet
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        btnDirections?.setOnClickListener {
            val uri = Uri.parse("google.navigation:q=${marker.position.latitude},${marker.position.longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            startActivity(intent)
        }
    }

    private fun addTravelMarkers() {
        val locations = listOf(
            LatLng(10.7769, 106.7009) to "Dinh Độc Lập",
            LatLng(10.7798, 106.6990) to "Nhà thờ Đức Bà",
            LatLng(10.7725, 106.6980) to "Chợ Bến Thành"
        )
        for (loc in locations) {
            mMap.addMarker(MarkerOptions()
                .position(loc.first)
                .title(loc.second)
                .snippet("Điểm tham quan nổi tiếng")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
        }
    }

    private fun checkPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else getUserLocation()
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        locationService.getCurrentLocation(
            onSuccess = { lat, lng ->
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
            },
            onFailure = { }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}