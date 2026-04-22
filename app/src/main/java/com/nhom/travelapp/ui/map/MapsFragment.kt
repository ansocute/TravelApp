package com.nhom.travelapp.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.BitmapDescriptor

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

        // 1. Cấu hình giao diện bản đồ
        mMap.uiSettings.apply {
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = true
            isCompassEnabled = true
        }
        mMap.setPadding(0, 280, 0, 0)

        // 2. Tải Map Style (Để trong try-catch là rất đúng)
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) android.util.Log.e("MapsError", "Style lỗi.")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Thêm các Marker du lịch mẫu
        addTravelMarkers()

        // 4. QUAN TRỌNG: Logic di chuyển Camera
        // Thay vì moveCamera cố định ở HCM tại đây, chúng ta sẽ gọi hàm lấy vị trí
        checkPermissionAndGetLocation()

        // 5. Các sự kiện tương tác
        setupMapInteractions()
    }

    private fun setupMapInteractions() {
        mMap.setOnMarkerClickListener { marker ->
            if (marker.title != "Vị trí của bạn") {
                displayLocationInfo(marker)
            }
            false
        }
        mMap.setOnMapClickListener {
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
    // Tạo một class đơn giản để quản lý dữ liệu
    // --- KHAI BÁO BIẾN Ở ĐẦU CLASS ---
    private val iconCache = mutableMapOf<String, BitmapDescriptor>()

    // --- DATA CLASS (Có thể để ngoài hoặc trong class) ---
    data class TravelLocation(
        val position: LatLng,
        val title: String,
        val type: String, // "park", "food", hoặc "default"
        val snippet: String
    )

    // --- HÀM LẤY ICON (Bản tối ưu dùng Cache) ---
    private fun getCustomIcon(type: String): BitmapDescriptor {
        return iconCache.getOrPut(type) {
            val drawableId = when (type) {
                "park" -> R.drawable.ic_park
                "food" -> R.drawable.ic_food
                else -> R.drawable.ic_default
            }
            val bitmap = BitmapFactory.decodeResource(resources, drawableId)
            // Resize 100x100 để marker không quá to che mất bản đồ
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
            BitmapDescriptorFactory.fromBitmap(resizedBitmap)
        }
    }

    // --- HÀM THÊM MARKER ---
    private fun addTravelMarkers() {
        val travelList = listOf(
            TravelLocation(LatLng(10.7769, 106.7009), "Dinh Độc Lập", "default", "Di tích lịch sử"),
            TravelLocation(LatLng(10.7798, 106.6990), "Nhà thờ Đức Bà", "default", "Kiến trúc cổ"),
            TravelLocation(LatLng(10.7884, 106.7048), "Thảo Cầm Viên", "park", "Công viên xanh"),
            TravelLocation(LatLng(10.7725, 106.6980), "Chợ Bến Thành", "food", "Ẩm thực & Mua sắm"),
            TravelLocation(LatLng(10.7825, 106.6990), "Hồ Con Rùa", "park", "Điểm check-in"),
            TravelLocation(LatLng(10.7751, 106.7004), "Nhà hát Thành phố", "default", "Địa điểm văn hóa")
        )

        for (place in travelList) {
            mMap.addMarker(MarkerOptions()
                .position(place.position)
                .title(place.title)
                .snippet(place.snippet)
                .icon(getCustomIcon(place.type))
            )
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

            // Gọi Service lấy vị trí
            locationService.getCurrentLocation(
                onSuccess = { lat, lng ->
                    // Nếu lấy được vị trí người dùng, bay camera về đó
                    val userLocation = LatLng(lat, lng)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                },
                onFailure = {
                    // Nếu lỗi hoặc người dùng tắt GPS, lúc này mới dùng tọa độ dự phòng (HCM)
                    val hcmc = LatLng(10.7769, 106.7009)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmc, 15f))
                    Toast.makeText(requireContext(), "Sử dụng vị trí mặc định", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}