//package com.example.project_graduation.presentation.admin
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ExitToApp
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AdminScreen(
//
//    onLogout: () -> Unit
//){
//
//
//    LaunchedEffect(Unit) {
//        Log.d("AdminScreen", "Fetched the admin screen")
//    }
//
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White
//                ),
//                actions = {
//                    IconButton(onClick = onLogout) {
//                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
//                    }
//                }
//            )
//        }
//    ){paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White)
//                .padding(paddingValues)
//                .padding(horizontal = 20.dp)
//        ) {
//            item {
//                Spacer(modifier = Modifier.height(16.dp))
//
//
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//    }
//}
//}



package com.example.project_graduation.presentation.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_graduation.presentation.admin.booking_management.BookingsManagementContent
import com.example.project_graduation.presentation.admin.booking_management.BookingsManagementViewModel
import com.example.project_graduation.presentation.admin.hotel_management.HotelsManagementContent
import com.example.project_graduation.presentation.admin.hotel_management.HotelsManagementViewModel
import com.example.project_graduation.presentation.admin.room_management.RoomsManagementViewModel
import com.example.project_graduation.presentation.admin.user_management.UsersManagementContent
import com.example.project_graduation.presentation.admin.user_management.UsersManagementViewModel
import com.example.project_graduation.presentation.profile.ProfileViewModel

// Sample data classes for preview
data class DashboardStat(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
    val change: String = ""
)

data class RecentBooking(
    val bookingId: String,
    val userName: String,
    val hotelName: String,
    val checkIn: String,
    val status: String,
    val amount: String
)

data class Hotel(
    val hotelId: Int,
    val hotelName: String,
    val hotelAddress: String,
    val description: String,
    val rating: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    profileViewModel: ProfileViewModel,
    hotelsManagementViewModel: HotelsManagementViewModel,
    usersManagementViewModel: UsersManagementViewModel,
    bookingsManagementViewModel: BookingsManagementViewModel,
    roomsManagementViewModel: RoomsManagementViewModel,
    initialTab: Int = 0,
    onNavigateToRooms: (Int, String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    LaunchedEffect(Unit) {
        Log.d("ContentValues", "Admin screen loaded")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Admin Dashboard",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Hotel Management System",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                ),
                actions = {
                    IconButton(onClick = {
                        Log.d("ContentValues", "Admin logout clicked")

                        // Call logout from ViewModel
                        profileViewModel.logout {
                            onLogout()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1976D2),
                        selectedTextColor = Color(0xFF1976D2),
                        indicatorColor = Color(0xFF1976D2).copy(alpha = 0.1f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            Icons.Default.BookOnline,
                            contentDescription = "Bookings"
                        )
                    },
                    label = { Text("Bookings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1976D2),
                        selectedTextColor = Color(0xFF1976D2),
                        indicatorColor = Color(0xFF1976D2).copy(alpha = 0.1f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            Icons.Default.Hotel,
                            contentDescription = "Hotels"
                        )
                    },
                    label = { Text("Hotels") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1976D2),
                        selectedTextColor = Color(0xFF1976D2),
                        indicatorColor = Color(0xFF1976D2).copy(alpha = 0.1f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            Icons.Default.People,
                            contentDescription = "Users"
                        )
                    },
                    label = { Text("Users") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1976D2),
                        selectedTextColor = Color(0xFF1976D2),
                        indicatorColor = Color(0xFF1976D2).copy(alpha = 0.1f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            // Content based on selected tab
            when (selectedTab) {
                0 -> DashboardContent()
                1 -> BookingsManagementContent(bookingsManagementViewModel)
                2 -> HotelsManagementContent(hotelsManagementViewModel,
                    onNavigateToRooms = onNavigateToRooms)
                3 -> UsersManagementContent(usersManagementViewModel)
            }
        }
    }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFFF5F5F5))
//                .padding(paddingValues)
//        ) {
//            // Tab Row
//            ScrollableTabRow(
//                selectedTabIndex = selectedTab,
//                containerColor = Color.White,
//                contentColor = Color(0xFF1976D2),
//                edgePadding = 16.dp
//            ) {
//                Tab(
//                    selected = selectedTab == 0,
//                    onClick = { selectedTab = 0 },
//                    text = { Text("Dashboard") },
//                    icon = { Icon(Icons.Default.Dashboard, null) }
//                )
//                Tab(
//                    selected = selectedTab == 1,
//                    onClick = { selectedTab = 1 },
//                    text = { Text("Bookings") },
//                    icon = { Icon(Icons.Default.BookOnline, null) }
//                )
//                Tab(
//                    selected = selectedTab == 2,
//                    onClick = { selectedTab = 2 },
//                    text = { Text("Hotels") },
//                    icon = { Icon(Icons.Default.Hotel, null) }
//                )
//                Tab(
//                    selected = selectedTab == 3,
//                    onClick = { selectedTab = 3 },
//                    text = { Text("Users") },
//                    icon = { Icon(Icons.Default.People, null) }
//                )
//            }
//
//            // Content based on selected tab
//            when (selectedTab) {
//                0 -> DashboardContent()
//                1 -> BookingsContent()
//                2 -> HotelsContent()
//                3 -> UsersContent()
//            }
//        }
//    }
}

@Composable
fun DashboardContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Grid
        item {
            Text(
                "Overview",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    stat = DashboardStat(
                        title = "Total Bookings",
                        value = "1,234",
                        icon = Icons.Default.BookOnline,
                        color = Color(0xFF4CAF50),
                        change = "+12%"
                    ),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    stat = DashboardStat(
                        title = "Revenue",
                        value = "$45.2K",
                        icon = Icons.Default.AttachMoney,
                        color = Color(0xFF2196F3),
                        change = "+8%"
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    stat = DashboardStat(
                        title = "Total Hotels",
                        value = "15",
                        icon = Icons.Default.Hotel,
                        color = Color(0xFFFF9800)
                    ),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    stat = DashboardStat(
                        title = "Active Users",
                        value = "892",
                        icon = Icons.Default.People,
                        color = Color(0xFF9C27B0),
                        change = "+5%"
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Bookings
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Recent Bookings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(getSampleBookings()) { booking ->
            RecentBookingCard(booking)
        }
        // Pagination
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* Previous page */ },
                    enabled = false // Disable for demo
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous",
                        tint = if (false) Color(0xFF1976D2) else Color.Gray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Page numbers
                    for (page in 1..3) {
                        Surface(
                            shape = CircleShape,
                            color = if (page == 1) Color(0xFF1976D2) else Color.Transparent,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = page.toString(),
                                    color = if (page == 1) Color.White else Color.Gray,
                                    fontWeight = if (page == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = { /* Next page */ },
                    enabled = true
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next",
                        tint = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    stat: DashboardStat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(stat.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        stat.icon,
                        contentDescription = null,
                        tint = stat.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (stat.change.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            stat.change,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    stat.value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    stat.title,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun RecentBookingCard(booking: RecentBooking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    booking.bookingId,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    booking.userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    booking.hotelName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    booking.checkIn,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (booking.status) {
                        "CONFIRMED" -> Color(0xFFE8F5E9)
                        "PENDING" -> Color(0xFFFFF3E0)
                        "CANCELLED" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE3F2FD)
                    }
                ) {
                    Text(
                        booking.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (booking.status) {
                            "CONFIRMED" -> Color(0xFF4CAF50)
                            "PENDING" -> Color(0xFFFF9800)
                            "CANCELLED" -> Color(0xFFF44336)
                            else -> Color(0xFF2196F3)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    booking.amount,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
fun BookingsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Bookings Management (Coming Soon)", fontSize = 18.sp, color = Color.Gray)
    }
}

//@Composable
//fun HotelsContent() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text("Hotels Management (Coming Soon)", fontSize = 18.sp, color = Color.Gray)
//    }
//}

//@Composable
//fun UsersContent() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text("Users Management (Coming Soon)", fontSize = 18.sp, color = Color.Gray)
//    }
//}

// Sample Data
fun getSampleBookings(): List<RecentBooking> {
    return listOf(
        RecentBooking(
            bookingId = "#BK001",
            userName = "John Smith",
            hotelName = "Grand Palace Hotel",
            checkIn = "Jan 15, 2026",
            status = "CONFIRMED",
            amount = "$750"
        ),
        RecentBooking(
            bookingId = "#BK002",
            userName = "Maria Garcia",
            hotelName = "Beach Resort Miami",
            checkIn = "Jan 18, 2026",
            status = "PENDING",
            amount = "$540"
        ),
        RecentBooking(
            bookingId = "#BK003",
            userName = "Chen Wei",
            hotelName = "Royal Palace London",
            checkIn = "Jan 20, 2026",
            status = "CONFIRMED",
            amount = "$1,200"
        ),
        RecentBooking(
            bookingId = "#BK004",
            userName = "Ahmed Hassan",
            hotelName = "Sakura Inn Tokyo",
            checkIn = "Jan 22, 2026",
            status = "CANCELLED",
            amount = "$420"
        )
    )
}

// ============= PREVIEWS =============

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    MaterialTheme {
//        AdminScreen(onLogout = {})
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun DashboardContentPreview() {
    MaterialTheme {
        Surface(color = Color(0xFFF5F5F5)) {
            DashboardContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatCardPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                stat = DashboardStat(
                    title = "Total Bookings",
                    value = "1,234",
                    icon = Icons.Default.BookOnline,
                    color = Color(0xFF4CAF50),
                    change = "+12%"
                ),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                stat = DashboardStat(
                    title = "Revenue",
                    value = "$45.2K",
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFF2196F3),
                    change = "+8%"
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentBookingCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RecentBookingCard(
                RecentBooking(
                    bookingId = "#BK001",
                    userName = "John Smith",
                    hotelName = "Grand Palace Hotel",
                    checkIn = "Jan 15, 2026",
                    status = "CONFIRMED",
                    amount = "$750"
                )
            )
            RecentBookingCard(
                RecentBooking(
                    bookingId = "#BK002",
                    userName = "Maria Garcia",
                    hotelName = "Beach Resort Miami",
                    checkIn = "Jan 18, 2026",
                    status = "PENDING",
                    amount = "$540"
                )
            )
        }
    }
}
