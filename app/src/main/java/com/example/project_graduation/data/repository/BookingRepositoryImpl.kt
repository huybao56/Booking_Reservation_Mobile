package com.example.project_graduation.data.repository

import com.example.project_graduation.data.remote.api.BookingApi
import com.example.project_graduation.data.remote.api.BookingPaymentApi
import com.example.project_graduation.data.remote.dto.BookingDto
import com.example.project_graduation.data.remote.dto.BookingPaymentDto
import com.example.project_graduation.domain.repository.BookingRepository

class BookingRepositoryImpl(
    private val bookingApi: BookingApi,
    private val bookingPaymentApi: BookingPaymentApi
) : BookingRepository {

    override suspend fun getAllBookings(): Result<List<BookingDto>> {
        return bookingApi.getAllBookings()
    }

    override suspend fun getBookingById(bookingId: Int): Result<BookingDto> {
        return bookingApi.getBookingById(bookingId)
    }

    override suspend fun getBookingsByUserId(userId: Int): Result<List<BookingDto>> {
        return bookingApi.getBookingsByUserId(userId)
    }

    override suspend fun getPaymentByBookingGroupId(bookingGroupId: String): Result<List<BookingPaymentDto>> {
        return bookingPaymentApi.getPaymentsByBookingGroupId(bookingGroupId)
    }
}