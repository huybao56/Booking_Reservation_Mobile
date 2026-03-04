package com.example.project_graduation.presentation.staff.staff_booking_management

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.project_graduation.presentation.staff.StaffBooking
//import com.example.project_graduation.presentation.staff.StaffViewModel

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


// ============= TAB 1: BOOKINGS =============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffBookingsContent(viewModel: StaffBookingsViewModel) {
    val bookings by viewModel.bookings.collectAsState()
    val successMsg by viewModel.operationSuccess.collectAsState()

    val statusFilters =
        listOf("All", "PENDING", "CONFIRMED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED")
    var selectedFilter by remember { mutableStateOf("All") }

    val filtered = if (selectedFilter == "All") bookings
    else bookings.filter { it.status == selectedFilter }

    // Snackbar
    LaunchedEffect(successMsg) {
        if (successMsg != null) {
            kotlinx.coroutines.delay(2500)
            viewModel.clearSuccess()
        }
    }

    Column(Modifier.fillMaxSize()) {
        // Filter chips
        LazyRow(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(statusFilters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            if (filter == "All") "Tất Cả (${bookings.size})" else "$filter (${bookings.count { it.status == filter }})",
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary.copy(alpha = 0.15f),
                        selectedLabelColor = Primary
                    )
                )
            }
        }

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Không có booking nào", color = Color.Gray, fontSize = 15.sp)
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (successMsg != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Row(
                                Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                                Spacer(Modifier.width(8.dp))
                                Text(successMsg!!, color = Color(0xFF2E7D32), fontSize = 14.sp)
                            }
                        }
                    }
                }
                items(filtered) { booking ->
                    StaffBookingCard(
                        booking = booking,
                        onUpdateStatus = { newStatus ->
                            viewModel.updateBookingStatus(
                                booking.bookingId,
                                newStatus
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StaffBookingCard(booking: StaffBooking, onUpdateStatus: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (booking.status) {
        "CONFIRMED" -> Color(0xFF4CAF50)
        "PENDING" -> Color(0xFFFF9800)
        "CHECKED_IN" -> Color(0xFF2196F3)
        "CHECKED_OUT" -> Color(0xFF9C27B0)
        "CANCELLED" -> Color(0xFFF44336)
        else -> Color.Gray
    }
    val statusBg = statusColor.copy(alpha = 0.12f)
    val statusLabel = when (booking.status) {
        "CONFIRMED" -> "Đã Xác Nhận"
        "PENDING" -> "Chờ Duyệt"
        "CHECKED_IN" -> "Đã Check-In"
        "CHECKED_OUT" -> "Đã Check-Out"
        "CANCELLED" -> "Đã Hủy"
        else -> booking.status
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(booking.bookingId, fontSize = 12.sp, color = Color.Gray)
                    Text(
                        booking.guestName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }
                Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
                    Text(
                        statusLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            // Info row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BookingInfoItem(
                    Icons.Default.MeetingRoom,
                    "Phòng ${booking.roomNumber}",
                    "${booking.roomType}"
                )
                BookingInfoItem(
                    Icons.Default.DateRange,
                    "${booking.checkIn}",
                    "→ ${booking.checkOut}"
                )
                BookingInfoItem(
                    Icons.Default.People,
                    "${booking.guests} khách",
                    formatVND(booking.totalAmount)
                )
            }

            // Special requests
            booking.specialRequests?.let { req ->
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF8E1)) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            null,
                            Modifier.size(14.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(req, fontSize = 12.sp, color = Color(0xFF795548))
                    }
                }
            }

            // Action buttons
            if (booking.status == "PENDING" || booking.status == "CONFIRMED") {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (booking.status) {
                        "PENDING" -> {
                            OutlinedButton(
                                onClick = { onUpdateStatus("CANCELLED") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(
                                        0xFFF44336
                                    )
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFFF44336)
                                )
                            ) { Text("Từ Chối", fontSize = 13.sp) }
                            Button(
                                onClick = { onUpdateStatus("CONFIRMED") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF4CAF50
                                    )
                                )
                            ) { Text("Xác Nhận", fontSize = 13.sp) }
                        }

                        "CONFIRMED" -> {
                            Button(
                                onClick = { onUpdateStatus("CHECKED_IN") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Icon(Icons.Default.Login, null, Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Check-In", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
            if (booking.status == "CHECKED_IN") {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onUpdateStatus("CHECKED_OUT") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Icon(Icons.Default.Logout, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Check-Out", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun BookingInfoItem(icon: ImageVector, line1: String, line2: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, Modifier.size(18.dp), tint = Primary)
        Spacer(Modifier.height(3.dp))
        Text(line1, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
        Text(line2, fontSize = 11.sp, color = Color.Gray)
    }
}

