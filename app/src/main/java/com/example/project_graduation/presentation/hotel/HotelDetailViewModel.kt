package com.example.project_graduation.presentation.hotel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.model.Hotel
import com.example.project_graduation.domain.repository.HotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HotelDetailState(
    val hotel: Hotel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)

class HotelDetailViewModel(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HotelDetailState())
    val state: StateFlow<HotelDetailState> = _state.asStateFlow()

    fun loadHotelDetail(hotelId: Int) {
        viewModelScope.launch {
            _state.value = HotelDetailState(isLoading = true)
            Log.d("HotelDetailViewModel", "Loading hotel ID: $hotelId")

//            get hotel by id
            hotelRepository.getHotelById(hotelId).fold(
                onSuccess = { hotel ->
                    _state.value = HotelDetailState(hotel = hotel, isLoading = false)
                    Log.d("HotelDetailViewModel", "Loaded hotel: ${hotel.hotelName}")
                },
                onFailure = { error ->
                    _state.value = HotelDetailState(
                        isLoading = false,
                        error = error.message ?: "Failed to load hotel"
                    )
                    Log.e("HotelDetailViewModel", "Error: ${error.message}")
                }
            )
        }
    }

    fun toggleFavorite() {
        _state.value = _state.value.copy(isFavorite = !_state.value.isFavorite)
    }
}