package com.example.project_graduation.data.mapper

import com.example.project_graduation.data.remote.dto.StaffBookingDto
import com.example.project_graduation.presentation.staff.staff_booking_management.StaffBooking

// ─── Mapper ───────────────────────────────────────────────────────────────────
private fun StaffBookingDto.toDomain() = StaffBooking(
    bookingId = bookingId.toString(),
    guestName = guestName,
    guestPhone = guestPhone,
    roomNumber = roomNumber,
    roomType = roomType,
    floor = floor,
    checkIn = checkIn,
    checkOut = checkOut,
    status = status,
    totalAmount = totalAmount,
    guests = guests,
    specialRequests = specialRequests
)