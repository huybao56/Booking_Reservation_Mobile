package com.example.project_graduation.domain.repository

import com.example.project_graduation.domain.model.User

interface UserRepository {
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getUserById(userId: Int): Result<User>

    // TODO: Add later for CRUD
    // suspend fun createUser(user: User): Result<User>
    // suspend fun updateUser(user: User): Result<User>
    // suspend fun deleteUser(userId: Int): Result<Unit>
}