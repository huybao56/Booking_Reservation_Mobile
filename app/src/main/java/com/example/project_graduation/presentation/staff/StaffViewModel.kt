package com.example.project_graduation.presentation.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ============= DATA MODELS =============

data class StaffInfo(
    val staffId: Int,
    val hotelId: Int,
    val hotelName: String,
    val staffName: String,
    val role: String = "Hotel Staff",
    val email: String = "",
    val phone: String = ""
)

data class StaffBooking(
    val bookingId: String,
    val guestName: String,
    val roomNumber: String,
    val roomType: String,
    val checkIn: String,
    val checkOut: String,
    val status: String,       // PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    val totalAmount: Double,
    val guests: Int = 1,
    val specialRequests: String? = null
)

data class StaffRoom(
    val roomId: Int,
    val roomNumber: String,
    val roomType: String,
    val floor: Int,
    val status: String,       // AVAILABLE, OCCUPIED, MAINTENANCE
    val basePrice: Double,
    val capacity: Int,
    val currentGuest: String? = null,   // tên khách đang ở nếu OCCUPIED
    val checkOutDate: String? = null
)

data class StaffDashboardStats(
    val todayCheckIns: Int = 0,
    val todayCheckOuts: Int = 0,
    val availableRooms: Int = 0,
    val occupiedRooms: Int = 0,
    val maintenanceRooms: Int = 0,
    val pendingBookings: Int = 0,
    val totalRevenue: Double = 0.0
)

// ============= CHAT DATA MODELS =============

data class CustomerConversation(
    val conversationId: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String? = null,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val bookingId: String? = null,    // Liên kết với booking nếu có
    val bookingStatus: String? = null
)

data class StaffChatMessage(
    val messageId: String = java.util.UUID.randomUUID().toString(),
    val senderId: String,        // "staff" hoặc userId của user
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromStaff: Boolean = false,
    val status: StaffMessageStatus = StaffMessageStatus.SENT
)

enum class StaffMessageStatus {
    SENDING, SENT, DELIVERED, READ
}


// ============= VIEW MODEL =============

class StaffViewModel : ViewModel() {

    // Staff info — trong thực tế sẽ lấy từ PreferencesManager sau khi login
    private val _staffInfo = MutableStateFlow(
        StaffInfo(
            staffId = 1,
            hotelId = 3,
            hotelName = "Grand Palace Hotel",
            staffName = "Nguyen Van A",
            role = "Front Desk",
            email = "staff@grandpalace.com",
            phone = "+84 901 234 567"
        )
    )
    val staffInfo: StateFlow<StaffInfo> = _staffInfo.asStateFlow()

    // Dashboard Stats
    private val _dashboardStats = MutableStateFlow(StaffDashboardStats())
    val dashboardStats: StateFlow<StaffDashboardStats> = _dashboardStats.asStateFlow()

    // Bookings
    private val _bookings = MutableStateFlow<List<StaffBooking>>(emptyList())
    val bookings: StateFlow<List<StaffBooking>> = _bookings.asStateFlow()

    // Rooms
    private val _rooms = MutableStateFlow<List<StaffRoom>>(emptyList())
    val rooms: StateFlow<List<StaffRoom>> = _rooms.asStateFlow()

    // Chat conversations
    private val _conversations = MutableStateFlow<List<CustomerConversation>>(emptyList())
    val conversations: StateFlow<List<CustomerConversation>> = _conversations.asStateFlow()

    // Current chat messages
    private val _currentMessages = MutableStateFlow<List<StaffChatMessage>>(emptyList())
    val currentMessages: StateFlow<List<StaffChatMessage>> = _currentMessages.asStateFlow()

    private val _currentConversation = MutableStateFlow<CustomerConversation?>(null)
    val currentConversation: StateFlow<CustomerConversation?> = _currentConversation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    init {
        loadAllData()
    }

    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            // TODO: Call real APIs here
            // bookingRepository.getBookingsByHotel(staffInfo.hotelId)
            // roomRepository.getRoomsByHotel(staffInfo.hotelId)

            // Mock data
            _bookings.value = getSampleStaffBookings()
            _rooms.value = getSampleStaffRooms()
            _conversations.value = getSampleCustomerConversations()
            updateDashboardStats()
            _isLoading.value = false
        }
    }

    private fun updateDashboardStats() {
        val bookingList = _bookings.value
        val roomList = _rooms.value
        _dashboardStats.value = StaffDashboardStats(
            todayCheckIns = bookingList.count { it.status == "CONFIRMED" || it.status == "CHECKED_IN" },
            todayCheckOuts = bookingList.count { it.status == "CHECKED_OUT" },
            availableRooms = roomList.count { it.status == "AVAILABLE" },
            occupiedRooms = roomList.count { it.status == "OCCUPIED" },
            maintenanceRooms = roomList.count { it.status == "MAINTENANCE" },
            pendingBookings = bookingList.count { it.status == "PENDING" },
            totalRevenue = bookingList.filter { it.status != "CANCELLED" }.sumOf { it.totalAmount }
        )
    }

    // ---- Booking actions ----
    fun updateBookingStatus(bookingId: String, newStatus: String) {
        viewModelScope.launch {
            // TODO: bookingRepository.updateStatus(bookingId, newStatus)
            _bookings.value = _bookings.value.map { b ->
                if (b.bookingId == bookingId) b.copy(status = newStatus) else b
            }
            updateDashboardStats()
            _operationSuccess.value = "Booking $bookingId updated to $newStatus"
        }
    }

    // ---- Room actions ----
    fun updateRoomStatus(roomId: Int, newStatus: String) {
        viewModelScope.launch {
            // TODO: roomRepository.updateStatus(roomId, newStatus)
            _rooms.value = _rooms.value.map { r ->
                if (r.roomId == roomId) r.copy(status = newStatus) else r
            }
            updateDashboardStats()
            _operationSuccess.value = "Room status updated to $newStatus"
        }
    }

    // ---- Chat actions ----
    fun openConversation(conversation: CustomerConversation) {
        viewModelScope.launch {
            _currentConversation.value = conversation
            // TODO: Fetch real messages from API
            _currentMessages.value = getSampleStaffMessages(conversation.conversationId)
            // Mark as read
            _conversations.value = _conversations.value.map {
                if (it.conversationId == conversation.conversationId) it.copy(unreadCount = 0) else it
            }
        }
    }

    fun closeConversation() {
        _currentConversation.value = null
        _currentMessages.value = emptyList()
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val conv = _currentConversation.value ?: return@launch
            val msg = StaffChatMessage(
                senderId = "staff_${_staffInfo.value.staffId}",
                senderName = _staffInfo.value.staffName,
                message = text,
                isFromStaff = true,
                status = StaffMessageStatus.SENDING
            )
            _currentMessages.value = _currentMessages.value + msg

            // Update last message in conversation
            _conversations.value = _conversations.value.map {
                if (it.conversationId == conv.conversationId)
                    it.copy(lastMessage = text, lastMessageTime = System.currentTimeMillis())
                else it
            }

            // Simulate SENT after 1s
            kotlinx.coroutines.delay(1000)
            updateMessageStatus(msg.messageId, StaffMessageStatus.SENT)
        }
    }

    private fun updateMessageStatus(messageId: String, status: StaffMessageStatus) {
        _currentMessages.value = _currentMessages.value.map { m ->
            if (m.messageId == messageId) m.copy(status = status) else m
        }
    }

    fun getTotalUnreadCount() = _conversations.value.sumOf { it.unreadCount }

    fun clearSuccess() { _operationSuccess.value = null }
    fun clearError() { _error.value = null }
}


// ============= SAMPLE DATA =============

fun getSampleStaffBookings(): List<StaffBooking> {
    return listOf(
        StaffBooking("#BK2601", "Nguyen Thi Lan", "101", "Deluxe Room", "25/02/2026", "27/02/2026", "CONFIRMED", 1500000.0, 2),
        StaffBooking("#BK2602", "Tran Van Minh", "205", "Suite", "25/02/2026", "01/03/2026", "CHECKED_IN", 4500000.0, 3, "Early check-in requested"),
        StaffBooking("#BK2603", "Le Thi Hoa", "310", "Standard", "25/02/2026", "26/02/2026", "PENDING", 800000.0, 1),
        StaffBooking("#BK2604", "Pham Duc Anh", "402", "Deluxe Room", "24/02/2026", "25/02/2026", "CHECKED_OUT", 1500000.0, 2),
        StaffBooking("#BK2605", "Hoang My Linh", "112", "Twin Room", "26/02/2026", "28/02/2026", "CONFIRMED", 1200000.0, 2),
        StaffBooking("#BK2606", "Do Van Tuan", "301", "Standard", "23/02/2026", "25/02/2026", "CANCELLED", 800000.0, 1),
        StaffBooking("#BK2607", "Vo Thi Thu", "220", "Junior Suite", "25/02/2026", "02/03/2026", "CONFIRMED", 3500000.0, 2, "Anniversary decoration"),
    )
}

fun getSampleStaffRooms(): List<StaffRoom> {
    return listOf(
        StaffRoom(1, "101", "Deluxe Room", 1, "OCCUPIED", 750000.0, 2, "Nguyen Thi Lan", "27/02/2026"),
        StaffRoom(2, "102", "Standard", 1, "AVAILABLE", 400000.0, 2),
        StaffRoom(3, "103", "Standard", 1, "MAINTENANCE", 400000.0, 2),
        StaffRoom(4, "201", "Twin Room", 2, "AVAILABLE", 600000.0, 2),
        StaffRoom(5, "205", "Suite", 2, "OCCUPIED", 1500000.0, 4, "Tran Van Minh", "01/03/2026"),
        StaffRoom(6, "210", "Deluxe Room", 2, "AVAILABLE", 750000.0, 2),
        StaffRoom(7, "301", "Standard", 3, "AVAILABLE", 400000.0, 2),
        StaffRoom(8, "310", "Standard", 3, "AVAILABLE", 400000.0, 2),
        StaffRoom(9, "401", "Junior Suite", 4, "AVAILABLE", 1100000.0, 3),
        StaffRoom(10, "402", "Deluxe Room", 4, "AVAILABLE", 750000.0, 2),
    )
}

fun getSampleCustomerConversations(): List<CustomerConversation> {
    val now = System.currentTimeMillis()
    return listOf(
        CustomerConversation("conv1", 101, "Nguyen Thi Lan", null, "Cho tôi hỏi giờ breakfast là mấy giờ ạ?", now - 300000, 2, true, "#BK2601", "CONFIRMED"),
        CustomerConversation("conv2", 102, "Tran Van Minh", null, "Room service xin chào, cảm ơn!", now - 1800000, 0, true, "#BK2602", "CHECKED_IN"),
        CustomerConversation("conv3", 103, "Hoang My Linh", null, "Bạn có thể giúp mình đặt taxi không?", now - 7200000, 1, false, "#BK2605", "CONFIRMED"),
        CustomerConversation("conv4", 104, "Le Van Cuong", null, "Phòng của tôi còn trống không?", now - 86400000, 0, false, null, null),
    )
}

fun getSampleStaffMessages(conversationId: String): List<StaffChatMessage> {
    val now = System.currentTimeMillis()
    return when (conversationId) {
        "conv1" -> listOf(
            StaffChatMessage(senderId = "user_101", senderName = "Nguyen Thi Lan", message = "Xin chào! Tôi có thể hỏi giờ breakfast là mấy giờ ạ?", timestamp = now - 900000, isFromStaff = false),
            StaffChatMessage(senderId = "staff_1", senderName = "Staff", message = "Xin chào chị Lan! Breakfast phục vụ từ 6:30 - 10:00 sáng tại tầng 2 ạ.", timestamp = now - 600000, isFromStaff = true, status = StaffMessageStatus.READ),
            StaffChatMessage(senderId = "user_101", senderName = "Nguyen Thi Lan", message = "Có buffet không ạ?", timestamp = now - 500000, isFromStaff = false),
            StaffChatMessage(senderId = "staff_1", senderName = "Staff", message = "Dạ có ạ! Buffet đầy đủ từ Á đến Âu, bao gồm cả trứng, bánh mì, ngũ cốc và nhiều loại salad.", timestamp = now - 400000, isFromStaff = true, status = StaffMessageStatus.READ),
            StaffChatMessage(senderId = "user_101", senderName = "Nguyen Thi Lan", message = "Cho tôi hỏi giờ breakfast là mấy giờ ạ?", timestamp = now - 300000, isFromStaff = false),
        )
        "conv2" -> listOf(
            StaffChatMessage(senderId = "user_102", senderName = "Tran Van Minh", message = "Cho tôi order room service được không?", timestamp = now - 3600000, isFromStaff = false),
            StaffChatMessage(senderId = "staff_1", senderName = "Staff", message = "Dạ được ạ! Anh muốn gọi món gì ạ? Menu room service: https://grandpalace.com/menu", timestamp = now - 3500000, isFromStaff = true, status = StaffMessageStatus.READ),
            StaffChatMessage(senderId = "user_102", senderName = "Tran Van Minh", message = "Cho 2 phần cơm chiên hải sản và 2 lon Pepsi", timestamp = now - 2000000, isFromStaff = false),
            StaffChatMessage(senderId = "staff_1", senderName = "Staff", message = "Dạ! Khoảng 20-30 phút sẽ có người mang lên phòng 205 cho anh ạ. Tổng: 380,000 VNĐ.", timestamp = now - 1900000, isFromStaff = true, status = StaffMessageStatus.READ),
            StaffChatMessage(senderId = "user_102", senderName = "Tran Van Minh", message = "Room service xin chào, cảm ơn!", timestamp = now - 1800000, isFromStaff = false),
        )
        "conv3" -> listOf(
            StaffChatMessage(senderId = "user_103", senderName = "Hoang My Linh", message = "Bạn có thể giúp mình đặt taxi ra sân bay không?", timestamp = now - 7200000, isFromStaff = false),
        )
        else -> listOf(
            StaffChatMessage(senderId = "user_104", senderName = "Le Van Cuong", message = "Phòng của tôi còn trống không?", timestamp = now - 86400000, isFromStaff = false),
            StaffChatMessage(senderId = "staff_1", senderName = "Staff", message = "Chào anh! Anh có thể cho tôi biết ngày check-in dự kiến không ạ?", timestamp = now - 86300000, isFromStaff = true, status = StaffMessageStatus.READ),
        )
    }
}