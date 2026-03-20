package com.example.project_graduation.domain.repository

import com.example.project_graduation.domain.model.User

interface UserRepository {
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getUserById(userId: Int): Result<User>

    // Update profile — gọi API và lưu local
    suspend fun updateUser(
        userId: Int,
        token: String,
        username: String,
        email: String,
        phone: String?,
        currentPassword: String? = null,
        newPassword: String? = null
    ): Result<User>

    // TODO: Add later for CRUD
    // suspend fun createUser(user: User): Result<User>
    // suspend fun updateUser(user: User): Result<User>
    // suspend fun deleteUser(userId: Int): Result<Unit>
}