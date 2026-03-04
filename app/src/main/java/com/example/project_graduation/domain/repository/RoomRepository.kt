package com.example.project_graduation.domain.repository

import com.example.project_graduation.domain.model.Room

data class RoomAvailability(
    val room: Room,
    val availableUnits: Int,
    val pricePerNight: Double,
    val totalPrice: Double
)

interface RoomRepository {
    /**
     * Get available room types for booking
     * @param hotelId Hotel ID
     * @param checkIn Check-in date (YYYY-MM-DD)
     * @param checkOut Check-out date (YYYY-MM-DD)
     */

    suspend fun getAvailableRooms(
        hotelId: Int,
        checkIn: String,
        checkOut: String
    ): Result<List<RoomAvailability>>

    /**
     * Get all room types for a hotel
     */
    suspend fun getRoomsByHotel(hotelId: Int): Result<List<Room>>

    // THÊM CÁC METHODS CHO ADMIN CRUD
    suspend fun getRoomById(roomId: Int): Result<Room>

    suspend fun createRoom(
        hotelId: Int,
        roomNumber: String,
        roomType: String,
        floor: Int,
        basePrice: Double,
        capacity: Int,
        description: String?,
        amenities: List<String>?
    ): Result<Int>  // Return created roomId

    suspend fun updateRoom(
        roomId: Int,
        roomNumber: String?,
        roomType: String?,
        floor: Int?,
        status: String?,
        basePrice: Double?,
        capacity: Int?,
        description: String?,
        amenities: List<String>?
    ): Result<Unit>

    suspend fun deleteRoom(roomId: Int): Result<Unit>
}
