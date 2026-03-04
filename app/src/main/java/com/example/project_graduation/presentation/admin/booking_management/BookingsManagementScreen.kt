package com.example.project_graduation.presentation.admin.booking_management

//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Dialog
//import com.example.project_graduation.data.remote.dto.BookingDto
//import com.example.project_graduation.data.remote.dto.BookingPaymentDto
//import com.example.project_graduation.presentation.admin.user_management.DetailRow
//
//// Booking with Payment info
//data class BookingWithPayment(
//    val booking: BookingDto,
//    val payment: BookingPaymentDto?
//)
//
//@Composable
//fun BookingsManagementContent(
//    viewModel: BookingsManagementViewModel
//) {
//    val bookings by viewModel.bookings.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    var searchQuery by remember { mutableStateOf("") }
//    var filterStatus by remember { mutableStateOf<String?>(null) }
//    var filterPaymentStatus by remember { mutableStateOf<String?>(null) }
//    var viewingBooking by remember { mutableStateOf<BookingWithPayment?>(null) }
//    var deletingBooking by remember { mutableStateOf<BookingDto?>(null) }
//
//    // Load bookings when screen opens
//    LaunchedEffect(Unit) {
//        viewModel.loadAllBookings()
//    }
//
//    val filteredBookings = bookings.filter { bookingWithPayment ->
//        val matchesSearch = bookingWithPayment.booking.bookingId.toString().contains(searchQuery, ignoreCase = true) ||
//                bookingWithPayment.booking.userId.toString().contains(searchQuery, ignoreCase = true) ||
//                bookingWithPayment.booking.roomId.toString().contains(searchQuery, ignoreCase = true)
//
//        val matchesStatus = filterStatus == null || bookingWithPayment.booking.status == filterStatus
//
//        val matchesPayment = filterPaymentStatus == null ||
//                bookingWithPayment.payment?.status == filterPaymentStatus
//
//        matchesSearch && matchesStatus && matchesPayment
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // Header with Stats
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column {
//                Text(
//                    "Bookings Management",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//                Text(
//                    "${bookings.size} total bookings",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//            }
//
//            // Stats Summary
//            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                StatChip(
//                    label = "Confirmed",
//                    count = bookings.count { it.booking.status == "CONFIRMED" },
//                    color = Color(0xFF4CAF50)
//                )
//                StatChip(
//                    label = "Pending",
//                    count = bookings.count { it.booking.status == "PENDING" },
//                    color = Color(0xFFFFC107)
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Search Bar
//        OutlinedTextField(
//            value = searchQuery,
//            onValueChange = { searchQuery = it },
//            placeholder = { Text("Search by Booking ID, User ID, Room ID...", fontSize = 14.sp) },
//            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
//            trailingIcon = {
//                if (searchQuery.isNotEmpty()) {
//                    IconButton(onClick = { searchQuery = "" }) {
//                        Icon(Icons.Default.Clear, contentDescription = "Clear")
//                    }
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                unfocusedBorderColor = Color.LightGray,
//                focusedBorderColor = Color(0xFF1976D2),
//                unfocusedContainerColor = Color.White,
//                focusedContainerColor = Color.White
//            ),
//            singleLine = true
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // Filter Chips Row 1: Booking Status
//        Text("Booking Status:", fontSize = 12.sp, color = Color.Gray)
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            FilterChip(
//                selected = filterStatus == null,
//                onClick = { filterStatus = null },
//                label = { Text("All") }
//            )
//            FilterChip(
//                selected = filterStatus == "CONFIRMED",
//                onClick = { filterStatus = "CONFIRMED" },
//                label = { Text("Confirmed") },
//                leadingIcon = {
//                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
//                }
//            )
//            FilterChip(
//                selected = filterStatus == "PENDING",
//                onClick = { filterStatus = "PENDING" },
//                label = { Text("Pending") },
//                leadingIcon = {
//                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
//                }
//            )
//            FilterChip(
//                selected = filterStatus == "CANCELLED",
//                onClick = { filterStatus = "CANCELLED" },
//                label = { Text("Cancelled") },
//                leadingIcon = {
//                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
//                }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Filter Chips Row 2: Payment Status
//        Text("Payment Status:", fontSize = 12.sp, color = Color.Gray)
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            FilterChip(
//                selected = filterPaymentStatus == null,
//                onClick = { filterPaymentStatus = null },
//                label = { Text("All Payments") }
//            )
//            FilterChip(
//                selected = filterPaymentStatus == "PENDING",
//                onClick = { filterPaymentStatus = "PENDING" },
//                label = { Text("Unpaid") },
//                leadingIcon = {
//                    Icon(Icons.Default.MoneyOff, contentDescription = null, modifier = Modifier.size(16.dp))
//                }
//            )
//            FilterChip(
//                selected = filterPaymentStatus == "COMPLETED",
//                onClick = { filterPaymentStatus = "COMPLETED" },
//                label = { Text("Paid") },
//                leadingIcon = {
//                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
//                }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Content based on state
//        when {
//            isLoading -> {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator(color = Color(0xFF1976D2))
//                }
//            }
//
//            error != null -> {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Icon(
//                            Icons.Default.Error,
//                            contentDescription = null,
//                            modifier = Modifier.size(64.dp),
//                            tint = Color.Red
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(error ?: "Unknown error", fontSize = 16.sp, color = Color.Gray)
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Button(
//                            onClick = { viewModel.loadAllBookings() },
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
//                        ) {
//                            Text("Retry")
//                        }
//                    }
//                }
//            }
//
//            filteredBookings.isEmpty() -> {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Icon(
//                            Icons.Default.BookmarkBorder,
//                            contentDescription = null,
//                            modifier = Modifier.size(64.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text("No bookings found", fontSize = 18.sp, color = Color.Gray)
//                    }
//                }
//            }
//
//            else -> {
//                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                    items(filteredBookings) { bookingWithPayment ->
//                        BookingManagementCard(
//                            bookingWithPayment = bookingWithPayment,
//                            onView = { viewingBooking = bookingWithPayment },
//                            onDelete = { deletingBooking = bookingWithPayment.booking }
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    // View Details Dialog
//    if (viewingBooking != null) {
//        BookingDetailsDialog(
//            bookingWithPayment = viewingBooking!!,
//            onDismiss = { viewingBooking = null }
//        )
//    }
//
//    // Delete Confirmation Dialog (Coming Soon)
//    if (deletingBooking != null) {
//        AlertDialog(
//            onDismissRequest = { deletingBooking = null },
//            icon = {
//                Icon(
//                    Icons.Default.Warning,
//                    contentDescription = null,
//                    tint = Color(0xFFF44336),
//                    modifier = Modifier.size(48.dp)
//                )
//            },
//            title = { Text("Delete Booking?", fontWeight = FontWeight.Bold) },
//            text = {
//                Text("Are you sure you want to delete Booking #${deletingBooking!!.bookingId}? This action cannot be undone.")
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        // TODO: Implement API call for delete
//                        deletingBooking = null
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
//                ) {
//                    Text("Delete")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { deletingBooking = null }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}
//
//@Composable
//fun StatChip(
//    label: String,
//    count: Int,
//    color: Color
//) {
//    Surface(
//        shape = RoundedCornerShape(8.dp),
//        color = color.copy(alpha = 0.1f)
//    ) {
//        Row(
//            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            Text(
//                label,
//                fontSize = 12.sp,
//                color = color,
//                fontWeight = FontWeight.Medium
//            )
//            Text(
//                count.toString(),
//                fontSize = 14.sp,
//                color = color,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}
//
//@Composable
//fun BookingManagementCard(
//    bookingWithPayment: BookingWithPayment,
//    onView: () -> Unit,
//    onDelete: () -> Unit
//) {
//    val booking = bookingWithPayment.booking
//    val payment = bookingWithPayment.payment
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
//            // Header: Booking ID + Status
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Box(
//                        modifier = Modifier
//                            .size(40.dp)
//                            .clip(CircleShape)
//                            .background(Color(0xFFE3F2FD)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            Icons.Default.BookOnline,
//                            contentDescription = null,
//                            tint = Color(0xFF1976D2),
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(12.dp))
//                    Column {
//                        Text(
//                            "Booking #${booking.bookingId}",
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                        Text(
//                            "User ID: ${booking.userId}",
//                            fontSize = 12.sp,
//                            color = Color.Gray
//                        )
//                    }
//                }
//
//                // Booking Status Badge
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
//                        booking.status ?: "UNKNOWN",
//                        fontSize = 11.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = when (booking.status) {
//                            "CONFIRMED" -> Color(0xFF4CAF50)
//                            "PENDING" -> Color(0xFFFFC107)
//                            "CANCELLED" -> Color(0xFFF44336)
//                            "CHECKED_IN" -> Color(0xFF2196F3)
//                            "CHECKED_OUT" -> Color.Gray
//                            else -> Color.Gray
//                        },
//                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Room & Dates Info
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Room Info
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        Icons.Default.MeetingRoom,
//                        contentDescription = null,
//                        tint = Color.Gray,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("Room ${booking.roomId}", fontSize = 13.sp, color = Color.Gray)
//                }
//
//                // Date Range
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        Icons.Default.CalendarMonth,
//                        contentDescription = null,
//                        tint = Color.Gray,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        "${booking.checkIn} → ${booking.checkOut}",
//                        fontSize = 12.sp,
//                        color = Color.Gray,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Payment Info + Actions
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Payment Status
//                Column {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            if (payment?.status == "COMPLETED") Icons.Default.Payment else Icons.Default.MoneyOff,
//                            contentDescription = null,
//                            tint = if (payment?.status == "COMPLETED") Color(0xFF4CAF50) else Color(0xFFFFC107),
//                            modifier = Modifier.size(18.dp)
//                        )
//                        Spacer(modifier = Modifier.width(6.dp))
//                        Text(
//                            if (payment != null) "${payment.status}" else "No Payment",
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = if (payment?.status == "COMPLETED") Color(0xFF4CAF50) else Color(0xFFFFC107)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Text(
//                        "$${"%.2f".format(booking.totalPrice ?: 0.0)}",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF1976D2)
//                    )
//
//                    if (payment != null) {
//                        Text(
//                            payment.provider ?: "Unknown Provider",
//                            fontSize = 11.sp,
//                            color = Color.Gray
//                        )
//                    }
//                }
//
//                // Action Buttons
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    IconButton(onClick = onView, modifier = Modifier.size(36.dp)) {
//                        Icon(
//                            Icons.Default.Visibility,
//                            contentDescription = "View",
//                            tint = Color.Gray,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
//                        Icon(
//                            Icons.Default.Delete,
//                            contentDescription = "Delete",
//                            tint = Color(0xFFF44336),
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }
//            }
//
//            // Booking Group ID (if exists)
//            if (!booking.bookingGroupId.isNullOrEmpty()) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(0xFFF5F5F5), RoundedCornerShape(6.dp))
//                        .padding(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        Icons.Default.Group,
//                        contentDescription = null,
//                        tint = Color.Gray,
//                        modifier = Modifier.size(14.dp)
//                    )
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text(
//                        "Group: ${booking.bookingGroupId.take(13)}...",
//                        fontSize = 10.sp,
//                        color = Color.Gray,
//                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BookingDetailsDialog(
//    bookingWithPayment: BookingWithPayment,
//    onDismiss: () -> Unit
//) {
//    val booking = bookingWithPayment.booking
//    val payment = bookingWithPayment.payment
//
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier.fillMaxWidth().padding(16.dp),
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White)
//        ) {
//            LazyColumn(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
//                item {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text("Booking Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//                        IconButton(onClick = onDismiss) {
//                            Icon(Icons.Default.Close, contentDescription = "Close")
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    // Booking Icon
//                    Box(
//                        modifier = Modifier
//                            .size(80.dp)
//                            .clip(CircleShape)
//                            .background(Color(0xFFE3F2FD))
//                            .align(Alignment.CenterHorizontally),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            Icons.Default.BookOnline,
//                            contentDescription = null,
//                            tint = Color(0xFF1976D2),
//                            modifier = Modifier.size(40.dp)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    DetailRow(Icons.Default.Tag, "Booking ID", "#${booking.bookingId}")
//                    DetailRow(Icons.Default.Person, "User ID", "#${booking.userId}")
//                    DetailRow(Icons.Default.MeetingRoom, "Room ID", "#${booking.roomId}")
//                    DetailRow(Icons.Default.Info, "Status", booking.status ?: "UNKNOWN")
//                    DetailRow(Icons.Default.Login, "Check-in", booking.checkIn)
//                    DetailRow(Icons.Default.Logout, "Check-out", booking.checkOut)
//                    DetailRow(Icons.Default.AttachMoney, "Total Price", "$${"%.2f".format(booking.totalPrice ?: 0.0)}")
//                    DetailRow(Icons.Default.CalendarToday, "Created At", booking.createdAt.take(16))
//
//                    if (!booking.bookingGroupId.isNullOrEmpty()) {
//                        DetailRow(Icons.Default.Group, "Group ID", booking.bookingGroupId.take(20) + "...")
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    HorizontalDivider()
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Payment Info
//                    Text(
//                        "Payment Information",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Black
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    if (payment != null) {
//                        DetailRow(Icons.Default.Payment, "Payment ID", "#${payment.paymentId}")
//                        DetailRow(Icons.Default.CreditCard, "Provider", payment.provider ?: "Unknown")
//                        DetailRow(Icons.Default.AttachMoney, "Amount", "$${"%.2f".format(payment.amount ?: 0.0)}")
//                        DetailRow(Icons.Default.Info, "Status", payment.status ?: "UNKNOWN")
//                        DetailRow(Icons.Default.Fingerprint, "Transaction ID", payment.transactionId ?: "N/A")
//                        DetailRow(Icons.Default.Schedule, "Paid At", payment.paidAt?.take(16) ?: "Not paid yet")
//                    } else {
//                        Text(
//                            "No payment information available",
//                            fontSize = 14.sp,
//                            color = Color.Gray,
//                            modifier = Modifier.align(Alignment.CenterHorizontally)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Button(
//                        onClick = onDismiss,
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Text("Close")
//                    }
//                }
//            }
//        }
//    }
//}

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.project_graduation.data.remote.dto.BookingDto
import com.example.project_graduation.data.remote.dto.BookingPaymentDto
import com.example.project_graduation.presentation.admin.user_management.DetailRow

// Booking with Payment info
data class BookingWithPayment(
    val booking: BookingDto,
    val payment: BookingPaymentDto?
)

@Composable
fun BookingsManagementContent(
    viewModel: BookingsManagementViewModel
) {
    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf<String?>(null) }
    var filterPaymentStatus by remember { mutableStateOf<String?>(null) }
    var viewingBooking by remember { mutableStateOf<BookingWithPayment?>(null) }
    var deletingBooking by remember { mutableStateOf<BookingDto?>(null) }

    // Load bookings when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadAllBookings()
    }

    val filteredBookings = bookings.filter { bookingWithPayment ->
        val matchesSearch = bookingWithPayment.booking.bookingId.toString().contains(searchQuery, ignoreCase = true) ||
                bookingWithPayment.booking.userId.toString().contains(searchQuery, ignoreCase = true) ||
                bookingWithPayment.booking.roomId.toString().contains(searchQuery, ignoreCase = true)

        val matchesStatus = filterStatus == null || bookingWithPayment.booking.status == filterStatus

        val matchesPayment = filterPaymentStatus == null ||
                bookingWithPayment.payment?.status == filterPaymentStatus

        matchesSearch && matchesStatus && matchesPayment
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Column {
                    // Title
                    Text(
                        "Bookings Management",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${bookings.size} total bookings",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatChip(
                            label = "Confirmed",
                            count = bookings.count { it.booking.status == "CONFIRMED" },
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "Pending",
                            count = bookings.count { it.booking.status == "PENDING" },
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Booking ID, User ID, Room ID...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF1976D2),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Filter Chips Row 1: Booking Status
        Text("Booking Status:", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = filterStatus == null,
                onClick = { filterStatus = null },
                label = { Text("All") }
            )
            FilterChip(
                selected = filterStatus == "CONFIRMED",
                onClick = { filterStatus = "CONFIRMED" },
                label = { Text("Confirm") },
                leadingIcon = {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
            FilterChip(
                selected = filterStatus == "PENDING",
                onClick = { filterStatus = "PENDING" },
                label = { Text("Pending") },
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
            FilterChip(
                selected = filterStatus == "CANCELLED",
                onClick = { filterStatus = "CANCELLED" },
                label = { Text("Cancel") },
                leadingIcon = {
                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

//        // Filter Chips Row 2: Payment Status
//        Text("Payment Status:", fontSize = 12.sp, color = Color.Gray)
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            FilterChip(
//                selected = filterPaymentStatus == null,
//                onClick = { filterPaymentStatus = null },
//                label = { Text("All Payments") }
//            )
//            FilterChip(
//                selected = filterPaymentStatus == "PENDING",
//                onClick = { filterPaymentStatus = "PENDING" },
//                label = { Text("Unpaid") },
//                leadingIcon = {
//                    Icon(Icons.Default.MoneyOff, contentDescription = null, modifier = Modifier.size(16.dp))
//                }
//            )
//            FilterChip(
//                selected = filterPaymentStatus == "COMPLETED",
//                onClick = { filterPaymentStatus = "COMPLETED" },
//                label = { Text("Paid") },
//                leadingIcon = {
//                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
//                }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))

        // Content based on state
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(error ?: "Unknown error", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadAllBookings() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            filteredBookings.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No bookings found", fontSize = 18.sp, color = Color.Gray)
                    }
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredBookings) { bookingWithPayment ->
                        BookingManagementCard(
                            bookingWithPayment = bookingWithPayment,
                            onView = { viewingBooking = bookingWithPayment },
                            onDelete = { deletingBooking = bookingWithPayment.booking }
                        )
                    }
                }
            }
        }
    }

    // View Details Dialog
    if (viewingBooking != null) {
        BookingDetailsDialog(
            bookingWithPayment = viewingBooking!!,
            onDismiss = { viewingBooking = null }
        )
    }

    // Delete Confirmation Dialog (Coming Soon)
    if (deletingBooking != null) {
        AlertDialog(
            onDismissRequest = { deletingBooking = null },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Delete Booking?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete Booking #${deletingBooking!!.bookingId}? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Implement API call for delete
                        deletingBooking = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingBooking = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                fontSize = 12.sp,
                color = color,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                count.toString(),
                fontSize = 15.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BookingManagementCard(
    bookingWithPayment: BookingWithPayment,
    onView: () -> Unit,
    onDelete: () -> Unit
) {
    val booking = bookingWithPayment.booking
    val payment = bookingWithPayment.payment

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header Row: Booking ID + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Booking #${booking.bookingId}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

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
                        booking.status ?: "UNKNOWN",
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

            // User ID
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("User ID: ${booking.userId}", fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Room ID
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Room ${booking.roomId}", fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Check-in & Check-out
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "${booking.checkIn} → ${booking.checkOut}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Payment Status + Amount
                Column {
                    // Payment Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (payment?.status == "COMPLETED") Icons.Default.Payment else Icons.Default.MoneyOff,
                            contentDescription = null,
                            tint = if (payment?.status == "COMPLETED") Color(0xFF4CAF50) else Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (payment != null) payment.status ?: "UNKNOWN" else "No Payment",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (payment?.status == "COMPLETED") Color(0xFF4CAF50) else Color(0xFFFFC107)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Total Amount
                    Text(
                        "${"%.2f".format(booking.totalPrice ?: 0.0)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )

                    // Payment Provider
                    if (payment != null) {
                        Text(
                            payment.provider ?: "Unknown",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Right: Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onView, modifier = Modifier.size(40.dp)) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = "View",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Booking Group ID (nếu có)
            if (!booking.bookingGroupId.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF5F5F5)
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
                            "Group: ${booking.bookingGroupId.take(13)}...",
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

@Composable
fun BookingDetailsDialog(
    bookingWithPayment: BookingWithPayment,
    onDismiss: () -> Unit
) {
    val booking = bookingWithPayment.booking
    val payment = bookingWithPayment.payment

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Booking Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Booking Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD))
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.BookOnline,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailRow(Icons.Default.Tag, "Booking ID", "#${booking.bookingId}")
                    DetailRow(Icons.Default.Person, "User ID", "#${booking.userId}")
                    DetailRow(Icons.Default.MeetingRoom, "Room ID", "#${booking.roomId}")
                    DetailRow(Icons.Default.Info, "Status", booking.status ?: "UNKNOWN")
                    DetailRow(Icons.Default.Login, "Check-in", booking.checkIn)
                    DetailRow(Icons.Default.Logout, "Check-out", booking.checkOut)
                    DetailRow(Icons.Default.AttachMoney, "Total Price", "$${"%.2f".format(booking.totalPrice ?: 0.0)}")
                    DetailRow(Icons.Default.CalendarToday, "Created At", booking.createdAt.take(16))

                    if (!booking.bookingGroupId.isNullOrEmpty()) {
                        DetailRow(Icons.Default.Group, "Group ID", booking.bookingGroupId.take(20) + "...")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Info
                    Text(
                        "Payment Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (payment != null) {
                        DetailRow(Icons.Default.Payment, "Payment ID", "#${payment.paymentId}")
                        DetailRow(Icons.Default.CreditCard, "Provider", payment.provider ?: "Unknown")
                        DetailRow(Icons.Default.AttachMoney, "Amount", "$${"%.2f".format(payment.amount ?: 0.0)}")
                        DetailRow(Icons.Default.Info, "Status", payment.status ?: "UNKNOWN")
                        DetailRow(Icons.Default.Fingerprint, "Transaction ID", payment.transactionId ?: "N/A")
                        DetailRow(Icons.Default.Schedule, "Paid At", payment.paidAt?.take(16) ?: "Not paid yet")
                    } else {
                        Text(
                            "No payment information available",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}