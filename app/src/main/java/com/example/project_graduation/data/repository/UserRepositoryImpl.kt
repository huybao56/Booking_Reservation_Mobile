package com.example.project_graduation.data.repository

import com.example.project_graduation.data.remote.api.UserApi
import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.model.UserRole
import com.example.project_graduation.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getAllUsers(): Result<List<User>> {
        return userApi.getAllUsers().map { dtoList ->
            dtoList.map { dto ->
                User(
                    userId = dto.userId,
                    username = dto.username,
                    email = dto.email,
                    phone = dto.phone,
                    createdAt = dto.createdAt,
                    role = UserRole.fromString(dto.role),
                    token = ""
                )
            }
        }
    }

    override suspend fun getUserById(userId: Int): Result<User> {
        return userApi.getUserById(userId).map { dto ->
            User(
                userId = dto.userId,
                username = dto.username,
                email = dto.email,
                phone = dto.phone,
                createdAt = dto.createdAt,
                role = UserRole.fromString(dto.role),
                token = ""
            )
        }
    }
}