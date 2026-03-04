package com.example.project_graduation.presentation.staff.staff_profile_management

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.project_graduation.presentation.profile.ProfileViewModel
import com.example.project_graduation.presentation.staff.StaffInfo
import com.example.project_graduation.presentation.staff.staff_dashboard_management.StaffDashboardViewModel

// ============= Staff Color Palette =============
private val Primary = Color(0xFF00897B)    // Teal 600
private val PrimaryDk = Color(0xFF00695C)    // Teal 800
private val Accent = Color(0xFF4DB6AC)    // Teal 300
private val LightBg = Color(0xFFF0FAF9)
private val CardBg = Color.White

// ============= TAB 4: PROFILE =============

@Composable
fun StaffProfileContent(
    dashboardViewModel: StaffDashboardViewModel,
    profileViewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val profile by dashboardViewModel.profile.collectAsState()
    val profileState by profileViewModel.state.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Profile header card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Primary, PrimaryDk)))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SupportAgent,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
//                            staffInfo.staffName,
                            profile.username.ifBlank { profileState.user?.username ?: "Staff" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
//                                staffInfo.role
                                profile.position.replace("_", " "),
                                fontSize = 13.sp, color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            // Hotel info card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Hotel Information",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Divider()
                    ProfileInfoRow(Icons.Default.Hotel, "Hotel", profile.hotelName.ifBlank { "-" })
                    ProfileInfoRow(
                        Icons.Default.Badge,
                        "StaffId",
                        "#STAFF${profile.staffId.toString().padStart(4, '0')}"
                    )
                }
            }
        }

        item {
            // Contact info
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Contact Information",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Divider()
                    ProfileInfoRow(
                        Icons.Default.Email,
                        "Email",
                        profile.email.ifBlank { "-" })
//                        staffInfo.email.ifBlank { "staff@hotel.com" })
                    ProfileInfoRow(
                        Icons.Default.Phone,
                        "Điện Thoại",
                        profile.phone.ifBlank { "-" })
                }
            }
        }

        item {
            // Action buttons
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Primary)
                ) {
                    Icon(Icons.Default.Lock, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Change Password")
                }
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Log Out")
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp), tint = Primary)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
        }
    }
}
