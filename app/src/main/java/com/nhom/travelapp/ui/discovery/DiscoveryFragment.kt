package com.nhom.travelapp.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.nhom.travelapp.data.repository.PlaceRepository
import com.nhom.travelapp.databinding.ActivityFragmentDiscoveryBinding
import com.nhom.travelapp.ui.adapter.PlaceAdapter

class DiscoveryFragment : Fragment() {

    private var _binding: ActivityFragmentDiscoveryBinding? = null
    private val binding get() = _binding!!
    private lateinit var placeAdapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityFragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupFilter()

        // Load dữ liệu ban đầu từ Repository
        loadLocalData()
    }

    private fun setupRecyclerView() {
        placeAdapter = PlaceAdapter(emptyList())
        binding.rvPlaces.apply {
            // Chia 2 cột cho đẹp
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = placeAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadLocalData() {
        // Gọi hàm từ companion object trong Repo
        val data = PlaceRepository.getPlaces()
        placeAdapter.updateData(data)
    }

    private fun setupSearch() {
        // Xử lý khi Đức nhập chữ vào ô tìm kiếm
        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString().trim()
            val allPlaces = PlaceRepository.getPlaces()

            if (query.isEmpty()) {
                placeAdapter.updateData(allPlaces)
            } else {
                val filtered = allPlaces.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.location.contains(query, ignoreCase = true)
                }
                placeAdapter.updateData(filtered)
            }
        }
    }

    private fun setupFilter() {
        binding.apply {
            // Lọc Tất cả
            chipAll.setOnClickListener {
                placeAdapter.updateData(PlaceRepository.getPlaces())
            }

            // Lọc Ẩm thực
            chipFood.setOnClickListener {
                val filtered = PlaceRepository.getPlaces().filter {
                    it.category.toString().equals("Food", ignoreCase = true)
                }
                placeAdapter.updateData(filtered)
            }

            // Lọc Cảnh đẹp
            chipSights.setOnClickListener {
                val filtered = PlaceRepository.getPlaces().filter {
                    it.category.toString().equals("Sights", ignoreCase = true)
                }
                placeAdapter.updateData(filtered)
            }

            // Lọc Khách sạn
            chipHotels.setOnClickListener {
                val filtered = PlaceRepository.getPlaces().filter {
                    it.category.toString().equals("Hotels", ignoreCase = true)
                }
                placeAdapter.updateData(filtered)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}