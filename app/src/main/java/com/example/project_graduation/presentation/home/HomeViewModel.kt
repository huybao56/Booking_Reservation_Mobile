package com.example.project_graduation.presentation.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.model.Hotel
import com.example.project_graduation.domain.repository.HotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val TAG = "HomeViewModel"

data class SearchCriteria(
    val checkIn: String = "",
    val checkOut: String = "",
    val guests: Int = 2
)

class HomeViewModel(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchCriteria = MutableStateFlow(SearchCriteria(
        checkIn = LocalDate.now().toString(),
        checkOut = LocalDate.now().plusDays(1).toString(),
        guests = 2
    ))
    val searchCriteria: StateFlow<SearchCriteria> = _searchCriteria.asStateFlow()


    private val _allHotels = MutableStateFlow<List<Hotel>>(emptyList())


    init {
        Log.d(TAG, "=== HomeViewModel initialized ===")
        Log.d(TAG, "Initial SearchCriteria: ${_searchCriteria.value}")
        loadHotels()
    }

    fun loadHotels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d(TAG, "Starting to load hotels...")

            hotelRepository.getAllHotels().fold(
                onSuccess = { hotels ->
                    _hotels.value = hotels
                    Log.d(TAG, "Loaded ${hotels.size} hotels: $hotels")
                },
                onFailure = { error ->
                    _hotels.value = emptyList()
                    _error.value = error.message
                    Log.e(TAG, "Error loading hotels: ${error.message}")
                }
            )

            _isLoading.value = false

        }
    }
//    private val _searchCriteria = MutableStateFlow(
//        SearchCriteria(
//            checkIn = LocalDate.now().toString(),
//            checkOut = LocalDate.now().plusDays(1).toString(),
//            guests = 2
//        )
//    )
//    val searchCriteria = _searchCriteria.asStateFlow()

    fun updateSearchCriteria(checkIn: String, checkOut: String, guests: Int) {
        Log.d("ContentValues", "BEFORE UPDATE: ${_searchCriteria.value}")

        _searchCriteria.value = SearchCriteria(checkIn, checkOut, guests)

        Log.d("ContentValues", "AFTER UPDATE: ${_searchCriteria.value}")
    }

    fun refreshHotels() {
        Log.d(TAG, "🔄 Refreshing hotels...")
        loadHotels()
    }

    fun filterHotels() {
//        // Filter logic here
//        // For example: filter by capacity >= guests
//        viewModelScope.launch {
//            val filtered = _hotels.value.filter { hotel ->
//                // Your filter logic
//                true
//            }
//            // Update hotels state
//        }
//        _hotels.value = _allHotels.value

        _hotels.value = _allHotels.value
        Log.d("ContentValues", "Hotels filtered with criteria: ${_searchCriteria.value}")

    }
    fun logCurrentState() {
        Log.d("ContentValues", "Current SearchCriteria: ${_searchCriteria.value}")
    }


    fun clearError() {
        _error.value = null
    }
}