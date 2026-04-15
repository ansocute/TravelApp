package com.nhom.travelapp.ui.discovery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.nhom.travelapp.R
import com.nhom.travelapp.data.model.Place
import com.nhom.travelapp.ui.details.DetailActivity

// tạo để test thử nhớ code vô đây
class DiscoveryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_discovery, container, false)


        val btnTestDetail = view.findViewById<Button>(R.id.btnTestDetail)
        // dữ liệu để test trang detail
        btnTestDetail.setOnClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            val dummyPlace = Place(
                id = "test_place_01",
                name = "Khu du lịch Suối Tiên",
                address = "120 Xa lộ Hà Nội, Quận 9, TP.HCM",
                description = "Dữ liệu test từ trang Khám Phá!",
                rating = 4.5f,
                category = "Tham quan",
                imageUrl = "https://ik.imagekit.io/tvlk/blog/2022/02/dia-diem-du-lich-viet-nam-cover.jpeg"
            )
            intent.putExtra("EXTRA_PLACE", dummyPlace)
            startActivity(intent)
        }

        return view
    }
}