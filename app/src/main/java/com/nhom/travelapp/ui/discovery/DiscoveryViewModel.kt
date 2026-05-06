package com.nhom.travelapp.ui.discovery

import androidx.lifecycle.*
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.Place
import com.nhom.travelapp.data.repository.PlaceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoveryViewModel : ViewModel() {

    // 1. Xóa dòng khởi tạo repository cũ (vì giờ dùng companion object)

    private val _placesState = MutableLiveData<Resource<List<Place>>>(Resource.Idle)
    val placesState: LiveData<Resource<List<Place>>> = _placesState

    private var searchJob: Job? = null

    init {
        fetchPlaces()
    }

    fun fetchPlaces() {
        viewModelScope.launch {
            _placesState.value = Resource.Loading
            // 2. Gọi trực tiếp qua PlaceRepository
            _placesState.value = PlaceRepository.getAllPlaces()
        }
    }

    fun search(query: String) {
        searchJob?.cancel()
        if (query.isEmpty()) {
            fetchPlaces()
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            _placesState.value = Resource.Loading
            // 3. Gọi trực tiếp qua PlaceRepository
            _placesState.value = PlaceRepository.searchPlaces(query)
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _placesState.value = Resource.Loading
            // 4. Gọi trực tiếp qua PlaceRepository
            _placesState.value = PlaceRepository.getPlacesByCategory(category)
        }
    }

    fun syncDataToFirebase() {
        viewModelScope.launch {
            // 5. Gọi trực tiếp qua PlaceRepository
            PlaceRepository.pushDummyData()
            fetchPlaces()
        }
    }
}