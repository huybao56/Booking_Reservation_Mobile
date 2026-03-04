package com.example.project_graduation.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val roomId: Int,
    val hotelId: Int,
    val roomNumber: String,
    val roomType: String,
    val floor: Int,
    val status: String,
    val basePrice: Double,
    val capacity: Int,
    val description: String? = null,
    val amenities: List<String> = emptyList(),
    val images: List<ImageDtoResponse> = emptyList(),
    val primaryImage: String? = null
//    val imageUrl: String? = null
)

//@Serializable
//data class InventoryDto(
//    val inventoryId: Int,
//    val roomTypeId: Int,
//    val inventoryDate: String,
//    val totalUnits: Int,
//    val bookedCount: Int,
//    val heldCount: Int
//)
//
//@Serializable
//data class PricingDto(
//    val pricingId: Int,
//    val roomTypeId: Int,
//    val priceDate: String,
//    val price: Double
//)

@Serializable
data class RoomAvailabilityDto(
    val room: RoomDto,
    val availableUnits: Int,
    val pricePerNight: Double,
)

//@Serializable
//data class ImageDtoRoomResponse(
//    val imageId: Int,
//    val imageUrl: String,
//    val isPrimary: Boolean,
//    val displayOrder: Int,
//    val caption: String? = null
//)