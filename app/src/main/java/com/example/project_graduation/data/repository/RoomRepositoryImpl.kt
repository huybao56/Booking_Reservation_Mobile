package com.example.project_graduation.data.repository

import com.example.project_graduation.data.mapper.toDomain
import com.example.project_graduation.data.remote.api.RoomApi
import com.example.project_graduation.data.remote.dto.ImageDtoResponse
import com.example.project_graduation.data.remote.dto.RoomDto
import com.example.project_graduation.domain.model.Room
import com.example.project_graduation.domain.model.RoomImage
import com.example.project_graduation.domain.repository.RoomAvailability
import com.example.project_graduation.domain.repository.RoomRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class RoomRepositoryImpl(
    private val roomApi: RoomApi
) : RoomRepository {

    override suspend fun getAvailableRooms(
        hotelId: Int,
        checkIn: String,
        checkOut: String
    ): Result<List<RoomAvailability>> {
        return roomApi.getAvailableRooms(hotelId, checkIn, checkOut).map { dtos ->
            // Calculate number of nights
            val nights = try {
                val checkInDate = LocalDate.parse(checkIn)
                val checkOutDate = LocalDate.parse(checkOut)
                ChronoUnit.DAYS.between(checkInDate, checkOutDate).toInt()
            } catch (e: Exception) {
                1
            }

            dtos.map { dto ->
                RoomAvailability(
                    room = dto.room.toDomain(),
                    availableUnits = dto.availableUnits,
                    pricePerNight = dto.pricePerNight,
                    totalPrice = dto.pricePerNight * nights
                )
            }
        }
    }

    override suspend fun getRoomsByHotel(hotelId: Int): Result<List<Room>> {
        return roomApi.getRoomsByHotel(hotelId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getRoomById(roomId: Int): Result<Room> {
        return roomApi.getRoomById(roomId).map { dto ->
            dto.toDomain()
        }
    }

    override suspend fun createRoom(
        hotelId: Int,
        roomNumber: String,
        roomType: String,
        floor: Int,
        basePrice: Double,
        capacity: Int,
        description: String?,
        amenities: List<String>?
    ): Result<Int> {
        return roomApi.createRoom(
            hotelId = hotelId,
            roomNumber = roomNumber,
            roomType = roomType,
            floor = floor,
            basePrice = basePrice,
            capacity = capacity,
            description = description,
            amenities = null
        )
    }

    override suspend fun updateRoom(
        roomId: Int,
        roomNumber: String?,
        roomType: String?,
        floor: Int?,
        status: String?,
        basePrice: Double?,
        capacity: Int?,
        description: String?,
        amenities: List<String>?
    ): Result<Unit> {
        return roomApi.updateRoom(
            roomId = roomId,
            roomNumber = roomNumber,
            roomType = roomType,
            floor = floor,
            status = status,
            basePrice = basePrice,
            capacity = capacity,
            description = description,
            amenities = amenities
        )
    }

    override suspend fun deleteRoom(roomId: Int): Result<Unit> {
        return roomApi.deleteRoom(roomId)
    }

//    private fun RoomDto.toDomain() =
//        roomId = this.roomId,
//    hotelId = this.hotelId,
//    roomNumber = this.roomNumber,
//    roomType = this.roomType,
//    floor = this.floor,
//    status = this.status,
//    basePrice = this.basePrice,
//    capacity = this.capacity,
//    description = this.description,
//    amenities = this.amenities,
//    images = this.images.map
//    {
//        imgDto ->
//        RoomImage(
//            imageId = imgDto.imageId,
//            imageUrl = imgDto.imageUrl,
//            isPrimary = imgDto.isPrimary,
//            displayOrder = imgDto.displayOrder,
//            caption = imgDto.caption
//        )
//    },
//    primaryImage = this.primaryImage
//    )

    private fun RoomDto.toDomain() = Room(
        roomId = this.roomId,
        hotelId = this.hotelId,
        roomNumber = this.roomNumber,
        roomType = this.roomType,
        floor = this.floor,
        status = this.status,
        basePrice = this.basePrice,
        capacity = this.capacity,
        description = this.description,
        amenities = this.amenities,
        images = this.images.map { imgDto ->
            RoomImage(
                imageId = imgDto.imageId,
                imageUrl = imgDto.imageUrl,
                isPrimary = imgDto.isPrimary,
                displayOrder = imgDto.displayOrder,
                caption = imgDto.caption
            )
        },
        primaryImage = this.primaryImage
    )

    private fun Room.toDto() = RoomDto(
        roomId = this.roomId,
        hotelId = this.hotelId,
        roomNumber = this.roomNumber,
        roomType = this.roomType,
        floor = this.floor,
        status = this.status,
        basePrice = this.basePrice,
        capacity = this.capacity,
        description = this.description,
        amenities = this.amenities,
        images = this.images.map { img ->
            ImageDtoResponse(
                imageId = img.imageId,
                imageUrl = img.imageUrl,
                isPrimary = img.isPrimary,
                displayOrder = img.displayOrder,
                caption = img.caption
            )
        },
        primaryImage = this.primaryImage
    )
}