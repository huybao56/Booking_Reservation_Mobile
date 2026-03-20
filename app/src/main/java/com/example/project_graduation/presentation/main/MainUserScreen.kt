@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.project_graduation.presentation.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.presentation.booking.BookingScreen
import com.example.project_graduation.presentation.chat.ChatListScreen
import com.example.project_graduation.presentation.chat.ChatScreen
import com.example.project_graduation.presentation.chat.ChatViewModel
import com.example.project_graduation.presentation.home.HomeScreen
import com.example.project_graduation.presentation.home.HomeViewModel
import com.example.project_graduation.presentation.profile.ProfileScreen
import com.example.project_graduation.presentation.profile.ProfileViewModel

@Composable
fun MainUserScreen(
    chatViewModel: ChatViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    preferencesManager: PreferencesManager,
    initialTab: Int = 0,
    onNavigateToHotelDetail: (Int) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    // ← LISTEN pending tab change từ ChatViewModel
    val pendingTab by chatViewModel.pendingTabChange.collectAsState()

    LaunchedEffect(pendingTab) {
        if (pendingTab != null) {
            selectedTab = pendingTab!!
            chatViewModel.clearPendingTab()
        }
    }

    // Log để debug
    LaunchedEffect(initialTab) {
        Log.d("MainUserScreen", "Initialized with tab: $initialTab")
    }

// Trong NavGraph.kt hoặc MainScreen.kt
    LaunchedEffect(Unit) {
        val user = preferencesManager.getUser()
        user?.let {
            chatViewModel.init(it.userId, it.username)
        }
    }

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        when (selectedTab) {
//                            0 -> "Home"
//                            1 -> "My Bookings"
//                            2 -> "Profile"
//                            else -> "Home"
//                        },
//                        fontWeight = FontWeight.Bold
//                    )
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White,
//                    titleContentColor = Color.Black
//                ),
//                actions = {
//                    // Chỉ show notification ở Home tab
//                    if (selectedTab == 0) {
//                        IconButton(onClick = { }) {
//                            Icon(
//                                Icons.Default.Notifications,
//                                contentDescription = "Notifications",
//                                tint = Color.Gray
//                            )
//                        }
//                    }
//
//                    IconButton(onClick = {
//                        Log.d("MainUserScreen", "Logout clicked")
//                        profileViewModel.logout {
//                            onLogout()
//                        }
//                    }) {
//                        Icon(
//                            Icons.AutoMirrored.Filled.ExitToApp,
//                            contentDescription = "Logout",
//                            tint = Color.Gray
//                        )
//                    }
//                }
//            )
//        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
//                windowInsets = WindowInsets(0.dp)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2196F3),
                        selectedTextColor = Color(0xFF2196F3),
                        indicatorColor = Color(0xFF2196F3).copy(alpha = 0.1f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.KingBed, contentDescription = "Bookings") },
                    label = { Text("Bookings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2196F3),
                        selectedTextColor = Color(0xFF2196F3),
                        indicatorColor = Color(0xFF2196F3).copy(alpha = 0.1f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                // Chat Tab - NEW!
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
//                        BadgedBox(
//                            badge = {
//                                // Hiển thị badge khi có tin nhắn chưa đọc
//                                if (selectedTab != 2) {
//                                    Badge(
//                                        containerColor = Color(0xFFFF5252)
//                                    ) {
//                                        Text("3") // Số tin nhắn chưa đọc (sau này sẽ lấy từ ViewModel)
//                                    }
//                                }
//                            }
//                        ) {
                        Icon(
                            Icons.Default.ChatBubble,
                            contentDescription = "Chat"
                        )
//                        }
                    },
                    label = { Text("Chat") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2196F3),
                        selectedTextColor = Color(0xFF2196F3),
                        indicatorColor = Color(0xFF2196F3).copy(alpha = 0.1f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2196F3),
                        selectedTextColor = Color(0xFF2196F3),
                        indicatorColor = Color(0xFF2196F3).copy(alpha = 0.1f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        // QUAN TRỌNG: paddingValues tự động tránh bottom nav
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    viewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    onNavigateToProfile = { selectedTab = 3 },
                    onNavigateToHotelDetail = onNavigateToHotelDetail,
                    onLogout = onLogout
                )

                1 -> BookingScreen(
                    preferencesManager = preferencesManager,
                    onBack = { selectedTab = 0 }
                )

//                2 -> ChatListScreen(
//                    onBack = { selectedTab = 0 },
//                    onConversationClick = { conversation ->
//                        // TODO: Navigate to chat detail screen
//                        Log.d("MainUserScreen", "Open chat with: ${conversation.hotelName}")
//                    }
//                )

                2 -> ChatScreen(
                    chatViewModel = chatViewModel,
                    onBack = { selectedTab = 0 },
//                    onConversationClick = { conversation ->
//                        // TODO: Navigate to chat detail screen
//                        Log.d("MainUserScreen", "Open chat with: ${conversation.hotelName}")
//                    }
                )

                3 -> ProfileScreen(
                    viewModel = profileViewModel,
                    onBack = { selectedTab = 0 },
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToBookings = {
                        selectedTab = 1  // ← Chỉ chuyển sang Bookings tab
                    },
                    onLogout = onLogout
                )
            }
        }
    }
}