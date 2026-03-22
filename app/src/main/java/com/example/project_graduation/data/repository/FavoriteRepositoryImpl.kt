package com.example.project_graduation.data.repository

import com.example.project_graduation.data.remote.api.FavoriteApi
import com.example.project_graduation.data.remote.dto.FavoriteDto
import com.example.project_graduation.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val favoriteApi: FavoriteApi
) : FavoriteRepository {

    override suspend fun getFavorites(userId: Int): Result<List<FavoriteDto>> =
        favoriteApi.getFavorites(userId)

    override suspend fun addFavorite(userId: Int, hotelId: Int): Result<Boolean> =
        favoriteApi.addFavorite(userId, hotelId).map { it.isFavorite }

    override suspend fun removeFavorite(userId: Int, hotelId: Int): Result<Boolean> =
        favoriteApi.removeFavorite(userId, hotelId).map { !it.isFavorite }

    override suspend fun getFavoriteIds(userId: Int): Result<List<Int>> =
        favoriteApi.getFavoriteIds(userId)
}