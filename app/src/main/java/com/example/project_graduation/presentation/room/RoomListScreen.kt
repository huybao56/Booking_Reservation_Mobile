package com.example.project_graduation.presentation.room

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.project_graduation.domain.model.Room
import com.example.project_graduation.domain.repository.RoomAvailability

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    hotelId: Int,
    hotelName: String,
    viewModel: RoomListViewModel,
    onBack: () -> Unit,
    onRoomSelected: (RoomAvailability) -> Unit
) {
    val state by viewModel.state.collectAsState()


    val filteredRooms = viewModel.getFilteredRooms()
    val roomTypes = state.roomAvailabilities.map { it.room.roomType  }.distinct()

//    // Load rooms when screen opens
//    LaunchedEffect(hotelId) {
//        viewModel.loadAvailableRoomTypes(hotelId)
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Available Rooms", fontSize = 18.sp)
                        Text(
                            hotelName,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(state.error ?: "Unknown error")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAvailableRoomTypes(hotelId) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            state.roomAvailabilities.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.BedroomParent,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No rooms available for selected dates",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                        .padding(paddingValues)
                ) {
                    // Date Selection Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Booking Dates",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Check-in
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Check-in",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        state.checkIn,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }

                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                // Check-out
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Check-out",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        state.checkOut,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE3F2FD)
                            ) {
                                Text(
                                    "${state.numberOfNights} night${if (state.numberOfNights > 1) "s" else ""}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Room Type Filter
                    if (roomTypes.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = state.selectedRoomType == null,
                                    onClick = { viewModel.filterByRoomType(null) },
                                    label = { Text("All (${state.roomAvailabilities.size})") }
                                )
                            }

                            items(roomTypes) { roomType ->
                                val count =
                                    state.roomAvailabilities.count { it.room.roomType == roomType }
                                FilterChip(
                                    selected = state.selectedRoomType == roomType,
                                    onClick = { viewModel.filterByRoomType(roomType) },
                                    label = { Text("$roomType ($count)") }
                                )
                            }
                        }
                    }
                    // Rooms List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredRooms) { roomAvailability ->
                            RoomAvailabilityCard(
                                roomAvailability = roomAvailability,
                                numberOfNights = state.numberOfNights,
                                onClick = { onRoomSelected(roomAvailability) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomAvailabilityCard(
    roomAvailability: RoomAvailability,
    numberOfNights: Int,
    onClick: () -> Unit
) {
    val room = roomAvailability.room

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Top
//            ) {
//                // Room Image Placeholder
//                Box(
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clip(RoundedCornerShape(12.dp))
//                        .background(Color(0xFFE0E0E0)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        Icons.Default.BedroomParent,
//                        contentDescription = null,
//                        modifier = Modifier.size(48.dp),
//                        tint = Color.Gray
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                // Room Info
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    // Room Type Badge
//                    Surface(
//                        shape = RoundedCornerShape(8.dp),
//                        color = when {
//                            room.roomType.contains("Standard", ignoreCase = true) -> Color(0xFFE3F2FD)
//                            room.roomType.contains("Deluxe", ignoreCase = true) -> Color(0xFFFFF3E0)
//                            room.roomType.contains("Suite", ignoreCase = true) -> Color(0xFFF3E5F5)
//                            room.roomType.contains("Executive", ignoreCase = true) -> Color(0xFFE1F5FE)
//                            room.roomType.contains("Family", ignoreCase = true) -> Color(0xFFFCE4EC)
//                            else -> Color(0xFFE0E0E0)
//                        }
//                    ) {
//                        Text(
//                            text = room.roomType,
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = when {
//                                room.roomType.contains("Standard", ignoreCase = true) -> Color(0xFF1976D2)
//                                room.roomType.contains("Deluxe", ignoreCase = true) -> Color(0xFFF57C00)
//                                room.roomType.contains("Suite", ignoreCase = true) -> Color(0xFF7B1FA2)
//                                room.roomType.contains("Executive", ignoreCase = true) -> Color(0xFF0277BD)
//                                room.roomType.contains("Family", ignoreCase = true) -> Color(0xFFC2185B)
//                                else -> Color.Gray
//                            },
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Capacity
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.Person,
//                            contentDescription = null,
//                            modifier = Modifier.size(16.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = "Up to ${room.capacity} guests",
//                            fontSize = 14.sp,
//                            color = Color.Gray
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    // Available Units
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.MeetingRoom,
//                            contentDescription = null,
//                            modifier = Modifier.size(16.dp),
//                            tint = if (roomAvailability.availableUnits <= 3) Color(0xFFFF5252) else Color(0xFF4CAF50)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = "${roomAvailability.availableUnits} rooms available",
//                            fontSize = 14.sp,
//                            color = if (roomAvailability.availableUnits <= 3) Color(0xFFFF5252) else Color(0xFF4CAF50),
//                            fontWeight = if (roomAvailability.availableUnits <= 3) FontWeight.Bold else FontWeight.Normal
//                        )
//                    }
//                }
//            }
//
//            // Description
//            room.description?.let { description ->
//                Spacer(modifier = Modifier.height(12.dp))
//                Text(
//                    text = description,
//                    fontSize = 13.sp,
//                    color = Color.Gray,
//                    maxLines = 2,
//                    lineHeight = 18.sp
//                )
//            }
//
//            // Amenities
//            if (room.amenities.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(12.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(6.dp)
//                ) {
//                    room.amenities.take(4).forEach { amenity ->
//                        Surface(
//                            shape = RoundedCornerShape(6.dp),
//                            color = Color(0xFFF5F5F5)
//                        ) {
//                            Row(
//                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Icon(
//                                    imageVector = when (amenity.lowercase()) {
//                                        "wifi" -> Icons.Default.Wifi
//                                        "tv" -> Icons.Default.Tv
//                                        "ac" -> Icons.Default.AcUnit
//                                        "mini bar" -> Icons.Default.LocalBar
//                                        "balcony" -> Icons.Default.Balcony
//                                        "jacuzzi" -> Icons.Default.HotTub
//                                        "kitchen" -> Icons.Default.Kitchen
//                                        "coffee maker" -> Icons.Default.Coffee
//                                        "safe" -> Icons.Default.Lock
//                                        else -> Icons.Default.Check
//                                    },
//                                    contentDescription = null,
//                                    modifier = Modifier.size(12.dp),
//                                    tint = Color.Gray
//                                )
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Text(
//                                    text = amenity,
//                                    fontSize = 11.sp,
//                                    color = Color.Gray
//                                )
//                            }
//                        }
//                    }
//
//                    if (room.amenities.size > 4) {
//                        Surface(
//                            shape = RoundedCornerShape(6.dp),
//                            color = Color(0xFFE3F2FD)
//                        ) {
//                            Text(
//                                text = "+${room.amenities.size - 4}",
//                                fontSize = 11.sp,
//                                color = Color(0xFF1976D2),
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                            )
//                        }
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Divider(color = Color.LightGray.copy(alpha = 0.5f))
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Price Section
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Bottom
//            ) {
//                Column {
//                    Text(
//                        text = "$${roomAvailability.pricePerNight} / night",
//                        fontSize = 13.sp,
//                        color = Color.Gray
//                    )
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Row(verticalAlignment = Alignment.Bottom) {
//                        Text(
//                            text = "$${String.format("%.2f", roomAvailability.totalPrice)}",
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color(0xFF2196F3)
//                        )
//                        Text(
//                            text = " total",
//                            fontSize = 14.sp,
//                            color = Color.Gray
//                        )
//                    }
//                    Text(
//                        text = "for $numberOfNights night${if (numberOfNights > 1) "s" else ""}",
//                        fontSize = 12.sp,
//                        color = Color.LightGray
//                    )
//                }
//
//                // Select Button
//                Button(
//                    onClick = onClick,
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF2196F3)
//                    ),
//                    shape = RoundedCornerShape(8.dp)
//                ) {
//                    Text("Select", fontSize = 14.sp)
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Icon(
//                        Icons.Default.ChevronRight,
//                        contentDescription = null,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            }
//        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Room Image với màu theo loại phòng
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            room.roomType.contains("Standard", ignoreCase = true) -> Color(0xFFE3F2FD)
                            room.roomType.contains("Deluxe", ignoreCase = true) -> Color(0xFFFFF3E0)
                            room.roomType.contains("Suite", ignoreCase = true) -> Color(0xFFF3E5F5)
                            room.roomType.contains("Executive", ignoreCase = true) -> Color(0xFFE1F5FE)
                            room.roomType.contains("Family", ignoreCase = true) -> Color(0xFFFCE4EC)
                            room.roomType.contains("Garden", ignoreCase = true) -> Color(0xFFCAECCB)
                            else -> Color(0xFFE0E0E0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ✅ HIỂN THỊ SỐ PHÒNG
                    Text(
                        text = room.roomNumber,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            room.roomType.contains("Standard", ignoreCase = true) -> Color(0xFF1976D2)
                            room.roomType.contains("Deluxe", ignoreCase = true) -> Color(0xFFF57C00)
                            room.roomType.contains("Suite", ignoreCase = true) -> Color(0xFF7B1FA2)
                            room.roomType.contains("Executive", ignoreCase = true) -> Color(0xFF0277BD)
                            room.roomType.contains("Family", ignoreCase = true) -> Color(0xFFC2185B)
                            room.roomType.contains("Garden", ignoreCase = true) -> Color(0xFF4CAF50)
                            else -> Color.Gray
                        }
                    )
                    Text(
                        text = "Room",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Room Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Room Type Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            room.roomType.contains("Standard", ignoreCase = true) -> Color(0xFFE3F2FD)
                            room.roomType.contains("Deluxe", ignoreCase = true) -> Color(0xFFFFF3E0)
                            room.roomType.contains("Suite", ignoreCase = true) -> Color(0xFFF3E5F5)
                            room.roomType.contains("Executive", ignoreCase = true) -> Color(0xFFE1F5FE)
                            room.roomType.contains("Family", ignoreCase = true) -> Color(0xFFFCE4EC)
                            room.roomType.contains("Garden", ignoreCase = true) -> Color(0xF2E0FFE2)
                            else -> Color(0xFFE0E0E0)
                        }
                    ) {
                        Text(
                            text = room.roomType,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                room.roomType.contains("Standard", ignoreCase = true) -> Color(0xFF1976D2)
                                room.roomType.contains("Deluxe", ignoreCase = true) -> Color(0xFFF57C00)
                                room.roomType.contains("Suite", ignoreCase = true) -> Color(0xFF7B1FA2)
                                room.roomType.contains("Executive", ignoreCase = true) -> Color(0xFF0277BD)
                                room.roomType.contains("Family", ignoreCase = true) -> Color(0xFFC2185B)
                                room.roomType.contains("Garden", ignoreCase = true) -> Color(0xFF4CAF50)
                                else -> Color.Gray
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Floor
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Layers,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Floor ${room.floor}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Capacity
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Up to ${room.capacity} guests",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Quick Amenities
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        room.amenities.take(3).forEach { amenity ->
                            Icon(
                                imageVector = when (amenity.lowercase()) {
                                    "wifi" -> Icons.Default.Wifi
                                    "tv" -> Icons.Default.Tv
                                    "ac" -> Icons.Default.AcUnit
                                    else -> Icons.Default.Check
                                },
                                contentDescription = amenity,
                                modifier = Modifier.size(14.dp),
                                tint = Color.Gray
                            )
                        }
                        if (room.amenities.size > 3) {
                            Text(
                                text = "+${room.amenities.size - 3}",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${String.format("%.0f", roomAvailability.pricePerNight)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                            Text(
                                text = "/night",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = "$${String.format("%.0f", roomAvailability.totalPrice)} total",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    // Select Button
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Select", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun RoomListPreview() {
    val rooms = listOf(
        RoomAvailability(
            room = Room(
                roomId = 1,
                hotelId = 1,
                roomNumber = "101",
                roomType = "Standard Room",
                floor = 1,
                status = "AVAILABLE",
                basePrice = 250.0,
                capacity = 2,
                description = "Comfortable standard room with city view",
                amenities = listOf("Wifi", "TV", "AC", "Mini Bar")
            ),
            availableUnits = 10,
            pricePerNight = 250.0,
            totalPrice = 500.0
        ),
        RoomAvailability(
            room = Room(
                roomId = 92,
                hotelId = 2,
                roomNumber = "101",
                roomType = "Garden View Room",
                floor = 1,
                status = "AVAILABLE",
                basePrice = 180.0,
                capacity = 2,
                description = "Peaceful room with beautiful garden views",
                amenities = listOf("Wifi", "TV", "AC", "Garden View")
            ),
            availableUnits = 10,
            pricePerNight = 180.0,
            totalPrice = 360.0
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rooms) { room ->
                RoomAvailabilityCard(
                    roomAvailability = room,
                    numberOfNights = 2,
                    onClick = {}
                )
            }
        }
    }
}


