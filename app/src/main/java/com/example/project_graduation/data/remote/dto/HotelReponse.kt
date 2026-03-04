package com.example.project_graduation.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class HotelDto(
    val hotelId: Int,
    val hotelName: String,
    val description: String?,
    val rating: Double?,
    val hotelAddress: String?,
    val images: List<ImageDtoResponse> = emptyList(),
    val primaryImage: String? = null,
    val createdAt: String? = null
)


@Serializable
data class ImageDtoResponse(
    val imageId: Int,
    val imageUrl: String,
    val isPrimary: Boolean,
    val displayOrder: Int,
    val caption: String? = null
)