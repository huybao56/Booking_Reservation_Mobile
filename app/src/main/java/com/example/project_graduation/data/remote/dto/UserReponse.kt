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

@Serializable
data class UserConversationDto(
    val conversationId  : Int,
    val userId          : Int,
    val hotelId         : Int,        // ← thêm so với StaffConversationDto
    val hotelName       : String,     // ← tên hotel hiển thị cho user
    val staffUserId     : Int,
    val lastMessage     : String?,
    val lastMessageTime : String?,    // "yyyy-MM-dd HH:mm:ss"
    val unreadCount     : Int,
    val isOnline        : Boolean = false
)

@Serializable
data class UserMessageDto(
    val messageId   : Int,
    val senderId    : Int,
    val senderName  : String,
    val senderRole  : String,         // "USER" hoặc "STAFF"
    val messageText : String,
    val isRead      : Boolean,
    val createdAt   : String          // "yyyy-MM-dd HH:mm:ss"
)