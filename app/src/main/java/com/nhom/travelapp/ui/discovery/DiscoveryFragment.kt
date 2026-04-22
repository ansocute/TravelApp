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

    private var _binding: ActivityFragmentDiscoveryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiscoveryViewModel by viewModels()
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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Fix lỗi "Too many arguments": Khởi tạo adapter chỉ với danh sách rỗng
        placeAdapter = PlaceAdapter(emptyList())

        binding.rvPlaces.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placeAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString().trim()
            viewModel.search(query)
        }
    }

    private fun setupFilter() {
        // Fix lỗi "Unresolved reference": Gọi trực tiếp từ binding
        // Đảm bảo ID trong XML là chipAll, chipFood, chipSights, chipHotels
        binding.apply {
            chipAll.setOnClickListener { viewModel.fetchPlaces() }
            chipFood.setOnClickListener { viewModel.filterByCategory("Food") }
            chipSights.setOnClickListener { viewModel.filterByCategory("Sights") }
            chipHotels.setOnClickListener { viewModel.filterByCategory("Hotels") }
        }
    }

    private fun observeViewModel() {
        viewModel.placesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
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
        _binding = null
    }
}