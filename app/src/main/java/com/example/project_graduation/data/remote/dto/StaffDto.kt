package com.example.project_graduation.data.remote.dto

// ─── Booking ──────────────────────────────────────────────────────────────────
data class StaffBookingDto(
    val bookingId: Int,
    val bookingGroupId: String,
    val guestName: String,
    val guestPhone: String?,
    val roomId: Int,
    val roomNumber: String,
    val roomType: String,
    val floor: Int,
    val checkIn: String,   // "dd/MM/yyyy" từ backend
    val checkOut: String,
    val status: String,
    val totalAmount: Double,
    val guests: Int,
    val specialRequests: String?
)

// ─── Room ─────────────────────────────────────────────────────────────────────
data class StaffRoomDto(
    val roomId: Int,
    val roomNumber: String,
    val roomType: String,
    val floor: Int,
    val status: String,
    val basePrice: Double,
    val capacity: Int,
    val currentGuest: String?,
    val checkOutDate: String?
)

// ─── Dashboard ────────────────────────────────────────────────────────────────
data class StaffDashboardStatsDto(
    val todayCheckIns: Int,
    val todayCheckOuts: Int,
    val availableRooms: Int,
    val occupiedRooms: Int,
    val maintenanceRooms: Int,
    val pendingBookings: Int,
    val totalRevenue: Double
)

// ─── Conversation ─────────────────────────────────────────────────────────────
data class StaffConversationDto(
    val conversationId: Int,
    val userId: Int,
    val guestName: String,
    val guestPhone: String?,
    val staffUserId: Int,
    val lastMessage: String?,
    val lastMessageTime: String?,
    val unreadCount: Int
)

// ─── Message ──────────────────────────────────────────────────────────────────
data class StaffMessageDto(
    val messageId: Int,
    val senderId: Int,
    val senderName: String,
    val senderRole: String,   // "USER" hoặc "STAFF"
    val messageText: String,
    val isRead: Boolean,
    val createdAt: String    // "yyyy-MM-dd HH:mm:ss"
)