package com.example.project_graduation.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    val favoriteId: Int,
    val userId: Int,
    val hotelId: Int,
    val hotelName: String,
    val hotelAddress: String?,
    val rating: Double?,
    val createdAt: String
)

@Serializable
data class FavoriteActionDto(
    val message: String,
    val favoriteId: Int?,
    val isFavorite: Boolean
)