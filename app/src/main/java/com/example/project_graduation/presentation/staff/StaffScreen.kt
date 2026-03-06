package com.example.project_graduation.presentation.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_graduation.presentation.staff.staff_chat_management.StaffChatScreen
import com.example.project_graduation.presentation.staff.staff_dashboard_management.StaffDashboardContent
import com.example.project_graduation.presentation.staff.staff_profile_management.StaffProfileContent
import com.example.project_graduation.presentation.profile.ProfileViewModel
import com.example.project_graduation.presentation.staff.staff_booking_management.StaffBookingsContent
import com.example.project_graduation.presentation.staff.staff_booking_management.StaffBookingsViewModel
import com.example.project_graduation.presentation.staff.staff_chat_management.StaffChatViewModel
import com.example.project_graduation.presentation.staff.staff_dashboard_management.StaffDashboardViewModel
import com.example.project_graduation.presentation.staff.staff_room_management.StaffRoomsContent
import com.example.project_graduation.presentation.staff.staff_room_management.StaffRoomsViewModel


// ============= STAFF SCREEN (Main Entry) =============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(
    staffViewModel: StaffViewModel,
    dashboardViewModel : StaffDashboardViewModel,
    bookingsViewModel  : StaffBookingsViewModel,
    roomsViewModel     : StaffRoomsViewModel,
    chatViewModel      : StaffChatViewModel,
    profileViewModel: ProfileViewModel,
    initialTab: Int = 0,
    onLogout: () -> Unit = {}
) {
    val profile by dashboardViewModel.profile.collectAsState()
//    val staffInfo by staffViewModel.staffInfo.collectAsState()
//    val conversations by staffViewModel.conversations.collectAsState()
//    val unreadCount = remember(conversations) { conversations.sumOf { it.unreadCount } }
    val chatUnread  by remember { derivedStateOf { chatViewModel.getTotalUnread() } }
    var selectedTab by remember { mutableStateOf(initialTab) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Accent, PrimaryDk))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SupportAgent,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                profile.hotelName.ifBlank { "Hotel Staff" },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${profile.username} · ${profile.position}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
                actions = {
                    IconButton(onClick = { profileViewModel.logout { onLogout() }}) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                listOf(
                    Triple(0, Icons.Default.Dashboard, "Home"),
                    Triple(1, Icons.Default.BookOnline, "Bookings"),
                    Triple(2, Icons.Default.MeetingRoom, "Rooms"),
                    Triple(3, Icons.Default.Chat, "Chat"),
                    Triple(4, Icons.Default.Person, "Profile")
                ).forEach { (index, icon, label) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            // Chat badge
                            if (index == 3 && chatUnread > 0) {
                                BadgedBox(badge = {
                                    Badge { Text(chatUnread.toString(), fontSize = 10.sp) }
                                }) {
                                    Icon(icon, label)
                                }
                            } else {
                                Icon(icon, label)
                            }
                        },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            indicatorColor = Primary.copy(alpha = 0.1f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = LightBg
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> StaffDashboardContent(dashboardViewModel, bookingsViewModel)
                1 -> StaffBookingsContent(bookingsViewModel)
                2 -> StaffRoomsContent(roomsViewModel)
                3 -> StaffChatScreen(chatViewModel)
                4 -> StaffProfileContent(dashboardViewModel, profileViewModel, onLogout)
            }
        }
    }
}



//// ============= TAB 3: CHAT (wrapper) =============
//
//@Composable
//fun StaffChatTabContent(viewModel: StaffViewModel) {
//    // Reuse the StaffChatScreen but without nav
//    StaffChatScreen(
//        staffViewModel = viewModel,
//        onBack = {}   // Tab không cần back nav
//    )
//}


// ============= Staff Color Palette =============
private val Primary = Color(0xFF00897B)    // Teal 600
private val PrimaryDk = Color(0xFF00695C)    // Teal 800
private val Accent = Color(0xFF4DB6AC)    // Teal 300
private val LightBg = Color(0xFFF0FAF9)
private val CardBg = Color.White

// ============= HELPERS =============

private fun formatVND(amount: Double): String {
    return if (amount >= 1_000_000) {
        "${"%.1f".format(amount / 1_000_000)}M VNĐ"
    } else {
        "${(amount / 1000).toInt()}K VNĐ"
    }
}


