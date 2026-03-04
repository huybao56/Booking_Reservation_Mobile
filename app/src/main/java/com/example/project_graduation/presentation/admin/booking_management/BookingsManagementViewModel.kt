package com.example.project_graduation.presentation.admin.booking_management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.remote.dto.BookingDto
import com.example.project_graduation.data.remote.dto.BookingPaymentDto
import com.example.project_graduation.domain.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingsManagementViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<BookingWithPayment>>(emptyList())
    val bookings: StateFlow<List<BookingWithPayment>> = _bookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadAllBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("BookingsManagementVM", "Loading all bookings from API...")

            bookingRepository.getAllBookings().fold(
                onSuccess = { bookingsList ->
                    // Load payment info for each booking
                    val bookingsWithPayments = bookingsList.map { booking ->
                        val payment = if (!booking.bookingGroupId.isNullOrEmpty()) {
                            bookingRepository.getPaymentByBookingGroupId(booking.bookingGroupId)
                                .getOrNull()
                                ?.firstOrNull()
                        } else {
                            null
                        }

                        BookingWithPayment(booking, payment)
                    }

                    _bookings.value = bookingsWithPayments
                    Log.d("BookingsManagementVM", "Loaded ${bookingsWithPayments.size} bookings with payment info")
                },
                onFailure = { error ->
                    _bookings.value = emptyList()
                    _error.value = error.message ?: "Failed to load bookings"
                    Log.e("BookingsManagementVM", "Error loading bookings: ${error.message}")
                }
            )

            _isLoading.value = false
        }
    }

    // TODO: Implement CRUD operations later
    fun updateBookingStatus(bookingId: Int, newStatus: String) {
        viewModelScope.launch {
            // TODO: Call API to update booking status
            Log.d("BookingsManagementVM", "Update booking $bookingId status to $newStatus")
        }
    }

    fun deleteBooking(bookingId: Int) {
        viewModelScope.launch {
            // TODO: Call API to delete booking
            Log.d("BookingsManagementVM", "Delete booking ID: $bookingId")
        }
    }
}