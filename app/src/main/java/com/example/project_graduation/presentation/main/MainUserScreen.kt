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
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    // Log để debug
    LaunchedEffect(initialTab) {
        Log.d("MainUserScreen", "Initialized with tab: $initialTab")
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
                windowInsets = WindowInsets(0.dp)
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
                    onNavigateToBookings = {
                        selectedTab = 1  // ← Chỉ chuyển sang Bookings tab
                    },
                    onLogout = onLogout
                )
            }
        }
    }
}


//@Composable
//fun HotelsListScreen(
//    onNavigateToHotelDetail: (Int) -> Unit
//) {
//    val sampleHotels = remember { getSampleHotelsForList() }
//    var searchQuery by remember { mutableStateOf("") }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        "Hotels",
//                        fontWeight = FontWeight.Bold
//                    )
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White,
//                    titleContentColor = Color.Black
//                ),
//                actions = {
//                    IconButton(onClick = { /* Filter */ }) {
//                        Icon(
//                            Icons.Default.FilterList,
//                            contentDescription = "Filter",
//                            tint = Color.Gray
//                        )
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFFF5F5F5))
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp)
//        ) {
//            item {
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Search Bar
//                OutlinedTextField(
//                    value = searchQuery,
//                    onValueChange = { searchQuery = it },
//                    placeholder = {
//                        Text("Search hotels...", color = Color.Gray, fontSize = 14.sp)
//                    },
//                    leadingIcon = {
//                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF2196F3))
//                    },
//                    trailingIcon = {
//                        if (searchQuery.isNotEmpty()) {
//                            IconButton(onClick = { searchQuery = "" }) {
//                                Icon(Icons.Default.Clear, contentDescription = "Clear")
//                            }
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(12.dp),
//                    colors = OutlinedTextFieldDefaults.colors(
//                        unfocusedBorderColor = Color.LightGray,
//                        focusedBorderColor = Color(0xFF2196F3),
//                        unfocusedContainerColor = Color.White,
//                        focusedContainerColor = Color.White
//                    ),
//                    singleLine = true
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Filter Chips Row
//                LazyRow(
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    item {
//                        FilterChip(
//                            selected = true,
//                            onClick = { },
//                            label = { Text("All") }
//                        )
//                    }
//                    item {
//                        FilterChip(
//                            selected = false,
//                            onClick = { },
//                            label = { Text("5 Star") },
//                            leadingIcon = {
//                                Icon(
//                                    Icons.Default.Star,
//                                    contentDescription = null,
//                                    modifier = Modifier.size(16.dp)
//                                )
//                            }
//                        )
//                    }
//                    item {
//                        FilterChip(
//                            selected = false,
//                            onClick = { },
//                            label = { Text("Price: Low to High") }
//                        )
//                    }
//                    item {
//                        FilterChip(
//                            selected = false,
//                            onClick = { },
//                            label = { Text("Rating: High") }
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "${sampleHotels.size} hotels found",
//                    fontSize = 14.sp,
//                    color = Color.Gray,
//                    fontWeight = FontWeight.Medium
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//
//            items(sampleHotels) { hotel ->
//                HotelListItemCard(
//                    hotel = hotel,
//                    onClick = { onNavigateToHotelDetail(hotel.hotelId) }
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//
//            item {
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//        }
//    }
//}
//
//@Composable
//fun HotelListItemCard(
//    hotel: HotelListItem,
//    onClick: () -> Unit
//) {
//    var isFavorite by remember { mutableStateOf(hotel.isFavorite) }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(140.dp)
//            .clickable(onClick = onClick),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(12.dp)
//        ) {
//            // Hotel Image Placeholder
//            Box(
//                modifier = Modifier
//                    .width(110.dp)
//                    .fillMaxHeight()
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(Color(0xFFE0E0E0)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    Icons.Default.Hotel,
//                    contentDescription = null,
//                    modifier = Modifier.size(48.dp),
//                    tint = Color.Gray
//                )
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            // Hotel Info
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxHeight(),
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                Column {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.Top
//                    ) {
//                        Text(
//                            text = hotel.name,
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black,
//                            maxLines = 2,
//                            modifier = Modifier.weight(1f)
//                        )
//
//                        IconButton(
//                            onClick = { isFavorite = !isFavorite },
//                            modifier = Modifier.size(32.dp)
//                        ) {
//                            Icon(
//                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
//                                contentDescription = "Favorite",
//                                tint = if (isFavorite) Color.Red else Color.Gray,
//                                modifier = Modifier.size(20.dp)
//                            )
//                        }
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.padding(top = 4.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.LocationOn,
//                            contentDescription = null,
//                            modifier = Modifier.size(14.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = hotel.location,
//                            fontSize = 12.sp,
//                            color = Color.Gray,
//                            maxLines = 1
//                        )
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.padding(top = 4.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.Star,
//                            contentDescription = null,
//                            modifier = Modifier.size(14.dp),
//                            tint = Color(0xFFFFD700)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = "${hotel.rating} (${hotel.reviews} reviews)",
//                            fontSize = 12.sp,
//                            color = Color.Gray
//                        )
//                    }
//                }
//
//                // Price
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.Bottom
//                ) {
//                    Column {
//                        Text(
//                            text = "$${hotel.pricePerNight}",
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color(0xFF2196F3)
//                        )
//                        Text(
//                            text = "per night",
//                            fontSize = 11.sp,
//                            color = Color.Gray
//                        )
//                    }
//
//                    if (hotel.discount > 0) {
//                        Surface(
//                            shape = RoundedCornerShape(8.dp),
//                            color = Color(0xFFFFEBEE)
//                        ) {
//                            Text(
//                                text = "-${hotel.discount}%",
//                                fontSize = 12.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = Color(0xFFF44336),
//                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Data class cho Hotels List
//data class HotelListItem(
//    val hotelId: Int,
//    val name: String,
//    val location: String,
//    val rating: Double,
//    val reviews: Int,
//    val pricePerNight: Int,
//    val discount: Int,
//    val isFavorite: Boolean
//)
//
//// Sample data
//fun getSampleHotelsForList(): List<HotelListItem> {
//    return listOf(
//        HotelListItem(
//            hotelId = 1,
//            name = "Grand Palace Hotel",
//            location = "New York, USA",
//            rating = 4.8,
//            reviews = 245,
//            pricePerNight = 150,
//            discount = 15,
//            isFavorite = false
//        ),
//        HotelListItem(
//            hotelId = 2,
//            name = "Beach Resort Paradise",
//            location = "Miami, Florida",
//            rating = 4.9,
//            reviews = 189,
//            pricePerNight = 220,
//            discount = 0,
//            isFavorite = true
//        ),
//        HotelListItem(
//            hotelId = 3,
//            name = "Mountain View Lodge",
//            location = "Aspen, Colorado",
//            rating = 4.7,
//            reviews = 156,
//            pricePerNight = 180,
//            discount = 10,
//            isFavorite = false
//        ),
//        HotelListItem(
//            hotelId = 4,
//            name = "City Center Inn",
//            location = "Los Angeles, CA",
//            rating = 4.5,
//            reviews = 302,
//            pricePerNight = 120,
//            discount = 20,
//            isFavorite = false
//        ),
//        HotelListItem(
//            hotelId = 5,
//            name = "Royal Suite Hotel",
//            location = "San Francisco, CA",
//            rating = 4.9,
//            reviews = 421,
//            pricePerNight = 250,
//            discount = 0,
//            isFavorite = true
//        ),
//        HotelListItem(
//            hotelId = 6,
//            name = "Seaside Villa Resort",
//            location = "Malibu, CA",
//            rating = 4.8,
//            reviews = 198,
//            pricePerNight = 300,
//            discount = 12,
//            isFavorite = false
//        )
//    )
//}
//
//// ============= PREVIEW =============
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun HotelsListScreenPreview() {
//    MaterialTheme {
//        HotelsListScreen(onNavigateToHotelDetail = {})
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun HotelListItemCardPreview() {
//    MaterialTheme {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color(0xFFF5F5F5))
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            HotelListItemCard(
//                hotel = HotelListItem(
//                    hotelId = 1,
//                    name = "Grand Palace Hotel",
//                    location = "New York, USA",
//                    rating = 4.8,
//                    reviews = 245,
//                    pricePerNight = 150,
//                    discount = 15,
//                    isFavorite = false
//                ),
//                onClick = {}
//            )
//            HotelListItemCard(
//                hotel = HotelListItem(
//                    hotelId = 2,
//                    name = "Beach Resort Paradise with Long Name",
//                    location = "Miami, Florida",
//                    rating = 4.9,
//                    reviews = 189,
//                    pricePerNight = 220,
//                    discount = 0,
//                    isFavorite = true
//                ),
//                onClick = {}
//            )
//        }
//    }
//}