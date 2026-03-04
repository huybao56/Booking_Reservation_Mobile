package com.example.project_graduation.domain.model

data class BookingSession(
    val sessionId: String,
    val userId: Int,
    val status: SessionStatus,
    val expiresAt: String,
    val createdAt: String
)

enum class SessionStatus {
    ACTIVE, EXPIRED, COMPLETED
}