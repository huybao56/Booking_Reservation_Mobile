package com.example.project_graduation.presentation.staff.staff_dashboard_management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.example.project_graduation.presentation.staff.StaffBookingCard
import com.example.project_graduation.presentation.staff.StaffViewModel
import com.example.project_graduation.presentation.staff.staff_booking_management.StaffBookingCard
import com.example.project_graduation.presentation.staff.staff_booking_management.StaffBookingsViewModel


// ============= Staff Color Palette =============
private val Primary = Color(0xFF00897B)    // Teal 600
private val PrimaryDk = Color(0xFF00695C)    // Teal 800
private val Accent = Color(0xFF4DB6AC)    // Teal 300
private val LightBg = Color(0xFFF0FAF9)
private val CardBg = Color.White

// ============= TAB 0: DASHBOARD =============

@Composable
fun StaffDashboardContent(
    viewModel: StaffDashboardViewModel,
    bookingsViewModel: StaffBookingsViewModel
//    viewModel: StaffViewModel
) {
//    val stats by viewModel.dashboardStats.collectAsState()
//    val bookings by viewModel.bookings.collectAsState()
//    val staffInfo by viewModel.staffInfo.collectAsState()

    val stats    by viewModel.stats.collectAsStateWithLifecycle()
    val bookings by bookingsViewModel.bookings.collectAsStateWithLifecycle()
    val staffInfo by viewModel.profile.collectAsStateWithLifecycle()

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header greeting
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Primary, PrimaryDk)))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            "Hello, ${staffInfo.username.split(" ").last()}! 👋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Today you have ${stats.todayCheckIns} customer check-in and ${stats.todayCheckOuts} customer check-out.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(Modifier.height(12.dp))
                        // Quick stats row
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            QuickStatChip(
                                "CheckIn",
                                stats.todayCheckIns.toString(),
                                Icons.Default.Login
                            )
                            QuickStatChip(
                                "CheckOut",
                                stats.todayCheckOuts.toString(),
                                Icons.Default.Logout
                            )
                            QuickStatChip(
                                "Pending",
                                stats.pendingBookings.toString(),
                                Icons.Default.Pending
                            )
                        }
                    }
                }
            }
        }

        item {
            // Room status cards
            Text(
                "Room's Status",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StaffStatCard(
                    title = "Available",
                    value = stats.availableRooms.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StaffStatCard(
                    title = "Occupied",
                    value = stats.occupiedRooms.toString(),
                    icon = Icons.Default.People,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                StaffStatCard(
                    title = "Maintenance",
                    value = stats.maintenanceRooms.toString(),
                    icon = Icons.Default.Build,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            // Revenue
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AttachMoney,
                            null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Revenue's Month", fontSize = 13.sp, color = Color.Gray)
                        Text(
                            "${stats.totalRevenue}$",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                }
            }
        }

        // Recent bookings
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Booking Recent",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.weight(1f)
                )
                Text("View All", fontSize = 13.sp, color = Primary)
            }
        }

        items(bookings.take(4)) { booking ->
            StaffBookingCard(booking = booking, onUpdateStatus = {})
        }
    }
}

@Composable
private fun QuickStatChip(label: String, value: String, icon: ImageVector) {
    Row(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(14.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
    }
}

@Composable
fun StaffStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.padding(14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.padding(10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
                }
                Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = color)
            }
//            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}



