package com.example.project_graduation.presentation.hotel


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_graduation.data.remote.ApiConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotelId: Int,
    viewModel: HotelDetailViewModel,
    onBack: () -> Unit,
    onBookNow: (Int, String) -> Unit

) {
    val state by viewModel.state.collectAsState()

    // Load hotel detail khi màn hình được tạo
    LaunchedEffect(hotelId) {
        viewModel.loadHotelDetail(hotelId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotel Details") },
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
                        Button(onClick = { viewModel.loadHotelDetail(hotelId) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            state.hotel != null -> {

                val hotel = state.hotel!!

                // Lấy ảnh primary hoặc ảnh đầu tiên (giống HomeScreen)
//                val displayImageUrl = hotel.primaryImage
//                    ?: hotel.images.firstOrNull { it.isPrimary }?.imageUrl
//                    ?: hotel.images.firstOrNull()?.imageUrl


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                        .padding(paddingValues)
                ) {
                    item {

                        // ===== IMAGE GALLERY (giống HotelsManagementScreen) =====
                        if (hotel.images.isNotEmpty()) {
                            var currentImageIndex by remember { mutableStateOf(0) }
                            // Bắt đầu tại ảnh primary
                            LaunchedEffect(hotel.images) {
                                currentImageIndex = hotel.images.indexOfFirst { it.isPrimary }
                                    .takeIf { it >= 0 } ?: 0
                            }
                            val currentImage = hotel.images[currentImageIndex]

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            ) {
                                // Ảnh chính
                                AsyncImage(
                                    model = "${ApiConfig.BASE_URL}${currentImage.imageUrl}",
                                    contentDescription = currentImage.caption ?: hotel.hotelName,
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

                                // Badge "Primary"
                                if (currentImage.isPrimary) {
                                    Surface(
                                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
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

                                // Counter "1 / 3"
                                Surface(
                                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "${currentImageIndex + 1} / ${hotel.images.size}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }

                                // Caption overlay
                                currentImage.caption?.let { caption ->
                                    Surface(
                                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
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
                                if (hotel.images.size > 1) {
                                    // Mũi tên trái
                                    IconButton(
                                        onClick = {
                                            currentImageIndex = if (currentImageIndex > 0)
                                                currentImageIndex - 1 else hotel.images.size - 1
                                        },
                                        modifier = Modifier.align(Alignment.CenterStart).padding(4.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color.White.copy(alpha = 0.9f),
                                            shadowElevation = 4.dp
                                        ) {
                                            Icon(
                                                Icons.Default.ChevronLeft,
                                                contentDescription = "Previous",
                                                modifier = Modifier.padding(8.dp).size(24.dp),
                                                tint = Color.Black
                                            )
                                        }
                                    }

                                    // Mũi tên phải
                                    IconButton(
                                        onClick = {
                                            currentImageIndex = if (currentImageIndex < hotel.images.size - 1)
                                                currentImageIndex + 1 else 0
                                        },
                                        modifier = Modifier.align(Alignment.CenterEnd).padding(4.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color.White.copy(alpha = 0.9f),
                                            shadowElevation = 4.dp
                                        ) {
                                            Icon(
                                                Icons.Default.ChevronRight,
                                                contentDescription = "Next",
                                                modifier = Modifier.padding(8.dp).size(24.dp),
                                                tint = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Placeholder nếu không có ảnh
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Hotel, null,
                                        modifier = Modifier.size(100.dp),
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No Image", fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }

//                        // Hotel Image (Placeholder)
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(300.dp)
//                                .background(Color(0xFFE0E0E0)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                Icons.Default.Hotel,
//                                contentDescription = null,
//                                modifier = Modifier.size(100.dp),
//                                tint = Color.Gray
//                            )
//                        }


                        // Hotel Image - UPDATED
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(300.dp)
//                        ) {
//                            if (displayImageUrl != null) {
//                                val fullImageUrl = "${ApiConfig.BASE_URL}$displayImageUrl"
//
//                                AsyncImage(
//                                    model = fullImageUrl,
//                                    contentDescription = hotel.hotelName,
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = ContentScale.Crop,
//                                    error = painterResource(android.R.drawable.ic_menu_gallery),
//                                    placeholder = painterResource(android.R.drawable.ic_menu_gallery)
//                                )
//                            } else {
//                                // Placeholder nếu không có ảnh - Gradient background
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .background(
//                                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
//                                                colors = listOf(
//                                                    Color(0xFF667eea),
//                                                    Color(0xFF764ba2)
//                                                )
//                                            )
//                                        ),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Column(
//                                        horizontalAlignment = Alignment.CenterHorizontally,
//                                        verticalArrangement = Arrangement.Center
//                                    ) {
//                                        Icon(
//                                            imageVector = Icons.Default.Hotel,
//                                            contentDescription = null,
//                                            modifier = Modifier.size(100.dp),
//                                            tint = Color.White.copy(alpha = 0.7f)
//                                        )
//                                        Spacer(modifier = Modifier.height(8.dp))
//                                        Text(
//                                            text = "No Image",
//                                            fontSize = 16.sp,
//                                            color = Color.White.copy(alpha = 0.7f),
//                                            fontWeight = FontWeight.Medium
//                                        )
//                                    }
//                                }
//                            }
//
//                            // Overlay tối nhẹ
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(Color.Black.copy(alpha = 0.1f))
//                            )
//                        }

                        // Hotel Info Card
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Hotel Name
                                    Text(
                                        text = state.hotel!!.hotelName,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )

//                                    // Favorite Button
//                                    IconButton(
//                                        onClick = { viewModel.toggleFavorite() },
//                                        modifier = Modifier
//                                            .size(48.dp)
//                                            .clip(CircleShape)
//                                            .background(Color(0xFFF5F5F5))
//                                    ) {
//                                        Icon(
//                                            imageVector = if (state.isFavorite)
//                                                Icons.Default.Favorite
//                                            else
//                                                Icons.Default.FavoriteBorder,
//                                            contentDescription = "Favorite",
//                                            tint = if (state.isFavorite)
//                                                Color.Red
//                                            else
//                                                Color.Gray
//                                        )
//                                    }
                                    IconButton(
                                        onClick = { /* TODO: Mở chat */ },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF5F5F5))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Chat,
                                            contentDescription = "Chat",
                                            tint = Color(0xFF1976D2)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Location
                                state.hotel!!.hotelAddress?.let { address ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFF2196F3),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = address,
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Rating
                                state.hotel!!.rating?.let { rating ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = String.format("%.1f", rating),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = " / 5.0",
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // Description
                                state.hotel!!.description?.let { description ->
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Divider(color = Color.LightGray)

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

//                        // Amenities Section
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp),
//                            shape = RoundedCornerShape(16.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(4.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(20.dp)
//                            ) {
//                                Text(
//                                    text = "Amenities",
//                                    fontSize = 18.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.Black
//                                )
//
//                                Spacer(modifier = Modifier.height(16.dp))
//
//                                // Amenity items
//                                AmenityItem(Icons.Default.Wifi, "Free WiFi")
//                                AmenityItem(Icons.Default.LocalParking, "Parking")
//                                AmenityItem(Icons.Default.Pool, "Swimming Pool")
//                                AmenityItem(Icons.Default.Restaurant, "Restaurant")
//                                AmenityItem(Icons.Default.FitnessCenter, "Gym")
//                            }
//                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Book Button
                        Button(
                            onClick = {
                                state.hotel?.let { hotel ->
                                    onBookNow(hotel.hotelId, hotel.hotelName)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text(
                                "Book Now",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AmenityItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
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