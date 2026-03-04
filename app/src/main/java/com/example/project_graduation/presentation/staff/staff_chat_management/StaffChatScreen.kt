package com.example.project_graduation.presentation.staff.staff_chat_management


import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_graduation.presentation.staff.CustomerConversation
import com.example.project_graduation.presentation.staff.StaffChatMessage
import com.example.project_graduation.presentation.staff.StaffMessageStatus
import com.example.project_graduation.presentation.staff.StaffViewModel
import com.example.project_graduation.presentation.staff.getSampleCustomerConversations
import com.example.project_graduation.presentation.staff.getSampleStaffMessages
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ============= Color Palette (Staff = Teal/Green) =============
private val StaffPrimary = Color(0xFF00897B)    // Teal 600
private val StaffPrimaryDark = Color(0xFF00695C) // Teal 800
private val StaffAccent = Color(0xFF4DB6AC)      // Teal 300
private val StaffBg = Color(0xFFF0FAF9)
private val StaffBubble = Color(0xFFE0F2F1)      // Staff chat bubble


// ============= STAFF CHAT SCREEN (entry point) =============

@Composable
fun StaffChatScreen(
    staffViewModel: StaffViewModel,
    onBack: () -> Unit = {}
) {
    val conversations by staffViewModel.conversations.collectAsState()
    val currentConversation by staffViewModel.currentConversation.collectAsState()
    val currentMessages by staffViewModel.currentMessages.collectAsState()

    if (currentConversation == null) {
        // List of customer conversations
        StaffChatListScreen(
            conversations = conversations,
            totalUnread = staffViewModel.getTotalUnreadCount(),
            onConversationClick = { staffViewModel.openConversation(it) },
            onBack = onBack
        )
    } else {
        // Detail screen — chat with specific customer
        StaffChatDetailScreen(
            conversation = currentConversation!!,
            messages = currentMessages,
            staffName = staffViewModel.staffInfo.collectAsState().value.staffName,
            onBack = { staffViewModel.closeConversation() },
            onSendMessage = { text -> staffViewModel.sendMessage(text) }
        )
    }
}

// ============= CHAT LIST SCREEN =============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffChatListScreen(
    conversations: List<CustomerConversation> = getSampleCustomerConversations(),
    totalUnread: Int = 3,
    onConversationClick: (CustomerConversation) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    val filtered = if (searchQuery.isBlank()) conversations
    else conversations.filter {
        it.userName.contains(searchQuery, ignoreCase = true) ||
                it.lastMessage.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm khách hàng...", fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White
                            ),
                            textStyle = TextStyle(color = Color.White, fontSize = 16.sp)
                        )
                    } else {
                        Column {
                            Text("Chat với Khách Hàng", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            if (totalUnread > 0) {
                                Text("$totalUnread tin nhắn chưa đọc", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch; if (!showSearch) searchQuery = "" }) {
                        Icon(
                            if (showSearch) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search", tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StaffPrimary)
            )
        },
        containerColor = StaffBg
    ) { paddingValues ->
        if (filtered.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ChatBubbleOutline, null, Modifier.size(72.dp), tint = Color(0xFFB2DFDB))
                    Spacer(Modifier.height(12.dp))
                    Text("Không có cuộc trò chuyện", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Text("Khách hàng sẽ liên hệ khi cần hỗ trợ", fontSize = 13.sp, color = Color.LightGray)
                }
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filtered) { conv ->
                    StaffConversationItem(conv, onClick = { onConversationClick(conv) })
                }
            }
        }
    }
}

@Composable
fun StaffConversationItem(
    conversation: CustomerConversation,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = if (conversation.unreadCount > 0) 3.dp else 1.dp
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(StaffAccent, StaffPrimary))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        conversation.userName.first().uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                // Online dot
                if (conversation.isOnline) {
                    Box(
                        Modifier
                            .size(13.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            // Name + last message
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        conversation.userName,
                        fontSize = 15.sp,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.weight(1f)
                    )
                    // Booking badge
                    conversation.bookingId?.let { bid ->
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE0F2F1)) {
                            Text(bid, fontSize = 10.sp, color = StaffPrimary, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    conversation.lastMessage,
                    fontSize = 13.sp,
                    color = if (conversation.unreadCount > 0) Color(0xFF424242) else Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                )
                // Booking status hint
                conversation.bookingStatus?.let { status ->
                    Spacer(Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Hotel, null, Modifier.size(11.dp), tint = StaffAccent)
                        Spacer(Modifier.width(3.dp))
                        Text(status, fontSize = 11.sp, color = StaffAccent)
                    }
                }
            }

            // Time + unread badge
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    formatChatTime(conversation.lastMessageTime),
                    fontSize = 11.sp,
                    color = if (conversation.unreadCount > 0) StaffPrimary else Color.Gray
                )
                if (conversation.unreadCount > 0) {
                    Box(
                        Modifier.size(22.dp).clip(CircleShape).background(StaffPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(conversation.unreadCount.toString(), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ============= CHAT DETAIL SCREEN =============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffChatDetailScreen(
    conversation: CustomerConversation,
    messages: List<StaffChatMessage> = emptyList(),
    staffName: String = "Staff",
    onBack: () -> Unit = {},
    onSendMessage: (String) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Quick reply templates for hotel staff
    val quickReplies = listOf(
        "Dạ, chúng tôi sẽ hỗ trợ ngay ạ!",
        "Breakfast: 6:30 - 10:00 tại tầng 2 ạ",
        "Check-out trước 12:00 trưa ạ",
        "Wi-Fi password: GrandPalace2026",
        "Nhân viên sẽ đến phòng trong 10-15 phút ạ"
    )
    var showQuickReplies by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(StaffAccent, StaffPrimary))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(conversation.userName.first().uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            if (conversation.isOnline) {
                                Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF4CAF50)).align(Alignment.BottomEnd))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(conversation.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                if (conversation.isOnline) "Đang hoạt động" else "Ngoại tuyến",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    conversation.bookingId?.let { bid ->
                        TextButton(onClick = {}) {
                            Text(bid, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        }
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StaffPrimary)
            )
        },
        bottomBar = {
            Column {
                // Quick Replies
                AnimatedVisibility(visible = showQuickReplies) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                            .background(Color.White),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(quickReplies) { reply ->
                            Surface(
                                onClick = {
                                    inputText = reply
                                    showQuickReplies = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = StaffBubble
                            ) {
                                Text(
                                    reply,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    fontSize = 13.sp,
                                    color = Color(0xFF00695C)
                                )
                            }
                        }
                    }
                }

                // Input bar
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Quick reply button
                        IconButton(
                            onClick = { showQuickReplies = !showQuickReplies },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.FlashOn,
                                null,
                                tint = if (showQuickReplies) StaffPrimary else Color.Gray
                            )
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Nhập tin nhắn hỗ trợ...", color = Color.Gray, fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StaffPrimary,
                                unfocusedBorderColor = Color(0xFFDDDDDD)
                            )
                        )

                        Spacer(Modifier.width(8.dp))

                        // Send button
                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(inputText.trim())
                                    inputText = ""
                                    showQuickReplies = false
                                }
                            },
                            containerColor = if (inputText.isNotBlank()) StaffPrimary else Color.Gray,
                            modifier = Modifier.size(48.dp),
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        },
        containerColor = StaffBg
    ) { paddingValues ->
        if (messages.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Chat, null, Modifier.size(64.dp), tint = Color(0xFFB2DFDB))
                    Spacer(Modifier.height(12.dp))
                    Text("Bắt đầu hỗ trợ khách hàng", fontSize = 15.sp, color = Color.Gray)
                    Text("Nhập tin nhắn bên dưới", fontSize = 13.sp, color = Color.LightGray)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    StaffMessageBubble(message = msg)
                }
            }
        }
    }
}

@Composable
fun StaffMessageBubble(message: StaffChatMessage) {
    val isStaff = message.isFromStaff

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isStaff) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Customer avatar (left side)
        if (!isStaff) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(StaffAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(message.senderName.first().uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isStaff) Alignment.End else Alignment.Start
        ) {
            if (!isStaff) {
                Text(message.senderName, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp, start = 4.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp, topEnd = 18.dp,
                    bottomStart = if (isStaff) 18.dp else 4.dp,
                    bottomEnd = if (isStaff) 4.dp else 18.dp
                ),
                color = if (isStaff) StaffPrimary else Color.White,
                shadowElevation = 1.dp
            ) {
                Text(
                    message.message,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    fontSize = 14.sp,
                    color = if (isStaff) Color.White else Color(0xFF1A1A1A),
                    lineHeight = 20.sp
                )
            }

            // Time + status
            Row(
                Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    formatTime(message.timestamp),
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                if (isStaff) {
                    when (message.status) {
                        StaffMessageStatus.SENDING -> Icon(Icons.Default.Schedule, null, Modifier.size(12.dp), tint = Color.Gray)
                        StaffMessageStatus.SENT -> Icon(Icons.Default.Check, null, Modifier.size(12.dp), tint = Color.Gray)
                        StaffMessageStatus.DELIVERED -> Icon(Icons.Default.DoneAll, null, Modifier.size(12.dp), tint = Color.Gray)
                        StaffMessageStatus.READ -> Icon(Icons.Default.DoneAll, null, Modifier.size(12.dp), tint = StaffAccent)
                    }
                }
            }
        }

        // Staff avatar (right side)
        if (isStaff) {
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(StaffPrimaryDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SupportAgent, null, Modifier.size(18.dp), tint = Color.White)
            }
        }
    }
}


// ============= HELPERS =============

private fun formatChatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Vừa xong"
        diff < 3_600_000 -> "${diff / 60_000} phút"
        diff < 86_400_000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))


// ============= PREVIEWS =============

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StaffChatListScreenPreview() {
    MaterialTheme {
        StaffChatListScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StaffChatListEmptyPreview() {
    MaterialTheme {
        StaffChatListScreen(conversations = emptyList())
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StaffChatDetailScreenPreview() {
    MaterialTheme {
        StaffChatDetailScreen(
            conversation = getSampleCustomerConversations()[0],
            messages = getSampleStaffMessages("conv1"),
            staffName = "Nguyen Van A"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StaffMessageBubblePreview() {
    MaterialTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .background(StaffBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StaffMessageBubble(
                StaffChatMessage(
                    senderId = "user_101", senderName = "Nguyen Thi Lan",
                    message = "Giờ breakfast là mấy giờ vậy ạ?", isFromStaff = false
                )
            )
            StaffMessageBubble(
                StaffChatMessage(
                    senderId = "staff_1",
                    senderName = "Staff",
                    message = "Dạ, breakfast từ 6:30 - 10:00 sáng tại tầng 2 ạ. Xin mời chị!",
                    isFromStaff = true,
                    status = StaffMessageStatus.READ
                )
            )
            StaffMessageBubble(
                StaffChatMessage(
                    senderId = "user_101", senderName = "Nguyen Thi Lan",
                    message = "Có buffet không ạ?", isFromStaff = false
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StaffConversationItemPreview() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth().background(StaffBg).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            StaffConversationItem(conversation = getSampleCustomerConversations()[0], onClick = {})
            StaffConversationItem(conversation = getSampleCustomerConversations()[1], onClick = {})
            StaffConversationItem(conversation = getSampleCustomerConversations()[2], onClick = {})
        }
    }
}