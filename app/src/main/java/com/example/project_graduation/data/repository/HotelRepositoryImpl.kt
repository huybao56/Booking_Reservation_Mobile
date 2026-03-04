package com.example.project_graduation.data.repository

import com.example.project_graduation.data.remote.api.HotelApi
import com.example.project_graduation.data.remote.dto.HotelDto
import com.example.project_graduation.data.remote.dto.ImageDtoResponse
import com.example.project_graduation.domain.model.Hotel
import com.example.project_graduation.domain.model.HotelImage
import com.example.project_graduation.domain.repository.HotelRepository

class HotelRepositoryImpl(
    private val hotelApi: HotelApi
) : HotelRepository {
    override suspend fun getAllHotels(): Result<List<Hotel>> {
        return hotelApi.getAllHotels().map { dtoList ->
            dtoList.map { dto ->
                Hotel(
                    hotelId = dto.hotelId,
                    hotelName = dto.hotelName,
                    description = dto.description,
                    rating = dto.rating,
                    hotelAddress = dto.hotelAddress,
                    images = dto.images.map { imgDto ->
                        HotelImage(
                            imageId = imgDto.imageId,
                            imageUrl = imgDto.imageUrl,
                            isPrimary = imgDto.isPrimary,
                            displayOrder = imgDto.displayOrder,
                            caption = imgDto.caption
                        )
                    },
                    primaryImage = dto.primaryImage,
                    createdAt = dto.createdAt
                )
            }
        }
    }

    // Implement getHotelById
    override suspend fun getHotelById(hotelId: Int): Result<Hotel> {
        // cách 1: lấy từ list hotels
//        return getAllHotels().mapCatching { hotels ->
//            hotels.find { it.hotelId == hotelId }
//                ?: throw Exception("Hotel not found")
//        }

        // cách 2: API riêng cho hotel detail
        // return hotelApi.getHotelById(hotelId)


        return hotelApi.getHotelById(hotelId).map { dto ->
            Hotel(
                hotelId = dto.hotelId,
                hotelName = dto.hotelName,
                description = dto.description,
                rating = dto.rating,
                hotelAddress = dto.hotelAddress,
                images = dto.images.map { imgDto ->
                    HotelImage(
                        imageId = imgDto.imageId,
                        imageUrl = imgDto.imageUrl,
                        isPrimary = imgDto.isPrimary,
                        displayOrder = imgDto.displayOrder,
                        caption = imgDto.caption
                    )
                },
                primaryImage = dto.primaryImage,
                createdAt = dto.createdAt
            )
        }
    }


    //    override suspend fun createHotel(
//        hotelName: String,
//        description: String?,
//        rating: Double?,
//        hotelAddress: String?
//    ): Result<Int> {
//        return hotelApi.createHotel(hotelName, description, rating, hotelAddress)
//    }

    override suspend fun createHotel(hotel: Hotel): Result<Int> {
        return hotelApi.createHotel(hotel.toDto())
    }


//    override suspend fun updateHotel(
//        hotelId: Int,
//        hotelName: String?,
//        description: String?,
//        rating: Double?,
//        hotelAddress: String?
//    ): Result<Boolean> {
//        return hotelApi.updateHotel(hotelId, hotelName, description, rating, hotelAddress)
//    }

    override suspend fun updateHotel(hotelId: Int, hotel: Hotel): Result<Boolean> {
        return hotelApi.updateHotel(hotelId, hotel.toDto())
    }

    override suspend fun deleteHotel(hotelId: Int): Result<Boolean> {
        return hotelApi.deleteHotel(hotelId)
    }

    private fun Hotel.toDto() = HotelDto(
        hotelId = this.hotelId,
        hotelName = this.hotelName,
        description = this.description,
        rating = this.rating,
        hotelAddress = this.hotelAddress,
//        hotelPhoneNumber = this.hotelPhoneNumber,
//        hotelEmail = this.hotelEmail,
        images = this.images.map { img ->
            ImageDtoResponse(
                imageId = img.imageId,
                imageUrl = img.imageUrl,
                isPrimary = img.isPrimary,
                displayOrder = img.displayOrder,
                caption = img.caption
            )
        },
        primaryImage = this.primaryImage,
        createdAt = this.createdAt
    )

}