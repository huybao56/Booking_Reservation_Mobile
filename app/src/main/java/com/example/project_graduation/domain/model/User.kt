package com.example.project_graduation.domain.model

enum class UserRole {
    USER,
    ADMIN,
    STAFF;

    companion object {
        fun fromString(role: String): UserRole {
            return when (role.uppercase()) {
                "STAFF" -> STAFF
                "ADMIN" -> ADMIN
                else -> USER
            }
        }
    }
}

data class User(
    val userId: Int,
    val username: String,
    val email: String,
    val phone: String?,
    val createdAt: String,
    val role: UserRole,
    val token: String = ""
){
    fun isStaff(): Boolean = role == UserRole.STAFF
    fun isAdmin(): Boolean = role == UserRole.ADMIN
    fun isUser(): Boolean = role == UserRole.USER
}