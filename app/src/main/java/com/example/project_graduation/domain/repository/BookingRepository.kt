package com.example.project_graduation.domain.repository

import com.example.project_graduation.data.remote.dto.BookingDto
import com.example.project_graduation.data.remote.dto.BookingPaymentDto

interface BookingRepository {
    // Bookings
    suspend fun getAllBookings(): Result<List<BookingDto>>
    suspend fun getBookingById(bookingId: Int): Result<BookingDto>
    suspend fun getBookingsByUserId(userId: Int): Result<List<BookingDto>>

    // Payments
    suspend fun getPaymentByBookingGroupId(bookingGroupId: String): Result<List<BookingPaymentDto>>

    // TODO: Add later for CRUD
    // suspend fun createBooking(booking: BookingDto): Result<BookingDto>
    // suspend fun updateBooking(booking: BookingDto): Result<BookingDto>
    // suspend fun deleteBooking(bookingId: Int): Result<Unit>
}