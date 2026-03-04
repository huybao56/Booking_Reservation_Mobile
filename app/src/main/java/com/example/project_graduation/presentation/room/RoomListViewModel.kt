package com.example.project_graduation.presentation.room

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.repository.RoomAvailability
import com.example.project_graduation.domain.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class RoomListState(
    val roomAvailabilities: List<RoomAvailability> = emptyList(),
    val checkIn: String = "",
    val checkOut: String = "",
    val numberOfNights: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedRoomType: String? = null,
    val hotelName: String = ""
)

class RoomListViewModel(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RoomListState())
    val state: StateFlow<RoomListState> = _state.asStateFlow()

    init {
        // Set default dates: today to tomorrow
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        _state.value = _state.value.copy(
            checkIn = today.format(formatter),
            checkOut = tomorrow.format(formatter),
            numberOfNights = 1,
            selectedRoomType = null
        )
    }


    fun updateDates(checkIn: String, checkOut: String) {
        _state.value = _state.value.copy(
            checkIn = checkIn,
            checkOut = checkOut
        )
        calculateNights()
    }

    fun updateHotelName(hotelName: String) {
        _state.value = _state.value.copy(hotelName = hotelName)
    }

    fun loadAvailableRoomTypes(hotelId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, selectedRoomType = null)

            Log.d("RoomListViewModel", "Loading rooms for hotel: $hotelId, checkIn: ${_state.value.checkIn}, checkOut: ${_state.value.checkOut}")

            roomRepository.getAvailableRooms(
                hotelId,
                _state.value.checkIn,
                _state.value.checkOut
            ).fold(
                onSuccess = { roomAvailabilities ->
                    _state.value = _state.value.copy(
                        roomAvailabilities = roomAvailabilities,
                        isLoading = false
                    )
                    Log.d("RoomListViewModel", "Loaded ${roomAvailabilities.size} room types")
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load rooms"
                    )
                    Log.e("RoomListViewModel", "Error: ${error.message}")
                }
            )
        }
    }

    fun updateCheckInDate(date: String, hotelId: Int) {
        _state.value = _state.value.copy(checkIn = date)
        calculateNights()
        loadAvailableRoomTypes(hotelId)
    }

    fun updateCheckOutDate(date: String, hotelId: Int) {
        _state.value = _state.value.copy(checkOut = date)
        calculateNights()
        loadAvailableRoomTypes(hotelId)
    }

    private fun calculateNights() {
        try {
            val checkIn = LocalDate.parse(_state.value.checkIn)
            val checkOut = LocalDate.parse(_state.value.checkOut)
            val nights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
            _state.value = _state.value.copy(numberOfNights = if (nights > 0) nights else 1)
        } catch (e: Exception) {
            _state.value = _state.value.copy(numberOfNights = 1)
        }
    }

    fun filterByRoomType(roomType: String?) {
        _state.value = _state.value.copy(selectedRoomType = roomType)
    }

    fun getFilteredRooms(): List<RoomAvailability> {
        val roomType = _state.value.selectedRoomType
        return if (roomType == null) {
            _state.value.roomAvailabilities
        } else {
            _state.value.roomAvailabilities.filter { it.room.roomType == roomType }
        }
    }
}