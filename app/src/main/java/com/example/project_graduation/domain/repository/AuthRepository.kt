package com.example.project_graduation.domain.repository

import com.example.project_graduation.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Pair<String, User>>
    suspend fun register(
        username: String,
        email: String,
        password: String,
        phone: String?
    ): Result<Pair<String, User>>

    suspend fun saveUser(token: String, user: User)
    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}