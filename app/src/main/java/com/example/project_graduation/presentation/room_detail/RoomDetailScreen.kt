package com.example.project_graduation.presentation.room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.domain.model.Room
import com.example.project_graduation.presentation.room_detail.BookNowState
import com.example.project_graduation.presentation.room_detail.RoomDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    room: Room,
    availableUnits: Int,
    pricePerNight: Double,
    totalNights: Int,
    checkIn: String,
    checkOut: String,
    userId: Int,
    viewModel: RoomDetailViewModel,
    onBack: () -> Unit,
    onBookNow: (Room, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val bookNowState by viewModel.bookNowState.collectAsState()


    // Dialog báo phòng bị người khác giữ
    if (bookNowState is BookNowState.RoomLocked) {
        AlertDialog(
            onDismissRequest = { viewModel.resetBookNowState() },
            title = { Text("Room Unavailable") },
            text = { Text("This room is currently being held by another user. Please try again in a few minutes.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetBookNowState() }) {
                    Text("OK")
                }
            }
        )
    }

    // Dialog báo lỗi khác
    if (bookNowState is BookNowState.Error) {
        val errorMsg = (bookNowState as BookNowState.Error).message
        AlertDialog(
            onDismissRequest = { viewModel.resetBookNowState() },
            title = { Text("Error") },
            text = { Text(errorMsg) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetBookNowState() }) {
                    Text("OK")
                }
            }
        )
    }

    val state by viewModel.state.collectAsState()
    LaunchedEffect(room.roomId) {
        viewModel.loadRoomDetail(room.roomId)
    }
    // Merge images từ API vào room gốc (giữ nguyên các field khác từ NavGraph)
    val roomWithImages = if (state.room?.images?.isNotEmpty() == true) {
        room.copy(
            images = state.room!!.images,
            primaryImage = state.room!!.primaryImage
        )
    } else {
        room
    }

    var selectedQuantity by remember { mutableStateOf(1) }
    val totalPrice = pricePerNight * totalNights * selectedQuantity

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
//                    Column (
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalArrangement = Arrangement.Center
//                    ){
                    Text("Room ${room.roomNumber}", fontSize = 18.sp)
                    Text(
                        room.roomType,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
//                    }
//                    Text("Room Details")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            item {
                // ===== IMAGE GALLERY =====
                if (roomWithImages.images.isNotEmpty()) {
                    var currentImageIndex by remember { mutableStateOf(0) }

                    // Bắt đầu tại ảnh primary
//                    LaunchedEffect(roomWithImages.images) {
//                        currentImageIndex = roomWithImages.images.indexOfFirst { it.isPrimary }
//                            .takeIf { it >= 0 } ?: 0
//                    }

                    // Reset về primary khi images thay đổi
                    LaunchedEffect(roomWithImages.images) {
                        val primaryIdx = roomWithImages.images.indexOfFirst { it.isPrimary }
                        currentImageIndex = if (primaryIdx >= 0) primaryIdx else 0
                    }

//                    val currentImage = roomWithImages.images[currentImageIndex]

                    // Clamp index để tránh crash khi images thay đổi size
                    val safeIndex = currentImageIndex.coerceIn(0, roomWithImages.images.size - 1)
                    val currentImage = roomWithImages.images[safeIndex]

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        // Ảnh chính
                        AsyncImage(
                            model = "${ApiConfig.BASE_URL}${currentImage.imageUrl}",
                            contentDescription = currentImage.caption ?: "Room ${room.roomNumber}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(android.R.drawable.ic_menu_gallery),
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                        )

                        // Overlay tối nhẹ
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.1f))
                        )

                        // Counter "1 / 3" góc trên trái
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "${safeIndex + 1} / ${roomWithImages.images.size}",
//                                "${currentImageIndex + 1} / ${roomWithImages.images.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }

                        // Badge "Primary" góc trên phải
                        if (currentImage.isPrimary) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "Primary",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Caption overlay phía dưới
                        currentImage.caption?.let { caption ->
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                                color = Color.Black.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    caption,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }

                        // Navigation Arrows (chỉ hiện khi có > 1 ảnh)
                        if (roomWithImages.images.size > 1) {
                            IconButton(
                                onClick = {
                                    currentImageIndex = if (currentImageIndex > 0)
                                        currentImageIndex - 1 else roomWithImages.images.size - 1
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.White.copy(alpha = 0.9f),
                                    shadowElevation = 4.dp
                                ) {
                                    Icon(
                                        Icons.Default.ChevronLeft,
                                        contentDescription = "Previous",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(24.dp),
                                        tint = Color.Black
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    currentImageIndex =
                                        if (currentImageIndex < roomWithImages.images.size - 1)
                                            currentImageIndex + 1 else 0
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.White.copy(alpha = 0.9f),
                                    shadowElevation = 4.dp
                                ) {
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "Next",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(24.dp),
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Placeholder khi không có ảnh
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color(0xFF42a5f5), Color(0xFF1565c0))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Hotel,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No Image",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }


//                // Room Image Placeholder
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(300.dp)
//                        .background(Color(0xFFE0E0E0)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        Icons.Default.Hotel,
//                        contentDescription = null,
//                        modifier = Modifier.size(100.dp),
//                        tint = Color.Gray
//                    )
//                }

                // Room Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Room Type
                        Text(
                            text = room.roomType,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Price
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$${"%.2f".format(pricePerNight)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                            Text(
                                text = " / night",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Available rooms
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MeetingRoom,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$availableUnits rooms available",
                                fontSize = 16.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Capacity
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.People,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Up to ${room.capacity} guests",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }

                        // Description
                        room.description?.let { description ->
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Description",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = description,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // Booking Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Booking Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Check-in
                        BookingInfoRow(
                            icon = Icons.Default.Login,
                            label = "Check-in",
                            value = checkIn
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Check-out
                        BookingInfoRow(
                            icon = Icons.Default.Logout,
                            label = "Check-out",
                            value = checkOut
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nights
                        BookingInfoRow(
                            icon = Icons.Default.NightsStay,
                            label = "Number of nights",
                            value = "$totalNights night${if (totalNights > 1) "s" else ""}"
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Quantity Selector
                        Text(
                            text = "Number of rooms",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Quantity selector
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                IconButton(
                                    onClick = { if (selectedQuantity > 1) selectedQuantity-- },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selectedQuantity > 1) Color(0xFF2196F3)
                                            else Color(0xFFE0E0E0)
                                        ),
                                    enabled = selectedQuantity > 1
                                ) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = "Decrease",
                                        tint = Color.White
                                    )
                                }

                                Text(
                                    text = selectedQuantity.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                IconButton(
                                    onClick = { if (selectedQuantity < availableUnits) selectedQuantity++ },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selectedQuantity < availableUnits) Color(0xFF2196F3)
                                            else Color(0xFFE0E0E0)
                                        ),
                                    enabled = selectedQuantity < availableUnits
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Increase",
                                        tint = Color.White
                                    )
                                }
                            }

                            // Total for rooms
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "$${"%.2f".format(totalPrice)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amenities Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Amenities",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display amenities from room data
                        if (room.amenities.isEmpty()) {
                            Text(
                                text = "No amenities information available",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        } else {
                            room.amenities.forEach { amenity ->
                                AmenityItem(getAmenityIcon(amenity), amenity)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

//                // Book Now Button
//                Button(
//                    onClick = { onBookNow(room, selectedQuantity) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .padding(horizontal = 16.dp),
//                    shape = RoundedCornerShape(12.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF2196F3)
//                    )
//                ) {
//                    Icon(
//                        Icons.Default.Payment,
//                        contentDescription = null,
//                        modifier = Modifier.size(24.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        "Book Now - $${"%.2f".format(totalPrice)}",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }


                // Book Now Button
                val isLocking = bookNowState is BookNowState.Locking
                Button(
                    onClick = {
                        viewModel.onBookNow(
                            userId   = userId,
                            roomId   = room.roomId,
                            checkIn  = checkIn,
                            checkOut = checkOut
                        )
                    },
                    enabled = !isLocking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    if (isLocking) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Checking availability...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Book Now - ${"$"}${"%.2f".format(totalPrice)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun BookingInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun AmenityItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

fun getAmenityIcon(amenity: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (amenity.lowercase()) {
        "wifi", "wi-fi" -> Icons.Default.Wifi
        "tv", "television" -> Icons.Default.Tv
        "ac", "air conditioning" -> Icons.Default.AcUnit
        "mini bar", "minibar" -> Icons.Default.LocalBar
        "safe", "safety box" -> Icons.Default.Lock
        "balcony" -> Icons.Default.Balcony
        "coffee maker" -> Icons.Default.Coffee
        "jacuzzi" -> Icons.Default.HotTub
        "kitchen", "kitchenette" -> Icons.Default.Kitchen
        "living room" -> Icons.Default.Weekend
        "garden view" -> Icons.Default.Park
        else -> Icons.Default.CheckCircle
    }
}


//
//// ============= PREVIEWS =============
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun RoomDetailScreenPreview() {
//    val mockRoom = Room(
//        roomId = 1,
//        hotelId = 1,
//        roomNumber = "201",
//        roomType = "Deluxe Room",
//        floor = 2,
//        status = "AVAILABLE",
//        basePrice = 350.0,
//        capacity = 3,
//        description = "Spacious deluxe room with balcony and premium amenities. Ideal for small families. Enjoy stunning city views from your private balcony.",
//        amenities = listOf("WiFi", "TV", "AC", "Mini Bar", "Balcony", "Coffee Maker", "Safe"),
////        imageUrl = null
//    )
//
//    MaterialTheme {
//        RoomDetailScreen(
//            room = mockRoom,
//            availableUnits = 5,
//            pricePerNight = 350.0,
//            totalNights = 3,
//            checkIn = "2026-01-15",
//            checkOut = "2026-01-18",
//            onBack = {},
//            onBookNow = { _, _ -> }
//        )
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun RoomDetailScreenStandardPreview() {
//    val mockRoom = Room(
//        roomId = 2,
//        hotelId = 1,
//        roomNumber = "101",
//        roomType = "Standard Room",
//        floor = 1,
//        status = "AVAILABLE",
//        basePrice = 250.0,
//        capacity = 2,
//        description = "Comfortable standard room with city view. Perfect for couples or solo travelers.",
//        amenities = listOf("WiFi", "TV", "AC", "Mini Bar", "Safe"),
////        imageUrl = null
//    )
//
//    MaterialTheme {
//        RoomDetailScreen(
//            room = mockRoom,
//            availableUnits = 10,
//            pricePerNight = 250.0,
//            totalNights = 2,
//            checkIn = "2026-01-20",
//            checkOut = "2026-01-22",
//            onBack = {},
//            onBookNow = { _, _ -> }
//        )
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun RoomDetailScreenSuitePreview() {
//    val mockRoom = Room(
//        roomId = 3,
//        hotelId = 1,
//        roomNumber = "501",
//        roomType = "Executive Suite",
//        floor = 5,
//        status = "AVAILABLE",
//        basePrice = 500.0,
//        capacity = 4,
//        description = "Luxury suite with separate living area and stunning views. Features premium amenities and spacious accommodation.",
//        amenities = listOf(
//            "WiFi",
//            "TV",
//            "AC",
//            "Mini Bar",
//            "Balcony",
//            "Jacuzzi",
//            "Kitchen",
//            "Living Room",
//            "Safe"
//        ),
////        imageUrl = null
//    )
//
//    MaterialTheme {
//        RoomDetailScreen(
//            room = mockRoom,
//            availableUnits = 2,
//            pricePerNight = 500.0,
//            totalNights = 5,
//            checkIn = "2026-02-01",
//            checkOut = "2026-02-06",
//            onBack = {},
//            onBookNow = { _, _ -> }
//        )
//    }
//}