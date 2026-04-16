package com.nhom.travelapp.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.databinding.ActivityFragmentDiscoveryBinding
import com.nhom.travelapp.ui.adapter.PlaceAdapter

class DiscoveryFragment : Fragment() {

    // 1. Khai báo Binding đúng tên file XML activity_fragment_discovery
    private var _binding: ActivityFragmentDiscoveryBinding? = null
    private val binding get() = _binding!!

    // 2. Khai báo ViewModel và Adapter
    private val viewModel: DiscoveryViewModel by viewModels()
    private lateinit var placeAdapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Khởi tạo Binding
        _binding = ActivityFragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Khởi tạo adapter với danh sách rỗng ban đầu
        placeAdapter = PlaceAdapter(emptyList())

        // Gán LayoutManager và Adapter cho RecyclerView (ID là rvPlaces trong XML)
        binding.rvPlaces.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placeAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearch() {
        // Lắng nghe sự thay đổi chữ trong ô tìm kiếm (ID là etSearch trong XML)
        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString().trim()
            viewModel.search(query)
        }
    }

    private fun observeViewModel() {
        // Quan sát dữ liệu từ ViewModel
        viewModel.placesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    // Cập nhật danh sách mới vào Adapter
                    state.data?.let { listPlaces ->
                        placeAdapter.updateData(listPlaces)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Giải phóng binding để tránh rò rỉ bộ nhớ
        _binding = null
    }
}