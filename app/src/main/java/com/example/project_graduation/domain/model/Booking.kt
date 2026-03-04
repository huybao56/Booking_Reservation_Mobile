package com.example.project_graduation.domain.model

data class Booking(
    val bookingId: Int,
    val userId: Int,
    val status: BookingStatus,
    val checkIn: String,
    val checkOut: String,
    val totalPrice: Double,
    val createdAt: String
)

enum class BookingStatus {
    PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW
}