package com.example.project_graduation.data.remote.dto

import kotlinx.serialization.Serializable


/**
 * DTO for Booking
 */
@Serializable
data class BookingDto(
    val bookingId: Int,
    val userId: Int,
    val status: String,
    val checkIn: String,
    val checkOut: String,
    val totalPrice: Double,
    val createdAt: String,
    val roomId: Int,
    val bookingGroupId: String?,

    val roomNumber: String? = null,
    val hotelName: String? = null
)

@Serializable
data class BookingPaymentDto(
    val paymentId: Int,
    val provider: String?,
    val amount: Double?,
    val status: String?,
    val transactionId: String?,
    val paidAt: String?,
    val bookingGroupId: String?
)