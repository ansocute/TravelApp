package com.nhom.travelapp.ui.discovery

import androidx.lifecycle.*
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.Place
import com.nhom.travelapp.data.repository.PlaceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val repository: PlaceRepository = PlaceRepository()
) : ViewModel() {

    private val _placesState = MutableLiveData<Resource<List<Place>>>(Resource.Idle)
    val placesState: LiveData<Resource<List<Place>>> = _placesState

    private var searchJob: Job? = null

    init {
        fetchPlaces()
    }

    fun fetchPlaces() {
        viewModelScope.launch {
            _placesState.value = Resource.Loading
            _placesState.value = repository.getAllPlaces()
        }
    }

    fun search(query: String) {
        searchJob?.cancel()
        if (query.isEmpty()) {
            fetchPlaces()
            return
        }
        searchJob = viewModelScope.launch {
            delay(500) // Debounce 0.5s để tránh lag Firebase
            _placesState.value = Resource.Loading
            _placesState.value = repository.searchPlaces(query)
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _placesState.value = Resource.Loading
            _placesState.value = repository.getPlacesByCategory(category)
        }
    }
}