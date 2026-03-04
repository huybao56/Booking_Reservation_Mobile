package com.example.project_graduation.domain.model

data class Hotel(
    val hotelId: Int,
    val hotelName: String,
    val description: String?,
    val rating: Double?,
    val hotelAddress: String?,
    val images: List<HotelImage> = emptyList(),
    val primaryImage: String? = null,
    val createdAt: String? = null
)



data class HotelImage(
    val imageId: Int,
    val imageUrl: String,
    val isPrimary: Boolean,
    val displayOrder: Int,
    val caption: String? = null
)