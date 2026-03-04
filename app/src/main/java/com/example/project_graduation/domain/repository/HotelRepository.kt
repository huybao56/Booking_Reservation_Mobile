package com.example.project_graduation.domain.repository

import com.example.project_graduation.domain.model.Hotel

interface HotelRepository {
    suspend fun getAllHotels(): Result<List<Hotel>>
    suspend fun getHotelById(hotelId: Int): Result<Hotel>
    // CRUD operations
//    suspend fun createHotel(
//        hotelName: String,
//        description: String?,
//        rating: Double?,
//        hotelAddress: String?
//    ): Result<Int>

//    suspend fun updateHotel(
//        hotelId: Int,
//        hotelName: String?,
//        description: String?,
//        rating: Double?,
//        hotelAddress: String?
//    ): Result<Boolean>

    suspend fun createHotel(hotel: Hotel): Result<Int>
    suspend fun updateHotel(hotelId: Int, hotel: Hotel): Result<Boolean>
    suspend fun deleteHotel(hotelId: Int): Result<Boolean>

}