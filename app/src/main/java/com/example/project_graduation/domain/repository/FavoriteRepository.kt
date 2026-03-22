package com.example.project_graduation.domain.repository


import com.example.project_graduation.data.remote.dto.FavoriteDto

interface FavoriteRepository {
    suspend fun getFavorites(userId: Int): Result<List<FavoriteDto>>
    suspend fun addFavorite(userId: Int, hotelId: Int): Result<Boolean>
    suspend fun removeFavorite(userId: Int, hotelId: Int): Result<Boolean>
    suspend fun getFavoriteIds(userId: Int): Result<List<Int>>
}