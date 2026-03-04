package com.example.project_graduation.presentation.staff.staff_room_management

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.project_graduation.presentation.staff.StaffRoom
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


// ============= TAB 2: ROOMS =============

@Composable
fun StaffRoomsContent(viewModel: StaffRoomsViewModel) {
    val rooms by viewModel.rooms.collectAsState()
    val successMsg by viewModel.operationSuccess.collectAsState()

    val statusFilters = listOf("All", "AVAILABLE", "OCCUPIED", "MAINTENANCE")
    var selectedFilter by remember { mutableStateOf("All") }

    val filtered =
        if (selectedFilter == "All") rooms else rooms.filter { it.status == selectedFilter }

    Column(Modifier.fillMaxSize()) {
        // Filter row
        LazyRow(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(statusFilters) { f ->
                val chipColor = when (f) {
                    "AVAILABLE" -> Color(0xFF4CAF50)
                    "OCCUPIED" -> Color(0xFF2196F3)
                    "MAINTENANCE" -> Color(0xFFFF9800)
                    else -> Primary
                }
                FilterChip(
                    selected = selectedFilter == f,
                    onClick = { selectedFilter = f },
                    label = {
                        Text(
                            when (f) {
                                "All" -> "Tất Cả (${rooms.size})"
                                "AVAILABLE" -> "Trống (${rooms.count { it.status == "AVAILABLE" }})"
                                "OCCUPIED" -> "Có Khách (${rooms.count { it.status == "OCCUPIED" }})"
                                "MAINTENANCE" -> "Bảo Trì (${rooms.count { it.status == "MAINTENANCE" }})"
                                else -> f
                            },
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = chipColor.copy(alpha = 0.15f),
                        selectedLabelColor = chipColor
                    )
                )
            }
        }

        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
            items(filtered) { room ->
                StaffRoomCard(
                    room = room,
                    onUpdateStatus = { viewModel.updateRoomStatus(room.roomId, it) })
            }
        }
    }
}

@Composable
fun StaffRoomCard(room: StaffRoom, onUpdateStatus: (String) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    val (statusColor, statusLabel) = when (room.status) {
        "AVAILABLE" -> Pair(Color(0xFF4CAF50), "Trống")
        "OCCUPIED" -> Pair(Color(0xFF2196F3), "Có Khách")
        "MAINTENANCE" -> Pair(Color(0xFFFF9800), "Bảo Trì")
        else -> Pair(Color.Gray, room.status)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Room number
            Box(
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        room.roomNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                    Text("T${room.floor}", fontSize = 10.sp, color = statusColor.copy(alpha = 0.7f))
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    room.roomType,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    "${room.capacity} người · ${formatVND(room.basePrice)}/đêm",
                    fontSize = 12.sp, color = Color.Gray
                )
                if (room.status == "OCCUPIED" && room.currentGuest != null) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            Modifier.size(13.dp),
                            tint = Color(0xFF2196F3)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            room.currentGuest,
                            fontSize = 12.sp,
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.Medium
                        )
                        room.checkOutDate?.let {
                            Text(" · CO: $it", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.12f)) {
                    Text(
                        statusLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.MoreVert,
                            null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        if (room.status != "AVAILABLE") {
                            DropdownMenuItem(
                                text = { Text("✅ Đánh dấu Trống") },
                                onClick = { onUpdateStatus("AVAILABLE"); showMenu = false }
                            )
                        }
                        if (room.status != "OCCUPIED") {
                            DropdownMenuItem(
                                text = { Text("🛏️ Đánh dấu Có Khách") },
                                onClick = { onUpdateStatus("OCCUPIED"); showMenu = false }
                            )
                        }
                        if (room.status != "MAINTENANCE") {
                            DropdownMenuItem(
                                text = { Text("🔧 Đánh dấu Bảo Trì") },
                                onClick = { onUpdateStatus("MAINTENANCE"); showMenu = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

