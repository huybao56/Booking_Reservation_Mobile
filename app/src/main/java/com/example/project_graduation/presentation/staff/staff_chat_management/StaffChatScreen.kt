//package com.example.project_graduation.presentation.staff.staff_chat_management
//
//
//import androidx.compose.animation.*
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.automirrored.filled.Send
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.project_graduation.presentation.staff.CustomerConversation
//import com.example.project_graduation.presentation.staff.StaffChatMessage
//import com.example.project_graduation.presentation.staff.StaffMessageStatus
//import com.example.project_graduation.presentation.staff.StaffViewModel
//import com.example.project_graduation.presentation.staff.getSampleCustomerConversations
//import com.example.project_graduation.presentation.staff.getSampleStaffMessages
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.*
//
//// ============= Color Palette (Staff = Teal/Green) =============
//private val StaffPrimary = Color(0xFF00897B)    // Teal 600
//private val StaffPrimaryDark = Color(0xFF00695C) // Teal 800
//private val StaffAccent = Color(0xFF4DB6AC)      // Teal 300
//private val StaffBg = Color(0xFFF0FAF9)
//private val StaffBubble = Color(0xFFE0F2F1)      // Staff chat bubble
//
//
//// ============= STAFF CHAT SCREEN (entry point) =============
//
//@Composable
//fun StaffChatScreen(
////    staffViewModel: StaffViewModel,
//    chatViewModel: StaffChatViewModel,
//    onBack: () -> Unit = {}
//) {
//    val conversations by chatViewModel.conversations.collectAsState()
//    val currentConversation by chatViewModel.currentConversation.collectAsState()
//    val currentMessages by chatViewModel.currentMessages.collectAsState()
//
//    if (currentConversation == null) {
//        // List of customer conversations
//        StaffChatListScreen(
//            conversations = conversations,
//            totalUnread = chatViewModel.getTotalUnread(),
//            onConversationClick = { chatViewModel.openConversation(it) },
//            onBack = onBack
//        )
//    } else {
//        // Detail screen — chat with specific customer
//        StaffChatDetailScreen(
//            conversation = currentConversation!!,
//            messages = currentMessages,
//            staffName = chatViewModel.currentStaffName,
//            onBack = { chatViewModel.closeConversation() },
//            onSendMessage = { text -> chatViewModel.sendMessage(text) }
//        )
//    }
//}
//
//// ============= CHAT LIST SCREEN =============
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StaffChatListScreen(
//    conversations: List<CustomerConversation> = emptyList(),
//    totalUnread: Int = 3,
//    onConversationClick: (CustomerConversation) -> Unit = {},
//    onBack: () -> Unit = {}
//) {
//    var searchQuery by remember { mutableStateOf("") }
//    var showSearch by remember { mutableStateOf(false) }
//
//    val filtered = if (searchQuery.isBlank()) conversations
//    else conversations.filter {
//        it.userName.contains(searchQuery, ignoreCase = true) ||
//                it.lastMessage.contains(searchQuery, ignoreCase = true)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    if (showSearch) {
//                        OutlinedTextField(
//                            value = searchQuery,
//                            onValueChange = { searchQuery = it },
//                            placeholder = { Text("Find customer...", fontSize = 14.sp) },
//                            singleLine = true,
//                            modifier = Modifier.fillMaxWidth(),
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedBorderColor = Color.White,
//                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
//                                focusedTextColor = Color.White,
//                                unfocusedTextColor = Color.White,
//                                cursorColor = Color.White
//                            ),
//                            textStyle = TextStyle(color = Color.White, fontSize = 16.sp)
//                        )
//                    } else {
//                        Column {
//                            Text("Chat with Customer", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
//                            if (totalUnread > 0) {
//                                Text("$totalUnread unread message", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
//                            }
//                        }
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { showSearch = !showSearch; if (!showSearch) searchQuery = "" }) {
//                        Icon(
//                            if (showSearch) Icons.Default.Close else Icons.Default.Search,
//                            contentDescription = "Search", tint = Color.White
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = StaffPrimary)
//            )
//        },
//        containerColor = StaffBg
//    ) { paddingValues ->
//        if (filtered.isEmpty()) {
//            Box(
//                Modifier.fillMaxSize().padding(paddingValues),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Icon(Icons.Default.ChatBubbleOutline, null, Modifier.size(72.dp), tint = Color(0xFFB2DFDB))
//                    Spacer(Modifier.height(12.dp))
//                    Text("Don't have any conversation", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
//                    Spacer(Modifier.height(4.dp))
//                    Text("Customer will contact when needing", fontSize = 13.sp, color = Color.LightGray)
//                }
//            }
//        } else {
//            LazyColumn(
//                Modifier.fillMaxSize().padding(paddingValues),
//                contentPadding = PaddingValues(vertical = 8.dp)
//            ) {
//                items(filtered) { conv ->
//                    StaffConversationItem(conv, onClick = { onConversationClick(conv) })
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun StaffConversationItem(
//    conversation: CustomerConversation,
//    onClick: () -> Unit
//) {
//    Surface(
//        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
//        onClick = onClick,
//        shape = RoundedCornerShape(16.dp),
//        color = Color.White,
//        shadowElevation = if (conversation.unreadCount > 0) 3.dp else 1.dp
//    ) {
//        Row(
//            Modifier.padding(14.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Avatar
//            Box {
//                Box(
//                    Modifier
//                        .size(52.dp)
//                        .clip(CircleShape)
//                        .background(
//                            Brush.linearGradient(listOf(StaffAccent, StaffPrimary))
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        conversation.userName.first().uppercase(),
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White
//                    )
//                }
//                // Online dot
//                if (conversation.isOnline) {
//                    Box(
//                        Modifier
//                            .size(13.dp)
//                            .clip(CircleShape)
//                            .background(Color(0xFF4CAF50))
//                            .align(Alignment.BottomEnd)
//                    )
//                }
//            }
//
//            Spacer(Modifier.width(14.dp))
//
//            // Name + last message
//            Column(Modifier.weight(1f)) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        conversation.userName,
//                        fontSize = 15.sp,
//                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
//                        color = Color(0xFF1A1A1A),
//                        modifier = Modifier.weight(1f)
//                    )
//                    // Booking badge
//                    conversation.bookingId?.let { bid ->
//                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE0F2F1)) {
//                            Text(bid, fontSize = 10.sp, color = StaffPrimary, fontWeight = FontWeight.Bold,
//                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
//                        }
//                    }
//                }
//                Spacer(Modifier.height(3.dp))
//                Text(
//                    conversation.lastMessage,
//                    fontSize = 13.sp,
//                    color = if (conversation.unreadCount > 0) Color(0xFF424242) else Color.Gray,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
//                )
//                // Booking status hint
//                conversation.bookingStatus?.let { status ->
//                    Spacer(Modifier.height(3.dp))
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(Icons.Default.Hotel, null, Modifier.size(11.dp), tint = StaffAccent)
//                        Spacer(Modifier.width(3.dp))
//                        Text(status, fontSize = 11.sp, color = StaffAccent)
//                    }
//                }
//            }
//
//            // Time + unread badge
//            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
//                Text(
//                    formatChatTime(conversation.lastMessageTime),
//                    fontSize = 11.sp,
//                    color = if (conversation.unreadCount > 0) StaffPrimary else Color.Gray
//                )
//                if (conversation.unreadCount > 0) {
//                    Box(
//                        Modifier.size(22.dp).clip(CircleShape).background(StaffPrimary),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(conversation.unreadCount.toString(), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//// ============= CHAT DETAIL SCREEN =============
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StaffChatDetailScreen(
//    conversation: CustomerConversation,
//    messages: List<StaffChatMessage> = emptyList(),
//    staffName: String = "Staff",
//    onBack: () -> Unit = {},
//    onSendMessage: (String) -> Unit = {}
//) {
//    var inputText by remember { mutableStateOf("") }
//    val listState = rememberLazyListState()
//    val scope = rememberCoroutineScope()
//
//    // Quick reply templates for hotel staff
//    val quickReplies = listOf(
//        "Dạ, chúng tôi sẽ hỗ trợ ngay ạ!",
//        "Breakfast: 6:30 - 10:00 tại tầng 2 ạ",
//        "Check-out trước 12:00 trưa ạ",
//        "Wi-Fi password: GrandPalace2026",
//        "Nhân viên sẽ đến phòng trong 10-15 phút ạ"
//    )
//    var showQuickReplies by remember { mutableStateOf(false) }
//
//    LaunchedEffect(messages.size) {
//        if (messages.isNotEmpty()) {
//            scope.launch { listState.animateScrollToItem(messages.size - 1) }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Box {
//                            Box(
//                                Modifier
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                                    .background(Brush.linearGradient(listOf(StaffAccent, StaffPrimary))),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(conversation.userName.first().uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
//                            }
//                            if (conversation.isOnline) {
//                                Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF4CAF50)).align(Alignment.BottomEnd))
//                            }
//                        }
//                        Spacer(Modifier.width(12.dp))
//                        Column {
//                            Text(conversation.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
//                            Text(
//                                if (conversation.isOnline) "Đang hoạt động" else "Ngoại tuyến",
//                                fontSize = 12.sp,
//                                color = Color.White.copy(alpha = 0.8f)
//                            )
//                        }
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
//                    }
//                },
//                actions = {
//                    conversation.bookingId?.let { bid ->
//                        TextButton(onClick = {}) {
//                            Text(bid, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
//                        }
//                    }
//                    IconButton(onClick = { }) {
//                        Icon(Icons.Default.MoreVert, null, tint = Color.White)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = StaffPrimary)
//            )
//        },
//        bottomBar = {
//            Column {
//                // Quick Replies
//                AnimatedVisibility(visible = showQuickReplies) {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .heightIn(max = 160.dp)
//                            .background(Color.White),
//                        contentPadding = PaddingValues(8.dp),
//                        verticalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        items(quickReplies) { reply ->
//                            Surface(
//                                onClick = {
//                                    inputText = reply
//                                    showQuickReplies = false
//                                },
//                                shape = RoundedCornerShape(8.dp),
//                                color = StaffBubble
//                            ) {
//                                Text(
//                                    reply,
//                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
//                                    fontSize = 13.sp,
//                                    color = Color(0xFF00695C)
//                                )
//                            }
//                        }
//                    }
//                }
//
//                // Input bar
//                Surface(
//                    color = Color.White,
//                    shadowElevation = 8.dp
//                ) {
//                    Row(
//                        Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
//                        verticalAlignment = Alignment.Bottom
//                    ) {
//                        // Quick reply button
//                        IconButton(
//                            onClick = { showQuickReplies = !showQuickReplies },
//                            modifier = Modifier.size(40.dp)
//                        ) {
//                            Icon(
//                                Icons.Default.FlashOn,
//                                null,
//                                tint = if (showQuickReplies) StaffPrimary else Color.Gray
//                            )
//                        }
//
//                        OutlinedTextField(
//                            value = inputText,
//                            onValueChange = { inputText = it },
//                            placeholder = { Text("Nhập tin nhắn hỗ trợ...", color = Color.Gray, fontSize = 14.sp) },
//                            modifier = Modifier.weight(1f),
//                            shape = RoundedCornerShape(24.dp),
//                            maxLines = 4,
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedBorderColor = StaffPrimary,
//                                unfocusedBorderColor = Color(0xFFDDDDDD)
//                            )
//                        )
//
//                        Spacer(Modifier.width(8.dp))
//
//                        // Send button
//                        FloatingActionButton(
//                            onClick = {
//                                if (inputText.isNotBlank()) {
//                                    onSendMessage(inputText.trim())
//                                    inputText = ""
//                                    showQuickReplies = false
//                                }
//                            },
//                            containerColor = if (inputText.isNotBlank()) StaffPrimary else Color.Gray,
//                            modifier = Modifier.size(48.dp),
//                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
//                        ) {
//                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
//                        }
//                    }
//                }
//            }
//        },
//        containerColor = StaffBg
//    ) { paddingValues ->
//        if (messages.isEmpty()) {
//            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Icon(Icons.Default.Chat, null, Modifier.size(64.dp), tint = Color(0xFFB2DFDB))
//                    Spacer(Modifier.height(12.dp))
//                    Text("Bắt đầu hỗ trợ khách hàng", fontSize = 15.sp, color = Color.Gray)
//                    Text("Nhập tin nhắn bên dưới", fontSize = 13.sp, color = Color.LightGray)
//                }
//            }
//        } else {
//            LazyColumn(
//                state = listState,
//                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 12.dp),
//                contentPadding = PaddingValues(vertical = 12.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(messages) { msg ->
//                    StaffMessageBubble(message = msg)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun StaffMessageBubble(message: StaffChatMessage) {
//    val isStaff = message.isFromStaff
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = if (isStaff) Arrangement.End else Arrangement.Start,
//        verticalAlignment = Alignment.Bottom
//    ) {
//        // Customer avatar (left side)
//        if (!isStaff) {
//            Box(
//                Modifier
//                    .size(32.dp)
//                    .clip(CircleShape)
//                    .background(StaffAccent),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(message.senderName.first().uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
//            }
//            Spacer(Modifier.width(8.dp))
//        }
//
//        Column(
//            modifier = Modifier.widthIn(max = 280.dp),
//            horizontalAlignment = if (isStaff) Alignment.End else Alignment.Start
//        ) {
//            if (!isStaff) {
//                Text(message.senderName, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp, start = 4.dp))
//            }
//
//            Surface(
//                shape = RoundedCornerShape(
//                    topStart = 18.dp, topEnd = 18.dp,
//                    bottomStart = if (isStaff) 18.dp else 4.dp,
//                    bottomEnd = if (isStaff) 4.dp else 18.dp
//                ),
//                color = if (isStaff) StaffPrimary else Color.White,
//                shadowElevation = 1.dp
//            ) {
//                Text(
//                    message.message,
//                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
//                    fontSize = 14.sp,
//                    color = if (isStaff) Color.White else Color(0xFF1A1A1A),
//                    lineHeight = 20.sp
//                )
//            }
//
//            // Time + status
//            Row(
//                Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    formatTime(message.timestamp),
//                    fontSize = 10.sp,
//                    color = Color.Gray
//                )
//                if (isStaff) {
//                    when (message.status) {
//                        StaffMessageStatus.SENDING -> Icon(Icons.Default.Schedule, null, Modifier.size(12.dp), tint = Color.Gray)
//                        StaffMessageStatus.SENT -> Icon(Icons.Default.Check, null, Modifier.size(12.dp), tint = Color.Gray)
//                        StaffMessageStatus.DELIVERED -> Icon(Icons.Default.DoneAll, null, Modifier.size(12.dp), tint = Color.Gray)
//                        StaffMessageStatus.READ -> Icon(Icons.Default.DoneAll, null, Modifier.size(12.dp), tint = StaffAccent)
//                    }
//                }
//            }
//        }
//
//        // Staff avatar (right side)
//        if (isStaff) {
//            Spacer(Modifier.width(8.dp))
//            Box(
//                Modifier
//                    .size(32.dp)
//                    .clip(CircleShape)
//                    .background(StaffPrimaryDark),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(Icons.Default.SupportAgent, null, Modifier.size(18.dp), tint = Color.White)
//            }
//        }
//    }
//}
//
//
//// ============= HELPERS =============
//
//private fun formatChatTime(timestamp: Long): String {
//    val now = System.currentTimeMillis()
//    val diff = now - timestamp
//    return when {
//        diff < 60_000 -> "Vừa xong"
//        diff < 3_600_000 -> "${diff / 60_000} phút"
//        diff < 86_400_000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
//        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
//    }
//}
//
//private fun formatTime(timestamp: Long): String =
//    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
//
//
//// ============= PREVIEWS =============
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun StaffChatListScreenPreview() {
//    MaterialTheme {
//        StaffChatListScreen()
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun StaffChatListEmptyPreview() {
//    MaterialTheme {
//        StaffChatListScreen(conversations = emptyList())
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun StaffChatDetailScreenPreview() {
//    MaterialTheme {
//        StaffChatDetailScreen(
//            conversation = getSampleCustomerConversations()[0],
//            messages = getSampleStaffMessages("conv1"),
//            staffName = "Nguyen Van A"
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun StaffMessageBubblePreview() {
//    MaterialTheme {
//        Column(
//            Modifier
//                .fillMaxWidth()
//                .background(StaffBg)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            StaffMessageBubble(
//                StaffChatMessage(
//                    senderId = "user_101", senderName = "Nguyen Thi Lan",
//                    message = "Giờ breakfast là mấy giờ vậy ạ?", isFromStaff = false
//                )
//            )
//            StaffMessageBubble(
//                StaffChatMessage(
//                    senderId = "staff_1",
//                    senderName = "Staff",
//                    message = "Dạ, breakfast từ 6:30 - 10:00 sáng tại tầng 2 ạ. Xin mời chị!",
//                    isFromStaff = true,
//                    status = StaffMessageStatus.READ
//                )
//            )
//            StaffMessageBubble(
//                StaffChatMessage(
//                    senderId = "user_101", senderName = "Nguyen Thi Lan",
//                    message = "Có buffet không ạ?", isFromStaff = false
//                )
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun StaffConversationItemPreview() {
//    MaterialTheme {
//        Column(Modifier.fillMaxWidth().background(StaffBg).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
//            StaffConversationItem(conversation = getSampleCustomerConversations()[0], onClick = {})
//            StaffConversationItem(conversation = getSampleCustomerConversations()[1], onClick = {})
//            StaffConversationItem(conversation = getSampleCustomerConversations()[2], onClick = {})
//        }
//    }
//}

package com.example.project_graduation.presentation.staff.staff_chat_management

import androidx.compose.animation.AnimatedVisibility
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ─── Colors ───────────────────────────────────────────────────────────────────
private val Primary     = Color(0xFF00897B)
private val PrimaryDark = Color(0xFF00695C)
private val Accent      = Color(0xFF4DB6AC)
private val Bg          = Color(0xFFF0FAF9)
private val Bubble      = Color(0xFFE0F2F1)

// =============================================================================
// ENTRY POINT
// =============================================================================

@Composable
fun StaffChatScreen(
    chatViewModel: StaffChatViewModel,
    onBack: () -> Unit = {}
) {
    val conversations       by chatViewModel.conversations.collectAsState()
    val currentConversation by chatViewModel.currentConversation.collectAsState()
    val currentMessages     by chatViewModel.currentMessages.collectAsState()

    if (currentConversation == null) {
        StaffChatListScreen(
            conversations       = conversations,
            totalUnread         = chatViewModel.getTotalUnread(),
            onConversationClick = { chatViewModel.openConversation(it) },
            onBack              = onBack
        )
    } else {
        StaffChatDetailScreen(
            conversation  = currentConversation!!,
            messages      = currentMessages,
            staffName     = chatViewModel.currentStaffName,
            onBack        = { chatViewModel.closeConversation() },
            onSendMessage = { text -> chatViewModel.sendMessage(text) }
        )
    }
}

// =============================================================================
// CHAT LIST SCREEN
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffChatListScreen(
    conversations:       List<CustomerConversation> = emptyList(),
    totalUnread:         Int = 0,
    onConversationClick: (CustomerConversation) -> Unit = {},
    onBack:              () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSearch  by remember { mutableStateOf(false) }

    val filtered = if (searchQuery.isBlank()) conversations
    else conversations.filter {
        it.userName.contains(searchQuery, ignoreCase = true) ||
                it.lastMessage?.contains(searchQuery, ignoreCase = true) == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value         = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder   = { Text("Tìm khách hàng...", fontSize = 14.sp) },
                            singleLine    = true,
                            modifier      = Modifier.fillMaxWidth(),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor     = Color.White,
                                unfocusedTextColor   = Color.White,
                                cursorColor          = Color.White
                            ),
                            textStyle = TextStyle(color = Color.White, fontSize = 16.sp)
                        )
                    } else {
                        Column {
                            Text("Chat với Khách Hàng", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            if (totalUnread > 0)
                                Text("$totalUnread tin chưa đọc", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch; if (!showSearch) searchQuery = "" }) {
                        Icon(if (showSearch) Icons.Default.Close else Icons.Default.Search, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        },
        containerColor = Bg
    ) { padding ->
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ChatBubbleOutline, null, Modifier.size(72.dp), tint = Color(0xFFB2DFDB))
                    Spacer(Modifier.height(12.dp))
                    Text("Không có cuộc trò chuyện", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Text("Khách hàng sẽ liên hệ khi cần hỗ trợ", fontSize = 13.sp, color = Color.LightGray)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(vertical = 8.dp)) {
                items(filtered, key = { it.conversationId }) { conv ->
                    ConversationItem(conv) { onConversationClick(conv) }
                }
            }
        }
    }
}

// =============================================================================
// CONVERSATION ITEM
// CustomerConversation: conversationId, userId, userName, guestPhone?,
//   staffUserId, lastMessage?, lastMessageTime?, unreadCount, isOnline
// =============================================================================

@Composable
private fun ConversationItem(conv: CustomerConversation, onClick: () -> Unit) {
    Surface(
        onClick       = onClick,
        modifier      = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        shape         = RoundedCornerShape(16.dp),
        color         = Color.White,
        shadowElevation = if (conv.unreadCount > 0) 3.dp else 1.dp
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {

            // Avatar
            Box {
                Box(
                    Modifier.size(52.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Accent, Primary))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        conv.userName.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White
                    )
                }
                if (conv.isOnline) {
                    Box(Modifier.size(13.dp).clip(CircleShape).background(Color(0xFF4CAF50)).align(Alignment.BottomEnd))
                }
            }

            Spacer(Modifier.width(14.dp))

            // Name + phone + last message
            Column(Modifier.weight(1f)) {
                Text(
                    conv.userName,
                    fontSize   = 15.sp,
                    fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                    color      = Color(0xFF1A1A1A)
                )
                conv.guestPhone?.let {
                    Text(it, fontSize = 11.sp, color = Accent)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    conv.lastMessage ?: "Chưa có tin nhắn",
                    fontSize   = 13.sp,
                    color      = if (conv.unreadCount > 0) Color(0xFF424242) else Color.Gray,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    fontWeight = if (conv.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                )
            }

            // Time + unread badge
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    formatChatTime(conv.lastMessageTime),
                    fontSize = 11.sp,
                    color    = if (conv.unreadCount > 0) Primary else Color.Gray
                )
                if (conv.unreadCount > 0) {
                    Box(
                        Modifier.size(22.dp).clip(CircleShape).background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(conv.unreadCount.toString(), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =============================================================================
// CHAT DETAIL SCREEN
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffChatDetailScreen(
    conversation:  CustomerConversation,
    messages:      List<StaffChatMessage> = emptyList(),
    staffName:     String = "Staff",
    onBack:        () -> Unit = {},
    onSendMessage: (String) -> Unit = {}
) {
    var inputText        by remember { mutableStateOf("") }
    var showQuickReplies by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope     = rememberCoroutineScope()

    val quickReplies = listOf(
        "Dạ, chúng tôi sẽ hỗ trợ ngay ạ!",
        "Breakfast: 6:30 - 10:00 tại tầng 2 ạ",
        "Check-out trước 12:00 trưa ạ",
        "Wi-Fi password: GrandPalace2026",
        "Nhân viên sẽ đến phòng trong 10-15 phút ạ"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) scope.launch { listState.animateScrollToItem(messages.size - 1) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Box(
                                Modifier.size(40.dp).clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Accent, Primary))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    conversation.userName.firstOrNull()?.uppercase() ?: "?",
                                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White
                                )
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
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f)
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
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        },
        bottomBar = {
            Column {
                // Quick replies
                AnimatedVisibility(visible = showQuickReplies) {
                    LazyColumn(
                        modifier        = Modifier.fillMaxWidth().heightIn(max = 160.dp).background(Color.White),
                        contentPadding  = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(quickReplies) { reply ->
                            Surface(
                                onClick = { inputText = reply; showQuickReplies = false },
                                shape   = RoundedCornerShape(8.dp),
                                color   = Bubble
                            ) {
                                Text(reply, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    fontSize = 13.sp, color = Color(0xFF00695C))
                            }
                        }
                    }
                }
                // Input bar
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.Bottom) {
                        IconButton(onClick = { showQuickReplies = !showQuickReplies }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.FlashOn, null, tint = if (showQuickReplies) Primary else Color.Gray)
                        }
                        OutlinedTextField(
                            value         = inputText,
                            onValueChange = { inputText = it },
                            placeholder   = { Text("Nhập tin nhắn hỗ trợ...", color = Color.Gray, fontSize = 14.sp) },
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(24.dp),
                            maxLines      = 4,
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Primary,
                                unfocusedBorderColor = Color(0xFFDDDDDD)
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick        = {
                                if (inputText.isNotBlank()) { onSendMessage(inputText.trim()); inputText = ""; showQuickReplies = false }
                            },
                            containerColor = if (inputText.isNotBlank()) Primary else Color.Gray,
                            modifier       = Modifier.size(48.dp),
                            elevation      = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        },
        containerColor = Bg
    ) { padding ->
        if (messages.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Chat, null, Modifier.size(64.dp), tint = Color(0xFFB2DFDB))
                    Spacer(Modifier.height(12.dp))
                    Text("Bắt đầu hỗ trợ khách hàng", fontSize = 15.sp, color = Color.Gray)
                    Text("Nhập tin nhắn bên dưới", fontSize = 13.sp, color = Color.LightGray)
                }
            }
        } else {
            LazyColumn(
                state               = listState,
                modifier            = Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp),
                contentPadding      = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.messageId }) { msg -> MessageBubble(msg) }
            }
        }
    }
}

// =============================================================================
// MESSAGE BUBBLE
// StaffChatMessage: messageId, senderId, senderName, messageText,
//   isFromStaff, isRead, createdAt, isSending
// =============================================================================

@Composable
private fun MessageBubble(message: StaffChatMessage) {
    val isStaff = message.isFromStaff

    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isStaff) Arrangement.End else Arrangement.Start,
        verticalAlignment   = Alignment.Bottom
    ) {
        // Customer avatar
        if (!isStaff) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(Accent), contentAlignment = Alignment.Center) {
                Text(message.senderName.firstOrNull()?.uppercase() ?: "?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier           = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isStaff) Alignment.End else Alignment.Start
        ) {
            if (!isStaff) {
                Text(message.senderName, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp, start = 4.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart    = 18.dp, topEnd      = 18.dp,
                    bottomStart = if (isStaff) 18.dp else 4.dp,
                    bottomEnd   = if (isStaff) 4.dp  else 18.dp
                ),
                color           = if (isStaff) Primary else Color.White,
                shadowElevation = 1.dp
            ) {
                Text(
                    text     = message.messageText,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    fontSize = 14.sp,
                    color    = if (isStaff) Color.White else Color(0xFF1A1A1A),
                    lineHeight = 20.sp
                )
            }

            // Time + status icon
            Row(
                Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(formatTime(message.createdAt), fontSize = 10.sp, color = Color.Gray)
                if (isStaff) {
                    when {
                        message.isSending -> Icon(Icons.Default.Schedule, null, Modifier.size(12.dp), tint = Color.Gray)
                        message.isRead    -> Icon(Icons.Default.DoneAll,  null, Modifier.size(12.dp), tint = Accent)
                        else              -> Icon(Icons.Default.Check,    null, Modifier.size(12.dp), tint = Color.Gray)
                    }
                }
            }
        }

        // Staff avatar
        if (isStaff) {
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(32.dp).clip(CircleShape).background(PrimaryDark), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.SupportAgent, null, Modifier.size(18.dp), tint = Color.White)
            }
        }
    }
}

// =============================================================================
// TIME HELPERS
// =============================================================================

/** createdAt từ backend: "yyyy-MM-dd HH:mm:ss" → hiển thị "HH:mm" */
private fun formatTime(createdAt: String): String = try {
    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(createdAt)
        ?: return createdAt.takeLast(5)
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
} catch (e: Exception) { createdAt.takeLast(5) }

/** lastMessageTime: "yyyy-MM-dd HH:mm:ss" hoặc "Vừa xong" */
private fun formatChatTime(ts: String?): String {
    if (ts.isNullOrBlank()) return ""
    if (!ts.contains("-")) return ts   // plain text như "Vừa xong"
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(ts)
            ?: return ts.takeLast(5)
        val diff = System.currentTimeMillis() - date.time
        when {
            diff < 60_000     -> "Vừa xong"
            diff < 3_600_000  -> "${diff / 60_000} phút"
            diff < 86_400_000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            else              -> SimpleDateFormat("dd/MM",  Locale.getDefault()).format(date)
        }
    } catch (e: Exception) { ts.takeLast(5) }
}

// =============================================================================
// PREVIEW DATA  (self-contained, không import StaffViewModel)
// =============================================================================

private fun previewConvs() = listOf(
    CustomerConversation(1, 101, "Nguyen Thi Lan", "0901234567", 1, "Giờ breakfast là mấy giờ ạ?", "2026-03-04 09:00:00", 2, isOnline = true),
    CustomerConversation(2, 102, "Tran Van Minh",  "0912345678", 1, "Room service cảm ơn!",          "2026-03-04 08:00:00", 0, isOnline = true),
    CustomerConversation(3, 103, "Hoang My Linh",  null,         1, "Đặt taxi giúp mình nhé",         "2026-03-04 07:00:00", 1, isOnline = false),
)

private fun previewMsgs() = listOf(
    StaffChatMessage(1, 101, "Nguyen Thi Lan", "Giờ breakfast là mấy giờ vậy ạ?",    isFromStaff = false, isRead = false, createdAt = "2026-03-04 08:55:00"),
    StaffChatMessage(2, 1,   "Staff",          "Breakfast 6:30–10:00 tại tầng 2 ạ!", isFromStaff = true,  isRead = true,  createdAt = "2026-03-04 08:57:00"),
    StaffChatMessage(3, 101, "Nguyen Thi Lan", "Có buffet không ạ?",                  isFromStaff = false, isRead = false, createdAt = "2026-03-04 09:00:00"),
)

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChatList() {
    MaterialTheme { StaffChatListScreen(conversations = previewConvs(), totalUnread = 3) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChatListEmpty() {
    MaterialTheme { StaffChatListScreen() }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChatDetail() {
    MaterialTheme {
        StaffChatDetailScreen(conversation = previewConvs()[0], messages = previewMsgs(), staffName = "Nguyen Van A")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageBubbles() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth().background(Bg).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            previewMsgs().forEach { MessageBubble(it) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConversationItems() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth().background(Bg).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            previewConvs().forEach { ConversationItem(it) {} }
        }
    }
}