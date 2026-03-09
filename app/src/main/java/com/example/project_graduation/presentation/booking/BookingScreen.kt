//package com.example.project_graduation.presentation.booking
//
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
//
//data class Booking(
//    val bookingId: Int,
//    val hotelName: String,
//    val roomType: String,
//    val roomNumber: String,
//    val checkIn: String,
//    val checkOut: String,
//    val totalPrice: Double,
//    val status: String,
//    val quantity: Int
//)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BookingScreen(
//    userId: Int,
//    onBack: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    // TODO: Load bookings from API
//    LaunchedEffect(userId) {
//        // Call API here
//        isLoading = false
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("My Bookings") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFF2196F3),
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White
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
//fun BookingCard(booking: Booking) {
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
//            // Status Badge
//            Surface(
//                shape = RoundedCornerShape(8.dp),
//                color = when (booking.status) {
//                    "CONFIRMED" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
//                    "PENDING" -> Color(0xFFFFC107).copy(alpha = 0.1f)
//                    "CANCELLED" -> Color(0xFFF44336).copy(alpha = 0.1f)
//                    else -> Color.Gray.copy(alpha = 0.1f)
//                }
//            ) {
//                Text(
//                    text = booking.status,
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = when (booking.status) {
//                        "CONFIRMED" -> Color(0xFF4CAF50)
//                        "PENDING" -> Color(0xFFFFC107)
//                        "CANCELLED" -> Color(0xFFF44336)
//                        else -> Color.Gray
//                    },
//                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Hotel & Room Info
//            Text(
//                text = booking.hotelName,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            Text(
//                text = "${booking.roomType} - Room ${booking.roomNumber}",
//                fontSize = 14.sp,
//                color = Color.Gray
//            )
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
//            // Price
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Total:", fontSize = 14.sp, color = Color.Gray)
//                Text(
//                    "$${"%.2f".format(booking.totalPrice)}",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF2196F3)
//                )
//            }
//        }
//    }
//}
//
//


package com.example.project_graduation.presentation.booking

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.api.BookingApi
import com.example.project_graduation.data.remote.dto.BookingDto
import kotlinx.coroutines.launch

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

    // Load bookings when screen opens
    LaunchedEffect(Unit) {
        scope.launch {
            try {
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

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("My Bookings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp) },
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
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                        Button(onClick = {
                            isLoading = true
                            errorMessage = null
                            scope.launch {
                                val user = preferencesManager.getUser()
                                if (user != null) {
                                    val result = bookingApi.getBookingsByUserId(user.userId)
                                    if (result.isSuccess) {
                                        bookings = result.getOrNull() ?: emptyList()
                                    }
                                }
                                isLoading = false
                            }
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }

            bookings.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                        Text("No bookings yet", fontSize = 18.sp, color = Color.Gray)
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { booking ->
                        BookingCard(booking = booking)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (booking.status) {
                        "CONFIRMED" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        "PENDING" -> Color(0xFFFFC107).copy(alpha = 0.1f)
                        "CANCELLED" -> Color(0xFFF44336).copy(alpha = 0.1f)
                        "CHECKED_IN" -> Color(0xFF2196F3).copy(alpha = 0.1f)
                        "CHECKED_OUT" -> Color.Gray.copy(alpha = 0.1f)
                        else -> Color.Gray.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = booking.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (booking.status) {
                            "CONFIRMED" -> Color(0xFF4CAF50)
                            "PENDING" -> Color(0xFFFFC107)
                            "CANCELLED" -> Color(0xFFF44336)
                            "CHECKED_IN" -> Color(0xFF2196F3)
                            "CHECKED_OUT" -> Color.Gray
                            else -> Color.Gray
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Room Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Room ID: ${booking.roomId}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.LightGray)

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
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-in", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        booking.checkIn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Check-out
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-out", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        booking.checkOut,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(12.dp))

            // Price & Created Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total:", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "$${"%.2f".format(booking.totalPrice)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Booked on:", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        booking.createdAt.take(10), // Only show date
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Booking Group ID (if exists)
            if (!booking.bookingGroupId.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Group: ${booking.bookingGroupId.take(8)}...",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}