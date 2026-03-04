package com.example.project_graduation.presentation.chat

//@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Data Models
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromCurrentUser: Boolean = false,
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

data class ChatConversation(
    val id: String,
    val hotelName: String,
    val hotelId: Int,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)


@Composable
fun ChatListScreen(
    conversations: List<ChatConversation> = getSampleConversations(),
    onConversationClick: (ChatConversation) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Messages",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                ),
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF666666)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (conversations.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFFCCCCCC)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No conversation yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Start chatting with hotels",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(paddingValues)
            ) {
                items(conversations) { conversation ->
                    ChatConversationItem(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    onBack: () -> Unit = {}
) {
    // State để track conversation nào đang được chọn
    var selectedConversation by remember { mutableStateOf<ChatConversation?>(null) }

    // State để lưu messages của conversation đang chọn
    var currentMessages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }

    // Nếu chưa chọn conversation nào -> hiển thị ChatListScreen
    if (selectedConversation == null) {
        ChatListScreen(
            conversations = getSampleConversations(),
            onConversationClick = { conversation ->
//                // Khi click vào conversation:
//                // 1. Set conversation đang chọn
//                selectedConversation = conversation
//
//                // 2. Load messages tương ứng với hotel
//                currentMessages = when (conversation.id) {
//                    "1" -> getSampleMessages() // Grand Palace Hotel
//                    "2" -> getSampleMessagesBeachResort()
//                    "3" -> getSampleMessagesMountainLodge()
//                    "4" -> getSampleMessagesCityCenter()
//                    else -> getSampleMessages()
//                }

                // Khi click:
                selectedConversation = conversation // ← Set conversation
                currentMessages = getMessagesByConversationId(conversation.id) // ← Load messages
            },
            onBack = onBack
        )
    } else {
        // Nếu đã chọn conversation -> hiển thị ChatDetailScreen
        ChatDetailScreen(
            conversation = selectedConversation!!,
            messages = currentMessages,
            onBack = {
                // Khi click back -> clear selection và quay về list
                selectedConversation = null
                currentMessages = emptyList()
            },
            onSendMessage = { messageText ->
                // Khi gửi message mới
                val newMessage = ChatMessage(
                    senderId = "current_user",
                    senderName = "You",
                    message = messageText,
                    timestamp = System.currentTimeMillis(),
                    isFromCurrentUser = true,
                    status = MessageStatus.SENDING
                )

                // Thêm message vào list
                currentMessages = currentMessages + newMessage

                // TODO: Gửi message lên server qua API
                // api.sendMessage(selectedConversation!!.id, messageText)
            }
        )
    }
}


@Composable
fun ChatConversationItem(
    conversation: ChatConversation,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online indicator
            Box {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color(0xFF2196F3).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Hotel,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                if (conversation.isOnline) {
                    Surface(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {}
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Conversation Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.hotelName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatTime(conversation.lastMessageTime),
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        fontSize = 14.sp,
                        color = if (conversation.unreadCount > 0) Color(0xFF1A1A1A)
                        else Color(0xFF666666),
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium
                        else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF2196F3)
                        ) {
                            Text(
                                text = conversation.unreadCount.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversation: ChatConversation,
    messages: List<ChatMessage> = getSampleMessages(),
    onBack: () -> Unit = {},
    onSendMessage: (String) -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color(0xFF2196F3).copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Hotel,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                conversation.hotelName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (conversation.isOnline) {
                                Text(
                                    "Online",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                ),
                actions = {
//                    IconButton(onClick = { /* Phone call */ }) {
//                        Icon(
//                            Icons.Default.Phone,
//                            contentDescription = "Call",
//                            tint = Color(0xFF666666)
//                        )
//                    }
                    IconButton(onClick = { /* More options */ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color(0xFF666666)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Attach button
                    IconButton(
                        onClick = { /* Attach file */ },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = "Attach",
                            tint = Color(0xFF666666)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Message input
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Type a message...",
                                color = Color(0xFF999999)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            disabledContainerColor = Color(0xFFF5F5F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Send button
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                onSendMessage(messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (messageText.isNotBlank()) Color(0xFF2196F3)
                                else Color(0xFFE0E0E0)
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            state = listState
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }

            // Add spacing at bottom for better UX
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isFromCurrentUser)
            Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromCurrentUser) {
            // Avatar for hotel messages
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = Color(0xFF2196F3).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Hotel,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (message.isFromCurrentUser)
                Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isFromCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isFromCurrentUser) 4.dp else 16.dp
                ),
                color = if (message.isFromCurrentUser)
                    Color(0xFF2196F3) else Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    if (!message.isFromCurrentUser) {
                        Text(
                            text = message.senderName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = message.message,
                        fontSize = 15.sp,
                        color = if (message.isFromCurrentUser)
                            Color.White else Color(0xFF1A1A1A),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatMessageTime(message.timestamp),
                    fontSize = 11.sp,
                    color = Color(0xFF999999)
                )

                if (message.isFromCurrentUser) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        when (message.status) {
                            MessageStatus.SENDING -> Icons.Default.Schedule
                            MessageStatus.SENT -> Icons.Default.Done
                            MessageStatus.DELIVERED -> Icons.Default.DoneAll
                            MessageStatus.READ -> Icons.Default.DoneAll
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (message.status == MessageStatus.READ)
                            Color(0xFF2196F3) else Color(0xFF999999)
                    )
                }
            }
        }

        if (message.isFromCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp))

            // Avatar for current user
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = Color(0xFF4CAF50).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


private fun getMessagesByConversationId(conversationId: String): List<ChatMessage> {
    return when (conversationId) {
        "1" -> getSampleMessages() // Grand Palace Hotel
        "2" -> getSampleMessagesBeachResort() // Beach Resort
        "3" -> getSampleMessagesMountainLodge() // Mountain Lodge
        "4" -> getSampleMessagesCityCenter() // City Center Inn
        else -> getSampleMessages()
    }
}

// Helper Functions
fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now" // Less than 1 minute
        diff < 3600000 -> "${diff / 60000}m ago" // Less than 1 hour
        diff < 86400000 -> "${diff / 3600000}h ago" // Less than 24 hours
        diff < 172800000 -> "Yesterday" // Less than 2 days
        else -> {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

fun formatMessageTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m"
        diff < 86400000 -> {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
        diff < 172800000 -> "Yesterday"
        else -> {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

// Sample Data
fun getSampleConversations(): List<ChatConversation> {
    val now = System.currentTimeMillis()
    return listOf(
        ChatConversation(
            id = "1",
            hotelName = "Grand Palace Hotel",
            hotelId = 1,
            lastMessage = "Your booking has been confirmed!",
            lastMessageTime = now - 300000, // 5 minutes ago
            unreadCount = 2,
            isOnline = true
        ),
        ChatConversation(
            id = "2",
            hotelName = "Beach Resort Paradise",
            hotelId = 2,
            lastMessage = "Thank you for choosing our resort. How can we help?",
            lastMessageTime = now - 3600000, // 1 hour ago
            unreadCount = 0,
            isOnline = true
        ),
        ChatConversation(
            id = "3",
            hotelName = "Mountain View Lodge",
            hotelId = 3,
            lastMessage = "We look forward to welcoming you!",
            lastMessageTime = now - 86400000, // 1 day ago
            unreadCount = 0,
            isOnline = false
        ),
        ChatConversation(
            id = "4",
            hotelName = "City Center Inn",
            hotelId = 4,
            lastMessage = "Your room upgrade is available",
            lastMessageTime = now - 172800000, // 2 days ago
            unreadCount = 1,
            isOnline = false
        )
    )
}

fun getSampleMessages(): List<ChatMessage> {
    val now = System.currentTimeMillis()
    return listOf(
        ChatMessage(
            senderId = "hotel_1",
            senderName = "Grand Palace Hotel",
            message = "Hello! Welcome to Grand Palace Hotel. How can we assist you today?",
            timestamp = now - 3600000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "Hi! I'd like to know more about the amenities available.",
            timestamp = now - 3540000,
            isFromCurrentUser = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "hotel_1",
            senderName = "Grand Palace Hotel",
            message = "We offer a wide range of amenities including:\n• Free WiFi\n• Swimming Pool\n• Fitness Center\n• Spa Services\n• 24/7 Room Service",
            timestamp = now - 3480000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "That sounds great! What about parking?",
            timestamp = now - 3420000,
            isFromCurrentUser = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "hotel_1",
            senderName = "Grand Palace Hotel",
            message = "Yes, we provide complimentary parking for all our guests. There's both valet and self-parking available.",
            timestamp = now - 3360000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "Perfect! Can I make a reservation for next weekend?",
            timestamp = now - 3300000,
            isFromCurrentUser = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "hotel_1",
            senderName = "Grand Palace Hotel",
            message = "Absolutely! I'll help you with that. What dates are you looking at?",
            timestamp = now - 3240000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "February 7-9, please. Room for 2 adults.",
            timestamp = now - 300000,
            isFromCurrentUser = true,
            status = MessageStatus.DELIVERED
        ),
        ChatMessage(
            senderId = "hotel_1",
            senderName = "Grand Palace Hotel",
            message = "Let me check availability for you...",
            timestamp = now - 120000,
            isFromCurrentUser = false,
            status = MessageStatus.SENT
        )
    )
}

// Sample messages cho Beach Resort
fun getSampleMessagesBeachResort(): List<ChatMessage> {
    val now = System.currentTimeMillis()
    return listOf(
        ChatMessage(
            senderId = "hotel_2",
            senderName = "Beach Resort Paradise",
            message = "Welcome to Beach Resort Paradise! 🌴 How may we help you?",
            timestamp = now - 7200000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "Do you offer water sports activities?",
            timestamp = now - 7100000,
            isFromCurrentUser = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "hotel_2",
            senderName = "Beach Resort Paradise",
            message = "Yes! We offer:\n• Snorkeling\n• Kayaking\n• Jet Skiing\n• Surfing Lessons\n• Diving Tours\n\nAll equipment is included!",
            timestamp = now - 7000000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "Sounds amazing! Are these included in the room rate?",
            timestamp = now - 6900000,
            isFromCurrentUser = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "hotel_2",
            senderName = "Beach Resort Paradise",
            message = "Snorkeling and kayaking are complimentary. Other activities have additional fees. Would you like a detailed price list?",
            timestamp = now - 3600000,
            isFromCurrentUser = false,
            status = MessageStatus.SENT
        )
    )
}

// Sample messages cho Mountain Lodge
fun getSampleMessagesMountainLodge(): List<ChatMessage> {
    val now = System.currentTimeMillis()
    return listOf(
        ChatMessage(
            senderId = "hotel_3",
            senderName = "Mountain View Lodge",
            message = "Hello! Thank you for your interest in Mountain View Lodge. 🏔️",
            timestamp = now - 172800000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "Is there heating in the rooms? I'm visiting in winter.",
            timestamp = now - 172700000,
            isFromCurrentUser = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "hotel_3",
            senderName = "Mountain View Lodge",
            message = "Yes, all rooms have central heating and fireplaces. We also provide extra blankets and hot chocolate service! ☕",
            timestamp = now - 172600000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "Perfect! That's exactly what I need.",
            timestamp = now - 86400000,
            isFromCurrentUser = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "hotel_3",
            senderName = "Mountain View Lodge",
            message = "We look forward to welcoming you! Let us know if you need any other information.",
            timestamp = now - 86300000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        )
    )
}

// Sample messages cho City Center
fun getSampleMessagesCityCenter(): List<ChatMessage> {
    val now = System.currentTimeMillis()
    return listOf(
        ChatMessage(
            senderId = "hotel_4",
            senderName = "City Center Inn",
            message = "Welcome to City Center Inn! How can we assist you?",
            timestamp = now - 600000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "Do you have parking available?",
            timestamp = now - 540000,
            isFromCurrentUser = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "hotel_4",
            senderName = "City Center Inn",
            message = "Yes, we have underground parking with 24/7 security. It's $15 per day.",
            timestamp = now - 480000,
            isFromCurrentUser = false,
            status = MessageStatus.READ
        ),
        ChatMessage(
            senderId = "user_1",
            senderName = "You",
            message = "Great! Is it close to public transportation?",
            timestamp = now - 172800000,
            isFromCurrentUser = true,
            status = MessageStatus.DELIVERED
        )
    )
}


// ============= PREVIEWS =============

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatListScreenPreview() {
    MaterialTheme {
        ChatListScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatListEmptyScreenPreview() {
    MaterialTheme {
        ChatListScreen(conversations = emptyList())
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatDetailScreenPreview() {
    MaterialTheme {
        ChatDetailScreen(
            conversation = ChatConversation(
                id = "1",
                hotelName = "Grand Palace Hotel",
                hotelId = 1,
                lastMessage = "Your booking has been confirmed!",
                lastMessageTime = System.currentTimeMillis(),
                unreadCount = 2,
                isOnline = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageBubblePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MessageBubble(
                message = ChatMessage(
                    senderId = "hotel_1",
                    senderName = "Grand Palace Hotel",
                    message = "Hello! How can we help you today?",
                    timestamp = System.currentTimeMillis(),
                    isFromCurrentUser = false
                )
            )
            MessageBubble(
                message = ChatMessage(
                    senderId = "user_1",
                    senderName = "You",
                    message = "I'd like to know about your amenities",
                    timestamp = System.currentTimeMillis(),
                    isFromCurrentUser = true,
                    status = MessageStatus.READ
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatConversationItemPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8F9FA))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ChatConversationItem(
                conversation = ChatConversation(
                    id = "1",
                    hotelName = "Grand Palace Hotel",
                    hotelId = 1,
                    lastMessage = "Your booking has been confirmed!",
                    lastMessageTime = System.currentTimeMillis() - 300000,
                    unreadCount = 2,
                    isOnline = true
                ),
                onClick = {}
            )
            ChatConversationItem(
                conversation = ChatConversation(
                    id = "2",
                    hotelName = "Beach Resort Paradise",
                    hotelId = 2,
                    lastMessage = "Thank you for choosing our resort",
                    lastMessageTime = System.currentTimeMillis() - 3600000,
                    unreadCount = 0,
                    isOnline = false
                ),
                onClick = {}
            )
        }
    }
}