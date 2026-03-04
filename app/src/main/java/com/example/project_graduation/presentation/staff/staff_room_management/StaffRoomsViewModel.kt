package com.example.project_graduation.presentation.staff.staff_room_management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StaffRoom(
    val roomId: Int,
    val roomNumber: String,
    val roomType: String,
    val floor: Int,
    val status: String,  // AVAILABLE / OCCUPIED / MAINTENANCE
    val basePrice: Double,
    val capacity: Int,
    val currentGuest: String? = null,
    val checkOutDate: String? = null
)

class StaffRoomsViewModel : ViewModel() {

    private val _rooms = MutableStateFlow<List<StaffRoom>>(emptyList())
    val rooms: StateFlow<List<StaffRoom>> = _rooms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    fun loadRooms(hotelId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            // TODO: roomRepository.getByHotel(hotelId)
            _rooms.value = getSampleStaffRooms()
            _isLoading.value = false
        }
    }

    fun updateRoomStatus(roomId: Int, newStatus: String) {
        viewModelScope.launch {
            // TODO: roomRepository.updateStatus(roomId, newStatus)
            _rooms.value = _rooms.value.map { r ->
                if (r.roomId == roomId) r.copy(status = newStatus) else r
            }
            _operationSuccess.value = "Phòng đã cập nhật → $newStatus"
        }
    }

    fun clearSuccess() {
        _operationSuccess.value = null
    }
}

// ─── Sample Data ─────────────────────────────────────────────────────────

fun getSampleStaffRooms(): List<StaffRoom> = listOf(
    StaffRoom(1, "101", "Deluxe Room", 1, "OCCUPIED", 750000.0, 2, "Nguyen Thi Lan", "27/02/2026"),
    StaffRoom(2, "102", "Standard", 1, "AVAILABLE", 400000.0, 2),
    StaffRoom(3, "103", "Standard", 1, "MAINTENANCE", 400000.0, 2),
    StaffRoom(4, "201", "Twin Room", 2, "AVAILABLE", 600000.0, 2),
    StaffRoom(5, "205", "Suite", 2, "OCCUPIED", 1500000.0, 4, "Tran Van Minh", "01/03/2026"),
    StaffRoom(6, "210", "Deluxe Room", 2, "AVAILABLE", 750000.0, 2),
    StaffRoom(7, "301", "Standard", 3, "AVAILABLE", 400000.0, 2),
    StaffRoom(8, "310", "Standard", 3, "AVAILABLE", 400000.0, 2),
    StaffRoom(9, "401", "Junior Suite", 4, "AVAILABLE", 1100000.0, 3),
    StaffRoom(10, "402", "Deluxe Room", 4, "AVAILABLE", 750000.0, 2),
)