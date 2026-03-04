package com.example.project_graduation.presentation.admin.hotel_management

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.domain.model.Hotel


// Data model
//data class Hotel(
//    val hotelId: Int,
//    val hotelName: String,
//    val hotelAddress: String,
//    val description: String,
//    val rating: Double
//)

@Composable
fun HotelsManagementContent(
    viewModel: HotelsManagementViewModel,
    onNavigateToRooms: (Int, String) -> Unit
) {
//    var hotels by remember { mutableStateOf(getSampleHotels()) }
//    var showAddDialog by remember { mutableStateOf(false) }
//    var editingHotel by remember { mutableStateOf<Hotel?>(null) }
//    var deletingHotel by remember { mutableStateOf<Hotel?>(null) }
//    var searchQuery by remember { mutableStateOf("") }

    val hotels by viewModel.hotels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingHotel by remember { mutableStateOf<Hotel?>(null) }
    var deletingHotel by remember { mutableStateOf<Hotel?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Load hotels when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadHotels()
    }

    // Show success message
    LaunchedEffect(operationSuccess) {
        if (operationSuccess != null) {
            // Auto clear after 3 seconds
            kotlinx.coroutines.delay(3000)
            viewModel.clearSuccessMessage()
        }
    }

//    val filteredHotels = hotels.filter {
//        it.hotelName.contains(searchQuery, ignoreCase = true) ||
//                it.hotelAddress.contains(searchQuery, ignoreCase = true)
//    }

    val filteredHotels = hotels.filter {
        it.hotelName.contains(searchQuery, ignoreCase = true) ||
                (it.hotelAddress?.contains(searchQuery, ignoreCase = true) == true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Upload Progress
        if (uploadProgress != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3).copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        uploadProgress!!,
                        color = Color(0xFF2196F3),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Success Message
        if (operationSuccess != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                        operationSuccess!!,
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Header with Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Hotels Management",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "${hotels.size} hotels registered",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Hotel")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search hotels...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
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

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on state
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                        Text(
                            error ?: "Unknown error",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadHotels() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            filteredHotels.isEmpty() -> {
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
                        Text("No hotels found", fontSize = 18.sp, color = Color.Gray)
                    }
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredHotels) { hotel ->
                        HotelManagementCard(
                            hotel = hotel,
                            onEdit = { editingHotel = hotel },
                            onDelete = { deletingHotel = hotel },
                            onNavigateToRooms = { onNavigateToRooms(it.hotelId, it.hotelName) }
//                            onClick = {
//                                // ← NAVIGATE TO ROOMS SCREEN
//                                onNavigateToRooms(hotel.hotelId, hotel.hotelName)
//                            }
                        )
                    }
                }
            }
        }


        // Add/Edit Dialog
        if (showAddDialog || editingHotel != null) {

            val context = LocalContext.current

            HotelFormDialog(
                hotel = editingHotel,
                viewModel = viewModel,
                onDismiss = {
                    showAddDialog = false
                    editingHotel = null
                },
                onSave = { hotel, imageUri ->
                    if (editingHotel != null) {
                        // Update with image
                        viewModel.updateHotelWithImage(
                            hotelId = editingHotel!!.hotelId,
                            hotel = hotel,
                            newImageUri = imageUri,
                            context = context
                        )
                    } else {
                        // Create with image
                        viewModel.createHotelWithImage(
                            hotel = hotel,
                            imageUri = imageUri,
                            context = context
                        )
                    }
                    showAddDialog = false
                    editingHotel = null
                }
            )
        }
//                    hotel ->
//                    if (editingHotel != null) {
//                        // Update existing hotel
//                        viewModel.updateHotel(
//                            hotelId = hotel.hotelId,
//                            hotelName = hotel.hotelName,
//                            description = hotel.description,
//                            rating = hotel.rating,
//                            hotelAddress = hotel.hotelAddress
//                        )
//                    } else {
//                        // Create new hotel
//                        viewModel.createHotel(
//                            hotelName = hotel.hotelName,
//                            description = hotel.description,
//                            rating = hotel.rating,
//                            hotelAddress = hotel.hotelAddress
//                        )
//                    }


        // Delete Confirmation Dialog (Coming Soon)
        if (deletingHotel != null) {
            AlertDialog(
                onDismissRequest = { deletingHotel = null },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = { Text("Delete Hotel?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Are you sure you want to delete \"${deletingHotel!!.hotelName}\"? This action cannot be undone.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteHotel(deletingHotel!!.hotelId)
                            deletingHotel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deletingHotel = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        // Hotels List
//        if (filteredHotels.isEmpty()) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Icon(
//                        Icons.Default.SearchOff,
//                        contentDescription = null,
//                        modifier = Modifier.size(64.dp),
//                        tint = Color.Gray
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text(
//                        "No hotels found",
//                        fontSize = 18.sp,
//                        color = Color.Gray
//                    )
//                }
//            }
//        } else {
//            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(filteredHotels) { hotel ->
//                    HotelCard(
//                        hotel = hotel,
//                        onEdit = { editingHotel = hotel },
//                        onDelete = { deletingHotel = hotel }
//                    )
//                }
//            }
//        }

    }

//    // Add/Edit Dialog
//    if (showAddDialog || editingHotel != null) {
//        HotelFormDialog(
//            hotel = editingHotel,
//            onDismiss = {
//                showAddDialog = false
//                editingHotel = null
//            },
//            onSave = { newHotel ->
//                if (editingHotel != null) {
//                    // Update existing
//                    hotels = hotels.map { if (it.hotelId == newHotel.hotelId) newHotel else it }
//                } else {
//                    // Add new
//                    hotels = hotels + newHotel.copy(hotelId = hotels.size + 1)
//                }
//                showAddDialog = false
//                editingHotel = null
//            }
//        )
//    }
//
//    // Delete Confirmation Dialog
//    if (deletingHotel != null) {
//        AlertDialog(
//            onDismissRequest = { deletingHotel = null },
//            icon = {
//                Icon(
//                    Icons.Default.Warning,
//                    contentDescription = null,
//                    tint = Color(0xFFF44336),
//                    modifier = Modifier.size(48.dp)
//                )
//            },
//            title = {
//                Text(
//                    "Delete Hotel?",
//                    fontWeight = FontWeight.Bold
//                )
//            },
//            text = {
//                Text("Are you sure you want to delete \"${deletingHotel!!.hotelName}\"? This action cannot be undone.")
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        hotels = hotels.filter { it.hotelId != deletingHotel!!.hotelId }
//                        deletingHotel = null
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFFF44336)
//                    )
//                ) {
//                    Text("Delete")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { deletingHotel = null }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//    // Add/Edit Dialog (Coming Soon)
//    if (showAddDialog || editingHotel != null) {
//        HotelFormDialog(
//            hotel = editingHotel,
//            onDismiss = {
//                showAddDialog = false
//                editingHotel = null
//            },
//            onSave = { newHotel ->
//                // TODO: Implement API call for create/update
//                showAddDialog = false
//                editingHotel = null
//            }
//        )
//    }
//
//    // Delete Confirmation Dialog (Coming Soon)
//    if (deletingHotel != null) {
//        AlertDialog(
//            onDismissRequest = { deletingHotel = null },
//            icon = {
//                Icon(
//                    Icons.Default.Warning,
//                    contentDescription = null,
//                    tint = Color(0xFFF44336),
//                    modifier = Modifier.size(48.dp)
//                )
//            },
//            title = { Text("Delete Hotel?", fontWeight = FontWeight.Bold) },
//            text = {
//                Text("Are you sure you want to delete \"${deletingHotel!!.hotelName}\"? This action cannot be undone.")
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        // TODO: Implement API call for delete
//                        deletingHotel = null
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
//                ) {
//                    Text("Delete")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { deletingHotel = null }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
}

@Composable
private fun HotelManagementCard(
    hotel: Hotel,
    onEdit: (Hotel) -> Unit,
    onDelete: (Hotel) -> Unit,
    onNavigateToRooms: (Hotel) -> Unit
//    onEdit: () -> Unit,
//    onDelete: () -> Unit,
//    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
//            // Hotel Image
//            if (hotel.primaryImage != null) {
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = "${ApiConfig.BASE_URL}${hotel.primaryImage}"
//                    ),
//                    contentDescription = "Hotel Image",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(180.dp)
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//            }

            // Images Gallery with Navigation Arrows
            if (hotel.images.isNotEmpty()) {
                var currentImageIndex by remember { mutableStateOf(0) }
                val currentImage = hotel.images[currentImageIndex]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // Main Image Display
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = "${ApiConfig.BASE_URL}${currentImage.imageUrl}",
                            contentDescription = currentImage.caption ?: "Hotel image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Primary badge
                        if (currentImage.isPrimary) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Primary",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Image counter
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${currentImageIndex + 1} / ${hotel.images.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        // Caption overlay (if exists)
                        currentImage.caption?.let { caption ->
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = caption,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Navigation Arrows (only show if more than 1 image)
                    if (hotel.images.size > 1) {
                        // Left Arrow
                        IconButton(
                            onClick = {
                                currentImageIndex = if (currentImageIndex > 0) {
                                    currentImageIndex - 1
                                } else {
                                    hotel.images.size - 1
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                shadowElevation = 4.dp
                            ) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Previous image",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Right Arrow
                        IconButton(
                            onClick = {
                                currentImageIndex = if (currentImageIndex < hotel.images.size - 1) {
                                    currentImageIndex + 1
                                } else {
                                    0
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                shadowElevation = 4.dp
                            ) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Next image",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            } else {
                // No images placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No images",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Hotel Info
            Text(
                hotel.hotelName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (hotel.hotelAddress != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        hotel.hotelAddress,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Rating and Image count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Rating
                hotel.rating?.let {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.Star,
//                            contentDescription = null,
//                            tint = MaterialTheme.colorScheme.primary,
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = String.format("%.1f", it),
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFC107)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "%.1f".format(hotel.rating),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }

                // Image count
                if (hotel.images.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${hotel.images.size} ${if (hotel.images.size == 1) "image" else "images"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }


//            if (hotel.rating != null) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        Icons.Default.Star,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp),
//                        tint = Color(0xFFFFC107)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        "%.1f".format(hotel.rating),
//                        fontSize = 13.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = Color.Black
//                    )
//                }
//            }

            if (hotel.description != null && hotel.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    hotel.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigateToRooms(hotel) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.MeetingRoom,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Room", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { onEdit(hotel) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { onDelete(hotel) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 13.sp)
                }
            }
        }
    }
}
//    // Lấy ảnh primary hoặc ảnh đầu tiên
//    val displayImageUrl = hotel.primaryImage
//        ?: hotel.images.firstOrNull { it.isPrimary }?.imageUrl
//        ?: hotel.images.firstOrNull()?.imageUrl


//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(2.dp)
//
//    ) {
//
//
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
//                Column(modifier = Modifier.weight(1f)) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.Hotel,
//                            contentDescription = null,
//                            tint = Color(0xFF1976D2),
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Text(
//                            hotel.hotelName,
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Address
//                    hotel.hotelAddress?.let { address ->
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(
//                                Icons.Default.LocationOn,
//                                contentDescription = null,
//                                modifier = Modifier.size(16.dp),
//                                tint = Color.Gray
//                            )
//                            Spacer(modifier = Modifier.width(4.dp))
//                            Text(
//                                address,
//                                fontSize = 14.sp,
//                                color = Color.Gray,
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(4.dp))
//                    }
//
//                    // Rating
//                    hotel.rating?.let { rating ->
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(
//                                Icons.Default.Star,
//                                contentDescription = null,
//                                modifier = Modifier.size(16.dp),
//                                tint = Color(0xFFFFD700)
//                            )
//                            Spacer(modifier = Modifier.width(4.dp))
//                            Text("$rating/5.0", fontSize = 14.sp, color = Color.Gray)
//                        }
//                    }
//                }
//
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    IconButton(onClick = { onEdit() }, modifier = Modifier.size(36.dp)) {
//                        Icon(
//                            Icons.Default.Edit,
//                            contentDescription = "Edit",
//                            tint = Color(0xFF1976D2),
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                    IconButton(onClick = { onDelete() }, modifier = Modifier.size(36.dp)) {
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
//            // Description
//            hotel.description?.let { desc ->
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    desc,
//                    fontSize = 13.sp,
//                    color = Color.Gray,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        }
//    }
//}

@Composable
private fun HotelFormDialog(
    hotel: Hotel?,
    viewModel: HotelsManagementViewModel,
    onDismiss: () -> Unit,
    onSave: (Hotel, Uri?) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(hotel?.hotelName ?: "") }
    var address by remember { mutableStateOf(hotel?.hotelAddress ?: "") }
    var description by remember { mutableStateOf(hotel?.description ?: "") }
    var rating by remember { mutableStateOf(hotel?.rating?.toString() ?: "4.5") }

    // Image state - chỉ 1 ảnh để test
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var nameError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp)
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (hotel == null) "Add New Hotel" else "Edit Hotel",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hotel Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        label = { Text("Hotel Name *") },
                        leadingIcon = {
                            Icon(Icons.Default.Hotel, contentDescription = null)
                        },
                        isError = nameError,
                        supportingText = {
                            if (nameError) Text("Hotel name is required")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address
                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            addressError = false
                        },
                        label = { Text("Address *") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        },
                        isError = addressError,
                        supportingText = {
                            if (addressError) Text("Address is required")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rating
                    OutlinedTextField(
                        value = rating,
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                rating = it
                            }
                        },
                        label = { Text("Rating (0-5)") },
                        leadingIcon = {
                            Icon(Icons.Default.Star, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Image Section
                    Text(
                        "Hotel Image",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        "Add one image for your hotel (optional)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Image Preview or Add Button
                    if (selectedImageUri != null) {
                        // Show selected image
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = "Selected Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Remove button
                                IconButton(
                                    onClick = { selectedImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(
                                            Color.Red.copy(alpha = 0.7f),
                                            RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    } else if (hotel?.primaryImage != null) {
                        // Show existing image when editing
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        "http://10.0.2.2:8080${hotel.primaryImage}"
                                    ),
                                    contentDescription = "Current Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Change button
                                Button(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1976D2)
                                    )
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Change")
                                }
                            }
                        }
                    } else {
                        // Add Image Button
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Click to add image",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                // Validate
                                if (name.isBlank()) {
                                    nameError = true
                                    return@Button
                                }
                                if (address.isBlank()) {
                                    addressError = true
                                    return@Button
                                }

                                // Create hotel object
                                val newHotel = Hotel(
                                    hotelId = hotel?.hotelId ?: 0,
                                    hotelName = name,
                                    hotelAddress = address,
                                    description = description.ifBlank { null },
                                    rating = rating.toDoubleOrNull(),
                                    images = emptyList(),
                                    primaryImage = null,
                                    createdAt = hotel?.createdAt
                                )

                                // Pass both hotel and image URI
                                onSave(newHotel, selectedImageUri)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                if (hotel == null) Icons.Default.Add else Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (hotel == null) "Add" else "Save")
                        }
                    }
                }
            }

//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = if (hotel == null) "Add New Hotel" else "Edit Hotel",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    IconButton(onClick = onDismiss) {
//                        Icon(Icons.Default.Close, contentDescription = "Close")
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = {
//                        name = it
//                        nameError = false
//                    },
//                    label = { Text("Hotel Name *") },
//                    leadingIcon = { Icon(Icons.Default.Hotel, contentDescription = null) },
//                    isError = nameError,
//                    supportingText = { if (nameError) Text("Hotel name is required") },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = address,
//                    onValueChange = {
//                        address = it
//                        addressError = false
//                    },
//                    label = { Text("Address *") },
//                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
//                    isError = addressError,
//                    supportingText = { if (addressError) Text("Address is required") },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = description,
//                    onValueChange = { description = it },
//                    label = { Text("Description") },
//                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp),
//                    minLines = 3,
//                    maxLines = 5
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = rating,
//                    onValueChange = {
//                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
//                            rating = it
//                        }
//                    },
//                    label = { Text("Rating (0-5)") },
//                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = onDismiss,
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Text("Cancel")
//                    }
//
//                    Button(
//                        onClick = {
//                            if (name.isBlank()) {
//                                nameError = true
//                                return@Button
//                            }
//                            if (address.isBlank()) {
//                                addressError = true
//                                return@Button
//                            }
//
//                            val newHotel = Hotel(
//                                hotelId = hotel?.hotelId ?: 0,
//                                hotelName = name,
//                                hotelAddress = address,
//                                description = description,
//                                rating = rating.toDoubleOrNull() ?: 4.5
//                            )
//                            onSave(newHotel)
//                        },
//                        modifier = Modifier.weight(1f),
//                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Icon(
//                            if (hotel == null) Icons.Default.Add else Icons.Default.Save,
//                            contentDescription = null,
//                            modifier = Modifier.size(18.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(if (hotel == null) "Add" else "Save")
//                    }
//                }
//            }
//        }
        }
    }
}


//@Composable
//fun HotelCard(
//    hotel: Hotel,
//    onEdit: () -> Unit,
//    onDelete: () -> Unit
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
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
//                Column(modifier = Modifier.weight(1f)) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.Hotel,
//                            contentDescription = null,
//                            tint = Color(0xFF1976D2),
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Text(
//                            hotel.hotelName,
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.LocationOn,
//                            contentDescription = null,
//                            modifier = Modifier.size(16.dp),
//                            tint = Color.Gray
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            hotel.hotelAddress,
//                            fontSize = 14.sp,
//                            color = Color.Gray,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.Star,
//                            contentDescription = null,
//                            modifier = Modifier.size(16.dp),
//                            tint = Color(0xFFFFD700)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            "${hotel.rating}/5.0",
//                            fontSize = 14.sp,
//                            color = Color.Gray
//                        )
//                    }
//                }
//
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    IconButton(
//                        onClick = onEdit,
//                        modifier = Modifier.size(36.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.Edit,
//                            contentDescription = "Edit",
//                            tint = Color(0xFF1976D2),
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                    IconButton(
//                        onClick = onDelete,
//                        modifier = Modifier.size(36.dp)
//                    ) {
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
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                hotel.description,
//                fontSize = 13.sp,
//                color = Color.Gray,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//}
//
//@Composable
//fun HotelFormDialog(
//    hotel: Hotel?,
//    onDismiss: () -> Unit,
//    onSave: (Hotel) -> Unit
//) {
//    var name by remember { mutableStateOf(hotel?.hotelName ?: "") }
//    var address by remember { mutableStateOf(hotel?.hotelAddress ?: "") }
//    var description by remember { mutableStateOf(hotel?.description ?: "") }
//    var rating by remember { mutableStateOf(hotel?.rating?.toString() ?: "4.5") }
//
//    var nameError by remember { mutableStateOf(false) }
//    var addressError by remember { mutableStateOf(false) }
//
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(24.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = if (hotel == null) "Add New Hotel" else "Edit Hotel",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    IconButton(onClick = onDismiss) {
//                        Icon(Icons.Default.Close, contentDescription = "Close")
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = {
//                        name = it
//                        nameError = false
//                    },
//                    label = { Text("Hotel Name *") },
//                    leadingIcon = {
//                        Icon(Icons.Default.Hotel, contentDescription = null)
//                    },
//                    isError = nameError,
//                    supportingText = {
//                        if (nameError) Text("Hotel name is required")
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = address,
//                    onValueChange = {
//                        address = it
//                        addressError = false
//                    },
//                    label = { Text("Address *") },
//                    leadingIcon = {
//                        Icon(Icons.Default.LocationOn, contentDescription = null)
//                    },
//                    isError = addressError,
//                    supportingText = {
//                        if (addressError) Text("Address is required")
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = description,
//                    onValueChange = { description = it },
//                    label = { Text("Description") },
//                    leadingIcon = {
//                        Icon(Icons.Default.Description, contentDescription = null)
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp),
//                    minLines = 3,
//                    maxLines = 5
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = rating,
//                    onValueChange = {
//                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
//                            rating = it
//                        }
//                    },
//                    label = { Text("Rating (0-5)") },
//                    leadingIcon = {
//                        Icon(Icons.Default.Star, contentDescription = null)
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp)
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = onDismiss,
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Text("Cancel")
//                    }
//
//                    Button(
//                        onClick = {
//                            if (name.isBlank()) {
//                                nameError = true
//                                return@Button
//                            }
//                            if (address.isBlank()) {
//                                addressError = true
//                                return@Button
//                            }
//
//                            val newHotel = Hotel(
//                                hotelId = hotel?.hotelId ?: 0,
//                                hotelName = name,
//                                hotelAddress = address,
//                                description = description,
//                                rating = rating.toDoubleOrNull() ?: 4.5
//                            )
//                            onSave(newHotel)
//                        },
//                        modifier = Modifier.weight(1f),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFF1976D2)
//                        ),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Icon(
//                            if (hotel == null) Icons.Default.Add else Icons.Default.Save,
//                            contentDescription = null,
//                            modifier = Modifier.size(18.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(if (hotel == null) "Add" else "Save")
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Sample Data
//fun getSampleHotels(): List<Hotel> {
//    return listOf(
//        Hotel(
//            hotelId = 1,
//            hotelName = "Grand Palace Hotel New York",
//            hotelAddress = "789 5th Avenue, New York, NY 10022, USA",
//            description = "Luxury 5-star hotel in the heart of Manhattan",
//            rating = 4.8
//        ),
//        Hotel(
//            hotelId = 2,
//            hotelName = "Beach Resort Miami",
//            hotelAddress = "1200 Ocean Drive, Miami Beach, FL 33139, USA",
//            description = "Beachfront resort with ocean views",
//            rating = 4.7
//        ),
//        Hotel(
//            hotelId = 3,
//            hotelName = "Pacific Tower Sydney",
//            hotelAddress = "100 George Street, Sydney NSW 2000, Australia",
//            description = "Modern hotel near Sydney Opera House",
//            rating = 4.6
//        ),
//        Hotel(
//            hotelId = 4,
//            hotelName = "Royal Palace London",
//            hotelAddress = "25 Buckingham Gate, London SW1E 6AF, UK",
//            description = "Historic luxury hotel near Buckingham Palace",
//            rating = 4.8
//        ),
//        Hotel(
//            hotelId = 5,
//            hotelName = "Sakura Inn Tokyo",
//            hotelAddress = "3-1-1 Marunouchi, Tokyo 100-0005, Japan",
//            description = "Modern Japanese hospitality",
//            rating = 4.8
//        )
//    )
//}

// ============= PREVIEWS =============
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun HotelsManagementPreview() {
//    MaterialTheme {
//        Surface(color = Color(0xFFF5F5F5)) {
////            HotelsManagementContent()
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun HotelCardPreview() {
//    MaterialTheme {
//        HotelCard(
//            hotel = Hotel(
//                hotelId = 1,
//                hotelName = "Grand Palace Hotel",
//                hotelAddress = "789 5th Avenue, New York",
//                description = "Luxury 5-star hotel in the heart of Manhattan",
//                rating = 4.8
//            ),
//            onEdit = {},
//            onDelete = {}
//        )
//    }
//}