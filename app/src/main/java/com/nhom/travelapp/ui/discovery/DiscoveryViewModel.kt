package com.nhom.travelapp.ui.discovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.Place
import com.nhom.travelapp.data.repository.PlaceRepository
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val repository: PlaceRepository = PlaceRepository()
) : ViewModel() {

    private val _placesState = MutableLiveData<Resource<List<Place>>>(Resource.Idle)
    val placesState: LiveData<Resource<List<Place>>> = _placesState

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
        if (query.isEmpty()) {
            fetchPlaces()
            return
        }
        viewModelScope.launch {
            _placesState.value = Resource.Loading
            _placesState.value = repository.searchPlaces(query)
        }
    }
}