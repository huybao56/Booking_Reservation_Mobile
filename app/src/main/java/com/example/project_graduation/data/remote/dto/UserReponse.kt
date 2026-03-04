package com.example.project_graduation.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserDto?,
    val staffInfo: StaffInfoDto? = null
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone: String? = null
)

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserDto?
)

@Serializable
data class UserDto(
    val userId: Int,
    val username: String,
    val email: String,
    val phone: String?,
    val createdAt: String,
    val role: String
)

@Serializable
data class StaffInfoDto(
    val staffId  : Int,
    val hotelId  : Int,
    val hotelName: String,
    val position : String,
    val canChat  : Boolean
)