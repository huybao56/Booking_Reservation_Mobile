package com.example.project_graduation.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

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

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Gọi API để lấy danh sách conversations
                // Hiện tại dùng mock data
                _conversations.value = getSampleConversations()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
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

    fun sendMessage(conversationId: String, messageText: String) {
        viewModelScope.launch {
            try {
                // TODO: Gọi API để gửi message
                // Hiện tại chỉ add vào list local
                val newMessage = ChatMessage(
                    senderId = "current_user",
                    senderName = "You",
                    message = messageText,
                    timestamp = System.currentTimeMillis(),
                    isFromCurrentUser = true,
                    status = MessageStatus.SENDING
                )

                _currentMessages.value = _currentMessages.value + newMessage

                // Update last message in conversation
                updateLastMessage(conversationId, messageText)

                // Simulate sending and receiving status updates
                simulateMessageStatusUpdates(newMessage)

                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

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