package com.example.project_graduation.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.remote.api.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID


class ChatViewModel(
    private val userApi: UserApi
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessage>> = _currentMessages.asStateFlow()

    private val _currentConversation = MutableStateFlow<ChatConversation?>(null)
    val currentConversation: StateFlow<ChatConversation?> = _currentConversation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    var currentUserId   : Int    = 0    ; private set
    var currentUserName : String = "You"; private set

    // Gọi từ NavGraph ngay sau khi login thành công
    fun init(userId: Int, userName: String) {
        Log.d("ChatVM", "▶ init userId=$userId userName=$userName")
        currentUserId   = userId
        currentUserName = userName
        loadConversations()
    }

//    init {
//        loadConversations()
//    }

    fun loadConversations() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                // TODO: Gọi API để lấy danh sách conversations
//                // Hiện tại dùng mock data
//                _conversations.value = getSampleConversations()
//                _error.value = null
//            } catch (e: Exception) {
//                _error.value = e.message
//            } finally {
//                _isLoading.value = false
//            }
//        }
        if (currentUserId == 0) return
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("ChatVM", "▶ loadConversations userId=$currentUserId")
            userApi.getMyConversations(currentUserId)
                .onSuccess { dtos ->
                    _conversations.value = dtos.map { dto ->
                        ChatConversation(
                            id = dto.conversationId.toString(), // ← Convert Int to String
                            hotelId = dto.hotelId,
                            hotelName = dto.hotelName,
                            lastMessage = dto.lastMessage ?: "",
                            lastMessageTime = parseTime(dto.lastMessageTime), // ← Convert String to Long
                            unreadCount = dto.unreadCount,
                            isOnline = dto.isOnline
                        )
                    }
                    _error.value = null
                    Log.d("ChatVM", "  ✔ ${dtos.size} conversations")
                }
                .onFailure { e ->
                    _error.value = "Không tải được danh sách chat: ${e.message}"
                    Log.e("ChatVM", "  ✗ ${e.message}")
                }
            _isLoading.value = false
        }
    }
    fun openConversation(conv: ChatConversation) {
        _currentConversation.value = conv
        viewModelScope.launch {
            val convId = conv.id.toIntOrNull() ?: return@launch
            Log.d("ChatVM", "▶ openConversation convId=$convId")
            userApi.getMessages(convId)
                .onSuccess { dtos ->
                    _currentMessages.value = dtos.map { dto ->
                        ChatMessage(
                            id = dto.messageId.toString(), // ← Convert Int to String
                            senderId = dto.senderId.toString(), // ← Convert Int to String
                            senderName = dto.senderName,
                            message = dto.messageText, // ← Map messageText to message
                            timestamp = parseTime(dto.createdAt), // ← Convert String to Long
                            isFromCurrentUser = dto.senderRole == "USER" || dto.senderId == currentUserId,
                            status = if (dto.isRead) MessageStatus.READ else MessageStatus.SENT
                        )
                    }
                    userApi.markAsRead(convId, currentUserId)
                    _conversations.value = _conversations.value.map {
                        if (it.id == conv.id) it.copy(unreadCount = 0) else it
                    }
                    _error.value = null
                    Log.d("ChatVM", "  ✔ ${dtos.size} messages")
                }
                .onFailure { e ->
                    _error.value = "Không tải được tin nhắn: ${e.message}"
                    Log.e("ChatVM", "  ✗ ${e.message}")
                }
        }
    }

    // Gọi từ Hotel Detail page để bắt đầu chat
    fun openOrCreateConversation(hotelId: Int, hotelName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("ChatVM", "▶ openOrCreateConversation hotelId=$hotelId name=$hotelName")
            userApi.createOrGetConversation(currentUserId, hotelId)
                .onSuccess { dto ->
                    val conv = ChatConversation(
                        id = dto.conversationId.toString(),
                        hotelId = dto.hotelId,
                        hotelName = dto.hotelName.ifBlank { hotelName },
                        lastMessage = dto.lastMessage ?: "",
                        lastMessageTime = parseTime(dto.lastMessageTime),
                        unreadCount = dto.unreadCount,
                        isOnline = dto.isOnline
                    )
                    if (_conversations.value.none { it.id == conv.id }) {
                        _conversations.value = _conversations.value + conv
                    }
                    openConversation(conv)
                    Log.d("ChatVM", "  ✔ conversationId=${conv.id}")
                }
                .onFailure { e ->
                    _error.value = "Không thể mở chat: ${e.message}"
                    Log.e("ChatVM", "  ✗ ${e.message}")
                }
            _isLoading.value = false
        }
    }

    fun closeConversation() {
        _currentConversation.value = null
        _currentMessages.value     = emptyList()
    }

    fun sendMessage(text: String) {
//        val conv = _currentConversation.value ?: return
        val conv = _currentConversation.value ?: return
        val convId = conv.id.toIntOrNull() ?: return

        viewModelScope.launch {
            val tempMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderId = currentUserId.toString(),
                senderName = currentUserName,
                message = text,
                timestamp = System.currentTimeMillis(),
                isFromCurrentUser = true,
                status = MessageStatus.SENDING
            )
            _currentMessages.value = _currentMessages.value + tempMsg
            Log.d("ChatVM", "▶ sendMessage convId=${conv.id} text=$text")

            userApi.sendMessage(convId, currentUserId, text)
                .onSuccess { msgId ->
                    // Update temp message with real ID
                    _currentMessages.value = _currentMessages.value.map {
                        if (it.id == tempMsg.id)
                            it.copy(id = msgId.toString(), status = MessageStatus.SENT)
                        else it
                    }
                    // Update conversation last message
                    _conversations.value = _conversations.value.map {
                        if (it.id == conv.id)
                            it.copy(
                                lastMessage = text,
                                lastMessageTime = System.currentTimeMillis()
                            )
                        else it
                    }
                    _error.value = null
                    Log.d("ChatVM", "  ✔ messageId=$msgId")
                }
                .onFailure { e ->
                    // Remove temp message on failure
                    _currentMessages.value = _currentMessages.value.filter { it.id != tempMsg.id }
                    _error.value = "Gửi thất bại: ${e.message}"
                    Log.e("ChatVM", "  ✗ ${e.message}")
                }
        }
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Gọi API để lấy messages của conversation
                // Hiện tại dùng mock data
                val conversation = _conversations.value.find { it.id == conversationId }
                _currentConversation.value = conversation
                _currentMessages.value = getSampleMessages()

                // Mark messages as read
                markAsRead(conversationId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

//    fun sendMessage(conversationId: String, messageText: String) {
//        viewModelScope.launch {
//            try {
//                // TODO: Gọi API để gửi message
//                // Hiện tại chỉ add vào list local
//                val newMessage = ChatMessage(
//                    senderId = "current_user",
//                    senderName = "You",
//                    message = messageText,
//                    timestamp = System.currentTimeMillis(),
//                    isFromCurrentUser = true,
//                    status = MessageStatus.SENDING
//                )
//
//                _currentMessages.value = _currentMessages.value + newMessage
//
//                // Update last message in conversation
//                updateLastMessage(conversationId, messageText)
//
//                // Simulate sending and receiving status updates
//                simulateMessageStatusUpdates(newMessage)
//
//                _error.value = null
//            } catch (e: Exception) {
//                _error.value = e.message
//            }
//        }
//    }

    private fun markAsRead(conversationId: String) {
        viewModelScope.launch {
            // TODO: Gọi API để mark as read
            // Update local state
            _conversations.value = _conversations.value.map { conversation ->
                if (conversation.id == conversationId) {
                    conversation.copy(unreadCount = 0)
                } else {
                    conversation
                }
            }
        }
    }

    private fun updateLastMessage(conversationId: String, message: String) {
        viewModelScope.launch {
            _conversations.value = _conversations.value.map { conversation ->
                if (conversation.id == conversationId) {
                    conversation.copy(
                        lastMessage = message,
                        lastMessageTime = System.currentTimeMillis()
                    )
                } else {
                    conversation
                }
            }
        }
    }

    private fun simulateMessageStatusUpdates(message: ChatMessage) {
        viewModelScope.launch {
            // Simulate SENT status after 1 second
            kotlinx.coroutines.delay(1000)
            updateMessageStatus(message.id, MessageStatus.SENT)

            // Simulate DELIVERED status after 2 seconds
            kotlinx.coroutines.delay(1000)
            updateMessageStatus(message.id, MessageStatus.DELIVERED)

            // Optionally simulate READ status after 3 seconds
            kotlinx.coroutines.delay(1000)
            updateMessageStatus(message.id, MessageStatus.READ)
        }
    }

    private fun updateMessageStatus(messageId: String, status: MessageStatus) {
        _currentMessages.value = _currentMessages.value.map { message ->
            if (message.id == messageId) {
                message.copy(status = status)
            } else {
                message
            }
        }
    }

    fun getTotalUnreadCount(): Int {
        return _conversations.value.sumOf { it.unreadCount }
    }

    fun searchConversations(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadConversations()
            } else {
                _conversations.value = _conversations.value.filter {
                    it.hotelName.contains(query, ignoreCase = true) ||
                            it.lastMessage.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                // TODO: Gọi API để delete conversation
                _conversations.value = _conversations.value.filter { it.id != conversationId }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Helper function to convert timestamp string to milliseconds
    private fun parseTime(timeStr: String?): Long {
        if (timeStr == null || timeStr.isEmpty()) return System.currentTimeMillis()
        return try {
            // Expected format: "yyyy-MM-dd HH:mm:ss"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.parse(timeStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e("ChatVM", "Error parsing time: $timeStr", e)
            System.currentTimeMillis()
        }
    }

    fun clearError() {
        _error.value = null
    }
}

// State class for UI
data class ChatUiState(
    val conversations: List<ChatConversation> = emptyList(),
    val currentMessages: List<ChatMessage> = emptyList(),
    val currentConversation: ChatConversation? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val unreadCount: Int = 0
)