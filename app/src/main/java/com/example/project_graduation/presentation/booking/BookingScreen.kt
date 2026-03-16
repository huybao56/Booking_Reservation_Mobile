//package com.example.project_graduation.presentation.booking
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.project_graduation.data.local.PreferencesManager
//import com.example.project_graduation.data.remote.api.BookingApi
//import com.example.project_graduation.data.remote.dto.BookingDto
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BookingScreen(
//    preferencesManager: PreferencesManager,
//    onBack: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val scope = rememberCoroutineScope()
//    val bookingApi = remember { BookingApi() }
//
//    var bookings by remember { mutableStateOf<List<BookingDto>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//
//
//    // Load bookings when screen opens
//    LaunchedEffect(Unit) {
//        scope.launch {
//            try {
//                val user = preferencesManager.getUser()
//                if (user != null) {
//                    val result = bookingApi.getBookingsByUserId(user.userId)
//                    if (result.isSuccess) {
//                        bookings = result.getOrNull() ?: emptyList()
//                        Log.d("BookingScreen", "Loaded ${bookings.size} bookings")
//                    } else {
//                        errorMessage = result.exceptionOrNull()?.message
//                        Log.e("BookingScreen", "Failed to load bookings: $errorMessage")
//                    }
//                } else {
//                    errorMessage = "Please login to view bookings"
//                }
//            } catch (e: Exception) {
//                errorMessage = e.message
//                Log.e("BookingScreen", "Error loading bookings: ${e.message}", e)
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//
//    Scaffold(
//        contentWindowInsets = WindowInsets(0),
//        topBar = {
//            TopAppBar(
//                title = { Text("My Bookings",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 22.sp) },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White,
//                    titleContentColor = Color.Black,
//                )
//            )
//        }
//    ) { paddingValues ->
//        when {
//            isLoading -> {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
//
//            errorMessage != null -> {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.Error,
//                            contentDescription = null,
//                            modifier = Modifier.size(64.dp),
//                            tint = Color.Red
//                        )
//                        Text(
//                            text = errorMessage ?: "Unknown error",
//                            fontSize = 16.sp,
//                            color = Color.Gray
//                        )
//                        Button(onClick = {
//                            isLoading = true
//                            errorMessage = null
//                            scope.launch {
//                                val user = preferencesManager.getUser()
//                                if (user != null) {
//                                    val result = bookingApi.getBookingsByUserId(user.userId)
//                                    if (result.isSuccess) {
//                                        bookings = result.getOrNull() ?: emptyList()
//                                    }
//                                }
//                                isLoading = false
//                            }
//                        }) {
//                            Text("Retry")
//                        }
//                    }
//                }
//            }
//
//            bookings.isEmpty() -> {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Icon(
//                            Icons.Default.BookmarkBorder,
//                            contentDescription = null,
//                            modifier = Modifier.size(64.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text("No bookings yet", fontSize = 18.sp, color = Color.Gray)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            "Book your first room to see it here!",
//                            fontSize = 14.sp,
//                            color = Color.LightGray
//                        )
//                    }
//                }
//            }
//
//            else -> {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color(0xFFF5F5F5))
//                        .padding(paddingValues),
//                    contentPadding = PaddingValues(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    items(bookings) { booking ->
//                        BookingCard(booking = booking)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BookingCard(booking: BookingDto) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            // Header: Booking ID & Status
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Booking #${booking.bookingId}",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//
//                // Status Badge
//                Surface(
//                    shape = RoundedCornerShape(8.dp),
//                    color = when (booking.status) {
//                        "CONFIRMED" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
//                        "PENDING" -> Color(0xFFFFC107).copy(alpha = 0.1f)
//                        "CANCELLED" -> Color(0xFFF44336).copy(alpha = 0.1f)
//                        "CHECKED_IN" -> Color(0xFF2196F3).copy(alpha = 0.1f)
//                        "CHECKED_OUT" -> Color.Gray.copy(alpha = 0.1f)
//                        else -> Color.Gray.copy(alpha = 0.1f)
//                    }
//                ) {
//                    Text(
//                        text = booking.status,
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = when (booking.status) {
//                            "CONFIRMED" -> Color(0xFF4CAF50)
//                            "PENDING" -> Color(0xFFFFC107)
//                            "CANCELLED" -> Color(0xFFF44336)
//                            "CHECKED_IN" -> Color(0xFF2196F3)
//                            "CHECKED_OUT" -> Color.Gray
//                            else -> Color.Gray
//                        },
//                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Room Info
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    Icons.Default.MeetingRoom,
//                    contentDescription = null,
//                    tint = Color(0xFF2196F3),
//                    modifier = Modifier.size(20.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Room ID: ${booking.roomId}",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            HorizontalDivider(color = Color.LightGray)
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Check-in/out
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Check-in
//                Column {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.Login,
//                            contentDescription = null,
//                            tint = Color(0xFF2196F3),
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Check-in", fontSize = 12.sp, color = Color.Gray)
//                    }
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        booking.checkIn,
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }
//
//                // Check-out
//                Column(horizontalAlignment = Alignment.End) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.Logout,
//                            contentDescription = null,
//                            tint = Color(0xFF2196F3),
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Check-out", fontSize = 12.sp, color = Color.Gray)
//                    }
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        booking.checkOut,
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            HorizontalDivider(color = Color.LightGray)
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Price & Created Date
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text("Total:", fontSize = 12.sp, color = Color.Gray)
//                    Text(
//                        "$${"%.2f".format(booking.totalPrice)}",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF2196F3)
//                    )
//                }
//
//                Column(horizontalAlignment = Alignment.End) {
//                    Text("Booked on:", fontSize = 12.sp, color = Color.Gray)
//                    Text(
//                        booking.createdAt.take(10), // Only show date
//                        fontSize = 12.sp,
//                        color = Color.Gray
//                    )
//                }
//            }
//
//            // Booking Group ID (if exists)
//            if (!booking.bookingGroupId.isNullOrEmpty()) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
//                        .padding(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        Icons.Default.Group,
//                        contentDescription = null,
//                        tint = Color.Gray,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        "Group: ${booking.bookingGroupId.take(8)}...",
//                        fontSize = 11.sp,
//                        color = Color.Gray
//                    )
//                }
//            }
//        }
//    }
//}

package com.example.project_graduation.presentation.booking

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.api.BookingApi
import com.example.project_graduation.data.remote.dto.BookingDto
import kotlinx.coroutines.launch

// Data class cho filter status
data class BookingStatusFilterItem(
    val label: String,
    val value: String?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

// Danh sách các trạng thái
fun getUserBookingStatusFilters(): List<BookingStatusFilterItem> {
    return listOf(
        BookingStatusFilterItem(
            label = "All",
            value = null,
            icon = Icons.Default.List,
            color = Color(0xFF6B7280)
        ),
        BookingStatusFilterItem(
            label = "Confirmed",
            value = "CONFIRMED",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF10B981)
        ),
        BookingStatusFilterItem(
            label = "Cancelled",
            value = "CANCELLED",
            icon = Icons.Default.Cancel,
            color = Color(0xFFEF4444)
        ),
        BookingStatusFilterItem(
            label = "Checked In",
            value = "CHECKED_IN",
            icon = Icons.Default.Login,
            color = Color(0xFF3B82F6)
        ),
        BookingStatusFilterItem(
            label = "Checked Out",
            value = "CHECKED_OUT",
            icon = Icons.Default.Logout,
            color = Color(0xFF8B5CF6)
        ),
        BookingStatusFilterItem(
            label = "No Show",
            value = "NO_SHOW",
            icon = Icons.Default.Schedule,
            color = Color(0xFFF44336)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    preferencesManager: PreferencesManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val bookingApi = remember { BookingApi() }

    var bookings by remember { mutableStateOf<List<BookingDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var cancellingBooking by remember { mutableStateOf<BookingDto?>(null) }
    var showCancelSuccess by remember { mutableStateOf(false) }

    // Load bookings function
    fun loadBookings() {
        scope.launch {
            try {
                isLoading = true
                errorMessage = null
                val user = preferencesManager.getUser()
                if (user != null) {
                    val result = bookingApi.getBookingsByUserId(user.userId)
                    if (result.isSuccess) {
                        bookings = result.getOrNull() ?: emptyList()
                        Log.d("BookingScreen", "Loaded ${bookings.size} bookings")
                    } else {
                        errorMessage = result.exceptionOrNull()?.message
                        Log.e("BookingScreen", "Failed to load bookings: $errorMessage")
                    }
                } else {
                    errorMessage = "Please login to view bookings"
                }
            } catch (e: Exception) {
                errorMessage = e.message
                Log.e("BookingScreen", "Error loading bookings: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    // Load bookings when screen opens
    LaunchedEffect(Unit) {
        loadBookings()
    }

    // Auto-hide success message
    LaunchedEffect(showCancelSuccess) {
        if (showCancelSuccess) {
            kotlinx.coroutines.delay(3000)
            showCancelSuccess = false
        }
    }

    // Filter bookings by status
    val filteredBookings = remember(bookings, selectedStatus) {
        if (selectedStatus == null) {
            bookings
        } else {
            bookings.filter { it.status == selectedStatus }
        }
    }



    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Bookings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Red
                            )
                            Text(
                                text = errorMessage ?: "Unknown error",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Button(onClick = { loadBookings() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                bookings.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No booking yet", fontSize = 18.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Book your first room to see it here!",
                                fontSize = 14.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }

                else -> {
                    // Success Message
                    if (showCancelSuccess) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Booking cancelled successfully",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Status Filter
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            "Filter by Status:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(getUserBookingStatusFilters()) { filter ->
                                UserBookingStatusChip(
                                    filter = filter,
                                    isSelected = selectedStatus == filter.value,
                                    onClick = { selectedStatus = filter.value }
                                )
                            }
                        }
                    }

                    // Bookings List
                    if (filteredBookings.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No booking found", fontSize = 18.sp, color = Color.Gray)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredBookings) { booking ->
                                EnhancedBookingCard(
                                    booking = booking,
                                    onCancelClick = {
                                        if (booking.status == "CONFIRMED" || booking.status == "PENDING") {
                                            cancellingBooking = booking
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Cancel Confirmation Dialog
    if (cancellingBooking != null) {
        AlertDialog(
            onDismissRequest = { cancellingBooking = null },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text("Cancel Booking?", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Are you sure you want to cancel this booking?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Booking #${cancellingBooking!!.bookingId}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        "${cancellingBooking!!.checkIn} → ${cancellingBooking!!.checkOut}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val result = bookingApi.updateBookingStatus(
                                    cancellingBooking!!.bookingId,
                                    "CANCELLED"
                                )
                                if (result.isSuccess) {
                                    showCancelSuccess = true
                                    loadBookings() // Reload bookings
                                } else {
                                    // TODO: Show error
                                    Log.e("BookingScreen", "Failed to cancel booking")
                                }
                            } catch (e: Exception) {
                                Log.e("BookingScreen", "Error cancelling: ${e.message}")
                            }
                            cancellingBooking = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Cancel Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { cancellingBooking = null }) {
                    Text("Keep Booking")
                }
            }
        )
    }
}

@Composable
fun UserBookingStatusChip(
    filter: BookingStatusFilterItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) filter.color.copy(alpha = 0.15f) else Color.White,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, filter.color)
        } else null,
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                filter.icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isSelected) filter.color else Color(0xFF6B7280)
            )
            Text(
                filter.label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) filter.color else Color(0xFF374151)
            )
        }
    }
}

@Composable
fun EnhancedBookingCard(
    booking: BookingDto,
    onCancelClick: () -> Unit
) {
    val canCancel = booking.status == "CONFIRMED" || booking.status == "PENDING"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Booking ID & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Booking #${booking.bookingId}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (booking.status) {
                        "CONFIRMED" -> Color(0xFF10B981).copy(alpha = 0.12f)
                        "PENDING" -> Color(0xFFF59E0B).copy(alpha = 0.12f)
                        "CANCELLED" -> Color(0xFFEF4444).copy(alpha = 0.12f)
                        "CHECKED_IN" -> Color(0xFF3B82F6).copy(alpha = 0.12f)
                        "CHECKED_OUT" -> Color(0xFF8B5CF6).copy(alpha = 0.12f)
                        else -> Color.Gray.copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = booking.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (booking.status) {
                            "CONFIRMED" -> Color(0xFF10B981)
                            "PENDING" -> Color(0xFFF59E0B)
                            "CANCELLED" -> Color(0xFFEF4444)
                            "CHECKED_IN" -> Color(0xFF3B82F6)
                            "CHECKED_OUT" -> Color(0xFF8B5CF6)
                            else -> Color.Gray
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hotel & Room Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Hotel,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = booking.hotelName ?: "Hotel Name",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Room ${booking.roomNumber ?: booking.roomId}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // Check-in/out
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Check-in
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Login,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-in", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        booking.checkIn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }

                // Check-out
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-out", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        booking.checkOut,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // Price & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price
                Column {
                    Text("Total:", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "$${"%.2f".format(booking.totalPrice)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }

                // Cancel Button
                if (canCancel) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Booked Date
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Booked on: ${booking.createdAt.take(10)}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            // Booking Group ID (if exists)
            if (!booking.bookingGroupId.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Group: ${booking.bookingGroupId.take(8)}...",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}