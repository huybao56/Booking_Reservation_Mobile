package com.example.project_graduation.presentation.staff.staff_booking_management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.remote.api.StaffApi
import com.example.project_graduation.data.remote.dto.StaffBookingDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//data class StaffBooking(
//    val bookingId: String,
//    val guestName: String,
//    val roomNumber: String,
//    val roomType: String,
//    val checkIn: String,
//    val checkOut: String,
//    val status: String,   // PENDING / CONFIRMED / CHECKED_IN / CHECKED_OUT / CANCELLED
//    val totalAmount: Double,
//    val guests: Int = 1,
//    val specialRequests: String? = null
//)

// ─── Domain model (UI layer dùng) ────────────────────────────────────────────
data class StaffBooking(
    val bookingId      : String,
    val guestName      : String,
    val guestPhone     : String?,
    val roomNumber     : String,
    val roomType       : String,
    val floor          : Int,
    val checkIn        : String,
    val checkOut       : String,
    val status         : String,
    val totalAmount    : Double,
    val guests         : Int,
    val specialRequests: String? = null
)

class StaffBookingsViewModel(
    private val staffApi: StaffApi
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<StaffBooking>>(emptyList())
    val bookings: StateFlow<List<StaffBooking>> = _bookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBookings(hotelId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            staffApi.getBookingsByHotel(hotelId)
                .onSuccess { dtos ->
                    _bookings.value = dtos.map { it.toDomain() }
                }
                .onFailure { e ->
                    _error.value = "Không tải được bookings: ${e.message}"
                }
            _isLoading.value = false
        }
    }

    fun updateBookingStatus(bookingId: String, newStatus: String) {
        val id = bookingId.toIntOrNull() ?: return
        viewModelScope.launch {
            staffApi.updateBookingStatus(id, newStatus)
                .onSuccess {
                    // Cập nhật local ngay lập tức
                    _bookings.value = _bookings.value.map { b ->
                        if (b.bookingId == bookingId) b.copy(status = newStatus) else b
                    }
                    val label = when (newStatus) {
                        "CONFIRMED" -> "Confirmed"
                        "CHECKED_IN" -> "Check-in successfully"
                        "CHECKED_OUT" -> "Check-out successfully"
                        "CANCELLED" -> "Cancelled booking"
                        else -> newStatus
                    }
                    _operationSuccess.value = label
                }
                .onFailure { e ->
                    _error.value = "Cập nhật thất bại: ${e.message}"
                }
        }
    }

//    fun loadBookings(hotelId: Int) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            // TODO: bookingRepository.getByHotel(hotelId)
//            _bookings.value = getSampleStaffBookings()
//            _isLoading.value = false
//        }
//    }
//
//    fun updateBookingStatus(bookingId: String, newStatus: String) {
//        viewModelScope.launch {
//            // TODO: bookingRepository.updateStatus(bookingId, newStatus)
//            _bookings.value = _bookings.value.map { b ->
//                if (b.bookingId == bookingId) b.copy(status = newStatus) else b
//            }
//            _operationSuccess.value = "Booking $bookingId → $newStatus"
//        }
//    }

    fun clearSuccess() {
        _operationSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }
}

// ─── Mapper ───────────────────────────────────────────────────────────────────
private fun StaffBookingDto.toDomain() = StaffBooking(
    bookingId       = bookingId.toString(),
    guestName       = guestName,
    guestPhone      = guestPhone,
    roomNumber      = roomNumber,
    roomType        = roomType,
    floor           = floor,
    checkIn         = checkIn,
    checkOut        = checkOut,
    status          = status,
    totalAmount     = totalAmount,
    guests          = guests,
    specialRequests = specialRequests
)

// ─── Sample Data ─────────────────────────────────────────────────────────
//
//fun getSampleStaffBookings(): List<StaffBooking> = listOf(
//    StaffBooking(
//        "#BK2601",
//        "Nguyen Thi Lan",
//        "101",
//        "Deluxe Room",
//        "25/02/2026",
//        "27/02/2026",
//        "CONFIRMED",
//        1500000.0,
//        2
//    ),
//    StaffBooking(
//        "#BK2602",
//        "Tran Van Minh",
//        "205",
//        "Suite",
//        "25/02/2026",
//        "01/03/2026",
//        "CHECKED_IN",
//        4500000.0,
//        3,
//        "Early check-in"
//    ),
//    StaffBooking(
//        "#BK2603",
//        "Le Thi Hoa",
//        "310",
//        "Standard",
//        "25/02/2026",
//        "26/02/2026",
//        "PENDING",
//        800000.0,
//        1
//    ),
//    StaffBooking(
//        "#BK2604",
//        "Pham Duc Anh",
//        "402",
//        "Deluxe Room",
//        "24/02/2026",
//        "25/02/2026",
//        "CHECKED_OUT",
//        1500000.0,
//        2
//    ),
//    StaffBooking(
//        "#BK2605",
//        "Hoang My Linh",
//        "112",
//        "Twin Room",
//        "26/02/2026",
//        "28/02/2026",
//        "CONFIRMED",
//        1200000.0,
//        2
//    ),
//    StaffBooking(
//        "#BK2606",
//        "Do Van Tuan",
//        "301",
//        "Standard",
//        "23/02/2026",
//        "25/02/2026",
//        "CANCELLED",
//        800000.0,
//        1
//    ),
//    StaffBooking(
//        "#BK2607",
//        "Vo Thi Thu",
//        "220",
//        "Junior Suite",
//        "25/02/2026",
//        "02/03/2026",
//        "CONFIRMED",
//        3500000.0,
//        2,
//        "Anniversary decoration"
//    ),
//)