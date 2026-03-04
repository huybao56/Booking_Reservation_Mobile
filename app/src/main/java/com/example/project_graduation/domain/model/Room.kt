package com.example.project_graduation.domain.model

data class Room(
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
    val images: List<RoomImage> = emptyList(),
    val primaryImage: String? = null
)

data class RoomImage(
    val imageId: Int,
    val imageUrl: String,
    val isPrimary: Boolean,
    val displayOrder: Int,
    val caption: String? = null
)
