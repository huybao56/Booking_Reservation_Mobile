package com.example.project_graduation.data.mapper

import com.example.project_graduation.data.remote.dto.RoomDto
import com.example.project_graduation.data.remote.dto.UserDto
import com.example.project_graduation.domain.model.Room
import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.model.UserRole

fun UserDto.toDomain(): User {
    return User(
        userId = userId,
        username = username,
        email = email,
        phone = phone,
        createdAt = createdAt,
        role = UserRole.fromString(role)
    )
}

