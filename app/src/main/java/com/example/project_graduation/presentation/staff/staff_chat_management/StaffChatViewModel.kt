package com.example.project_graduation.presentation.staff.staff_chat_management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.remote.api.StaffApi
import com.example.project_graduation.data.remote.dto.StaffConversationDto
import com.example.project_graduation.data.remote.dto.StaffMessageDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ─── Models ─────────────────────────────────────────────────────────────────

//data class CustomerConversation(
//    val conversationId: String,
//    val userId: Int,
//    val userName: String,
//    val userAvatar: String? = null,
//    val lastMessage: String,
//    val lastMessageTime: Long,
//    val unreadCount: Int = 0,
//    val isOnline: Boolean = false,
//    val bookingId: String? = null,
//    val bookingStatus: String? = null
//)
//
//data class StaffChatMessage(
//    val messageId: String = java.util.UUID.randomUUID().toString(),
//    val senderId: String,
//    val senderName: String,
//    val message: String,
//    val timestamp: Long = System.currentTimeMillis(),
//    val isFromStaff: Boolean = false,
//    val status: StaffMessageStatus = StaffMessageStatus.SENT
//)

data class CustomerConversation(
    val conversationId : Int,
    val userId         : Int,
    val userName       : String,
    val guestPhone     : String?,
    val staffUserId    : Int,
    val lastMessage    : String?,
    val lastMessageTime: String?,
    val unreadCount    : Int,
    val isOnline       : Boolean = false
)

data class StaffChatMessage(
    val messageId  : Int,
    val senderId   : Int,
    val senderName : String,
    val messageText: String,
    val isFromStaff: Boolean,
    val isRead     : Boolean,
    val createdAt  : String,
    // local-only — hiển thị trạng thái gửi tạm thời
    val isSending  : Boolean = false
)

enum class StaffMessageStatus { SENDING, SENT, DELIVERED, READ }

// ─── ViewModel ─────────────────────────────────────────────────────────────

class StaffChatViewModel(
    private val staffApi: StaffApi
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<CustomerConversation>>(emptyList())
    val conversations: StateFlow<List<CustomerConversation>> = _conversations.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<StaffChatMessage>>(emptyList())
    val currentMessages: StateFlow<List<StaffChatMessage>> = _currentMessages.asStateFlow()

    private val _currentConversation = MutableStateFlow<CustomerConversation?>(null)
    val currentConversation: StateFlow<CustomerConversation?> = _currentConversation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    var currentStaffName: String = "Staff"
        private set
    private var currentStaffId: Int = 0
    private var currentHotelId: Int = 0

    // ─── Init ─────────────────────────────────────────────────────────────
    fun init(staffId: Int, staffName: String, hotelId: Int) {
        currentStaffId = staffId
        currentStaffName = staffName
        currentHotelId = hotelId
        loadConversations()
    }

    fun loadConversations() {
        if (currentHotelId == 0) return
        viewModelScope.launch {
            _isLoading.value = true
            staffApi.getConversations(currentHotelId)
                .onSuccess { dtos -> _conversations.value = dtos.map { it.toDomain() } }
                .onFailure { e -> _error.value = "Không tải được conversations: ${e.message}" }
            _isLoading.value = false
        }
    }

    // ─── Mở conversation + load messages ──────────────────────────────────
    fun openConversation(conversation: CustomerConversation) {
        _currentConversation.value = conversation
        viewModelScope.launch {
            staffApi.getMessages(conversation.conversationId)
                .onSuccess { dtos ->
                    _currentMessages.value = dtos.map { it.toDomain(currentStaffId) }
                    // Mark as read
                    staffApi.markAsRead(conversation.conversationId, currentStaffId)
                    // Reset unread count local
                    _conversations.value = _conversations.value.map {
                        if (it.conversationId == conversation.conversationId) it.copy(unreadCount = 0) else it
                    }
                }
                .onFailure { e -> _error.value = "Không tải được messages: ${e.message}" }
        }
    }

    fun closeConversation() {
        _currentConversation.value = null
        _currentMessages.value = emptyList()
    }

    // ─── Gửi message ──────────────────────────────────────────────────────
    fun sendMessage(text: String) {
        val conv = _currentConversation.value ?: return
        viewModelScope.launch {
            // Hiển thị tạm thời ngay lập tức (optimistic update)
            val tempMsg = StaffChatMessage(
                messageId = -1,
                senderId = currentStaffId,
                senderName = currentStaffName,
                messageText = text,
                isFromStaff = true,
                isRead = false,
                createdAt = java.time.LocalDateTime.now().toString(),
                isSending = true
            )
            _currentMessages.value = _currentMessages.value + tempMsg

            staffApi.sendMessage(conv.conversationId, currentStaffId, text)
                .onSuccess { msgId ->
                    // Thay temp message bằng message thật
                    _currentMessages.value = _currentMessages.value.map {
                        if (it.messageId == -1 && it.isSending) it.copy(
                            messageId = msgId,
                            isSending = false
                        )
                        else it
                    }
                    // Cập nhật lastMessage trong conversation list
                    _conversations.value = _conversations.value.map {
                        if (it.conversationId == conv.conversationId)
                            it.copy(lastMessage = text, lastMessageTime = "Vừa xong")
                        else it
                    }
                }
                .onFailure { e ->
                    // Xóa temp message nếu gửi thất bại
                    _currentMessages.value = _currentMessages.value.filter { it.messageId != -1 }
                    _error.value = "Gửi thất bại: ${e.message}"
                }
        }
    }

    fun getTotalUnread(): Int = _conversations.value.sumOf { it.unreadCount }
    fun clearError() {
        _error.value = null
    }
}

// ─── Mappers ──────────────────────────────────────────────────────────────────
private fun StaffConversationDto.toDomain() = CustomerConversation(
    conversationId = conversationId,
    userId = userId,
    userName = guestName,
    guestPhone = guestPhone,
    staffUserId = staffUserId,
    lastMessage = lastMessage,
    lastMessageTime = lastMessageTime,
    unreadCount = unreadCount
)

private fun StaffMessageDto.toDomain(currentStaffId: Int) = StaffChatMessage(
    messageId = messageId,
    senderId = senderId,
    senderName = senderName,
    messageText = messageText,
    isFromStaff = senderRole == "STAFF" || senderId == currentStaffId,
    isRead = isRead,
    createdAt = createdAt
)

// staffId và staffName được set khi init
//    private var currentStaffId: Int = 0
//    private var currentStaffName: String = "Staff"

//    fun init(staffId: Int, staffName: String, hotelId: Int) {
//        currentStaffId = staffId
//        currentStaffName = staffName
//        loadConversations(hotelId)
//    }
//
//    fun loadConversations(hotelId: Int) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            // TODO: conversationRepository.getByHotel(hotelId)
//            _conversations.value = getSampleConversations()
//            _isLoading.value = false
//        }
//    }
//
//    fun openConversation(conversation: CustomerConversation) {
//        viewModelScope.launch {
//            _currentConversation.value = conversation
//            // TODO: messageRepository.getByConversation(conversation.conversationId)
//            _currentMessages.value = getSampleMessages(conversation.conversationId)
//            _conversations.value = _conversations.value.map {
//                if (it.conversationId == conversation.conversationId) it.copy(unreadCount = 0) else it
//            }
//        }
//    }
//
//    fun closeConversation() {
//        _currentConversation.value = null
//        _currentMessages.value = emptyList()
//    }
//
//    fun sendMessage(text: String) {
//        viewModelScope.launch {
//            val conv = _currentConversation.value ?: return@launch
//            val msg = StaffChatMessage(
//                senderId = "staff_$currentStaffId",
//                senderName = currentStaffName,
//                message = text,
//                isFromStaff = true,
//                status = StaffMessageStatus.SENDING
//            )
//            _currentMessages.value = _currentMessages.value + msg
//            _conversations.value = _conversations.value.map {
//                if (it.conversationId == conv.conversationId)
//                    it.copy(lastMessage = text, lastMessageTime = System.currentTimeMillis())
//                else it
//            }
//            kotlinx.coroutines.delay(1000)
//            _currentMessages.value = _currentMessages.value.map { m ->
//                if (m.messageId == msg.messageId) m.copy(status = StaffMessageStatus.SENT) else m
//            }
//        }
//    }
//
//    fun getTotalUnread() = _conversations.value.sumOf { it.unreadCount }
//}
//
//// ─── Sample Data ─────────────────────────────────────────────────────────
//
//fun getSampleConversations(): List<CustomerConversation> {
//    val now = System.currentTimeMillis()
//    return listOf(
//        CustomerConversation(
//            "conv1",
//            101,
//            "Nguyen Thi Lan",
//            null,
//            "Cho tôi hỏi giờ breakfast?",
//            now - 300000,
//            2,
//            true,
//            "#BK2601",
//            "CONFIRMED"
//        ),
//        CustomerConversation(
//            "conv2",
//            102,
//            "Tran Van Minh",
//            null,
//            "Room service xin chào, cảm ơn!",
//            now - 1800000,
//            0,
//            true,
//            "#BK2602",
//            "CHECKED_IN"
//        ),
//        CustomerConversation(
//            "conv3",
//            103,
//            "Hoang My Linh",
//            null,
//            "Bạn có thể giúp mình đặt taxi?",
//            now - 7200000,
//            1,
//            false,
//            "#BK2605",
//            "CONFIRMED"
//        ),
//        CustomerConversation(
//            "conv4",
//            104,
//            "Le Van Cuong",
//            null,
//            "Phòng của tôi còn trống không?",
//            now - 86400000,
//            0,
//            false,
//            null,
//            null
//        ),
//    )
//}
//
//fun getSampleMessages(conversationId: String): List<StaffChatMessage> {
//    val now = System.currentTimeMillis()
//    return when (conversationId) {
//        "conv1" -> listOf(
//            StaffChatMessage(
//                senderId = "user_101",
//                senderName = "Nguyen Thi Lan",
//                message = "Xin chào! Breakfast là mấy giờ ạ?",
//                timestamp = now - 900000,
//                isFromStaff = false
//            ),
//            StaffChatMessage(
//                senderId = "staff_1",
//                senderName = "Staff",
//                message = "Breakfast từ 6:30 - 10:00 sáng tại tầng 2 ạ.",
//                timestamp = now - 600000,
//                isFromStaff = true,
//                status = StaffMessageStatus.READ
//            ),
//            StaffChatMessage(
//                senderId = "user_101",
//                senderName = "Nguyen Thi Lan",
//                message = "Cho tôi hỏi giờ breakfast là mấy giờ ạ?",
//                timestamp = now - 300000,
//                isFromStaff = false
//            ),
//        )
//
//        "conv2" -> listOf(
//            StaffChatMessage(
//                senderId = "user_102",
//                senderName = "Tran Van Minh",
//                message = "Cho tôi order room service được không?",
//                timestamp = now - 3600000,
//                isFromStaff = false
//            ),
//            StaffChatMessage(
//                senderId = "staff_1",
//                senderName = "Staff",
//                message = "Dạ được ạ! Anh muốn gọi món gì?",
//                timestamp = now - 3500000,
//                isFromStaff = true,
//                status = StaffMessageStatus.READ
//            ),
//            StaffChatMessage(
//                senderId = "user_102",
//                senderName = "Tran Van Minh",
//                message = "Room service xin chào, cảm ơn!",
//                timestamp = now - 1800000,
//                isFromStaff = false
//            ),
//        )
//
//        "conv3" -> listOf(
//            StaffChatMessage(
//                senderId = "user_103",
//                senderName = "Hoang My Linh",
//                message = "Bạn có thể giúp mình đặt taxi ra sân bay không?",
//                timestamp = now - 7200000,
//                isFromStaff = false
//            ),
//        )
//
//        else -> listOf(
//            StaffChatMessage(
//                senderId = "user_104",
//                senderName = "Le Van Cuong",
//                message = "Phòng của tôi còn trống không?",
//                timestamp = now - 86400000,
//                isFromStaff = false
//            ),
//            StaffChatMessage(
//                senderId = "staff_1",
//                senderName = "Staff",
//                message = "Anh có thể cho biết ngày check-in dự kiến không ạ?",
//                timestamp = now - 86300000,
//                isFromStaff = true,
//                status = StaffMessageStatus.READ
//            ),
//        )
//    }
//}