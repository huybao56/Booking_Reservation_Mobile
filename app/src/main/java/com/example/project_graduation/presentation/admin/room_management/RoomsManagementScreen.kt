package com.example.project_graduation.presentation.admin.room_management

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import com.example.project_graduation.domain.model.Room

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsManagementScreen(
    hotelId: Int,
    hotelName: String,
    viewModel: RoomsManagementViewModel,
    onBack: () -> Unit
) {
    val rooms by viewModel.rooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingRoom by remember { mutableStateOf<Room?>(null) }
    var deletingRoom by remember { mutableStateOf<Room?>(null) }
    var searchQuery by remember { mutableStateOf("") }


    val context = LocalContext.current


    // Load rooms when screen opens
    LaunchedEffect(hotelId) {
        viewModel.loadRooms(hotelId, hotelName)
    }

    // Auto clear success message
    LaunchedEffect(operationSuccess) {
        if (operationSuccess != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearSuccessMessage()
        }
    }

    val filteredRooms = rooms.filter {
        it.roomNumber.contains(searchQuery, ignoreCase = true) ||
                it.roomType.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            hotelName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Rooms Management",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
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

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RoomStatCard(
                    title = "Total",
                    value = viewModel.totalRooms.toString(),
                    icon = Icons.Default.Hotel,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                RoomStatCard(
                    title = "Available",
                    value = viewModel.availableRooms.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                RoomStatCard(
                    title = "Occupied",
                    value = viewModel.occupiedRooms.toString(),
                    icon = Icons.Default.People,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Header with Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Room List",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        "${rooms.size} rooms registered",
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
                    Text("Add Room")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search rooms...", fontSize = 14.sp) },
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
                                onClick = { viewModel.loadRooms(hotelId, hotelName) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                filteredRooms.isEmpty() -> {
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
                            Text("No rooms found", fontSize = 18.sp, color = Color.Gray)
                        }
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredRooms) { room ->
                            RoomManagementCard(
                                room = room,
                                onEdit = { editingRoom = room },
                                onDelete = { deletingRoom = room }
                            )
                        }
                    }
                }
            }
        }

        // Add/Edit Dialog
        if (showAddDialog || editingRoom != null) {
            RoomFormDialog(
                room = editingRoom,
                onDismiss = {
                    showAddDialog = false
                    editingRoom = null
                },
//                onSave = { room ->
//                    if (editingRoom != null) {
//                        viewModel.updateRoom(
//                            roomId = room.roomId,
//                            roomNumber = room.roomNumber,
//                            roomType = room.roomType,
//                            floor = room.floor,
//                            status = room.status,
//                            basePrice = room.basePrice,
//                            capacity = room.capacity,
//                            description = room.description,
//                            amenities = room.amenities
//                        )
//                    } else {
//                        viewModel.createRoom(
//                            hotelId = hotelId,
//                            roomNumber = room.roomNumber,
//                            roomType = room.roomType,
//                            floor = room.floor,
//                            basePrice = room.basePrice,
//                            capacity = room.capacity,
//                            description = room.description,
//                            amenities = room.amenities
//                        )
//                    }
//
                onSave = { room, imageUri ->
                    if (editingRoom != null) {
                        viewModel.updateRoomWithImage(
                            roomId = room.roomId,
                            room = room,
                            newImageUri = imageUri,
                            context = context
                        )
                    } else {
                        viewModel.createRoomWithImage(
                            hotelId = hotelId,
                            room = room,
                            imageUri = imageUri,
                            context = context
                        )
                    }
                    showAddDialog = false
                    editingRoom = null
                }
            )
        }

        // Delete Confirmation Dialog
        if (deletingRoom != null) {
            AlertDialog(
                onDismissRequest = { deletingRoom = null },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = { Text("Delete Room?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Are you sure you want to delete Room ${deletingRoom!!.roomNumber}? This action cannot be undone.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteRoom(deletingRoom!!.roomId)
                            deletingRoom = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deletingRoom = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun RoomStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

//            Column {
//                Text(
//                    value,
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//                Text(
//                    title,
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//            }
            // Row chứa số và tiêu đề cạnh nhau
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    title,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun RoomManagementCard(
    room: Room,
    onEdit: (Room) -> Unit,
    onDelete: (Room) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Room Number and Type Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Room ${room.roomNumber}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = room.roomType,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Status Badge
                Surface(
                    color = when (room.status) {
                        "AVAILABLE" -> MaterialTheme.colorScheme.primaryContainer
                        "OCCUPIED" -> MaterialTheme.colorScheme.errorContainer
                        "MAINTENANCE" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = room.status ?: "UNKNOWN",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Images Gallery with Navigation Arrows
            if (room.images.isNotEmpty()) {
                var currentImageIndex by remember { mutableStateOf(0) }
                val currentImage = room.images[currentImageIndex]

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
                            contentDescription = currentImage.caption ?: "Room image",
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
                                text = "${currentImageIndex + 1} / ${room.images.size}",
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
                    if (room.images.size > 1) {
                        // Left Arrow
                        IconButton(
                            onClick = {
                                currentImageIndex = if (currentImageIndex > 0) {
                                    currentImageIndex - 1
                                } else {
                                    room.images.size - 1
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
                                currentImageIndex = if (currentImageIndex < room.images.size - 1) {
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

            // Room Details
            room.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Room Info Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Floor
                room.floor?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Layers,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Floor $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Capacity
                room.capacity?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$it guests",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Price
                room.basePrice?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$$it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Image count
            if (room.images.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${room.images.size} ${if (room.images.size == 1) "image" else "images"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit button
                OutlinedButton(
                    onClick = { onEdit(room) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }

                // Delete button
                OutlinedButton(
                    onClick = { onDelete(room) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}

//@Composable
//fun RoomManagementCard(
//    room: Room,
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
//                    // Room Number & Type
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Surface(
//                            shape = RoundedCornerShape(8.dp),
//                            color = when (room.status) {
//                                "AVAILABLE" -> Color(0xFFE8F5E9)
//                                "OCCUPIED" -> Color(0xFFFFF3E0)
//                                "MAINTENANCE" -> Color(0xFFFFEBEE)
//                                else -> Color(0xFFE3F2FD)
//                            }
//                        ) {
//                            Text(
//                                room.roomNumber,
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = when (room.status) {
//                                    "AVAILABLE" -> Color(0xFF4CAF50)
//                                    "OCCUPIED" -> Color(0xFFFF9800)
//                                    "MAINTENANCE" -> Color(0xFFF44336)
//                                    else -> Color(0xFF2196F3)
//                                },
//                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
//                            )
//                        }
//
//                        Text(
//                            room.roomType,
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = Color.Black
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Room Details
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        DetailChip(icon = Icons.Default.Domain, text = "Floor ${room.floor}")
//                        DetailChip(icon = Icons.Default.People, text = "${room.capacity} guests")
//                        DetailChip(icon = Icons.Default.AttachMoney, text = "$${room.basePrice}/night")
//                    }
//
//                    // Amenities
//                    if (room.amenities.isNotEmpty()) {
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            room.amenities.joinToString(" • "),
//                            fontSize = 12.sp,
//                            color = Color.Gray,
//                            maxLines = 2,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
//
//                    // Status Badge
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Surface(
//                        shape = RoundedCornerShape(6.dp),
//                        color = when (room.status) {
//                            "AVAILABLE" -> Color(0xFFE8F5E9)
//                            "OCCUPIED" -> Color(0xFFFFF3E0)
//                            "MAINTENANCE" -> Color(0xFFFFEBEE)
//                            else -> Color(0xFFE3F2FD)
//                        }
//                    ) {
//                        Text(
//                            room.status,
//                            fontSize = 11.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = when (room.status) {
//                                "AVAILABLE" -> Color(0xFF4CAF50)
//                                "OCCUPIED" -> Color(0xFFFF9800)
//                                "MAINTENANCE" -> Color(0xFFF44336)
//                                else -> Color(0xFF2196F3)
//                            },
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                        )
//                    }
//                }
//
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
//                        Icon(
//                            Icons.Default.Edit,
//                            contentDescription = "Edit",
//                            tint = Color(0xFF1976D2),
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
//        }
//    }
//}

@Composable
fun DetailChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Text(
            text,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomFormDialog(
    room: Room?,
//    viewModel: RoomsManagementViewModel,
    onDismiss: () -> Unit,
    onSave: (Room, Uri?) -> Unit
) {
    var roomNumber by remember { mutableStateOf(room?.roomNumber ?: "") }
    var roomType by remember { mutableStateOf(room?.roomType ?: "Standard Room") }
    var floor by remember { mutableStateOf(room?.floor?.toString() ?: "1") }
    var status by remember { mutableStateOf(room?.status ?: "AVAILABLE") }
    var basePrice by remember { mutableStateOf(room?.basePrice?.toString() ?: "250.0") }
    var capacity by remember { mutableStateOf(room?.capacity?.toString() ?: "2") }
    var description by remember { mutableStateOf(room?.description ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var roomNumberError by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val statusOptions = listOf("AVAILABLE", "OCCUPIED", "MAINTENANCE")
    val roomTypeOptions = listOf(
        "Standard Room",
        "Deluxe Room",
        "Suite Room",
        "Executive Room",
        "Family Room",
        "Garden View Room"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
//            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (room == null) "Add New Room" else "Edit Room",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Room Number
                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = {
                            roomNumber = it
                            roomNumberError = false
                        },
                        label = { Text("Room Number *") },
                        leadingIcon = { Icon(Icons.Default.MeetingRoom, contentDescription = null) },
                        isError = roomNumberError,
                        supportingText = { if (roomNumberError) Text("Room number is required") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Room Type Dropdown
                    var expandedRoomType by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedRoomType,
                        onExpandedChange = { expandedRoomType = it }
                    ) {
                        OutlinedTextField(
                            value = roomType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Room Type") },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoomType) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRoomType,
                            onDismissRequest = { expandedRoomType = false }
                        ) {
                            roomTypeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        roomType = option
                                        expandedRoomType = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Floor & Capacity Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = floor,
                            onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) floor = it },
                            label = { Text("Floor") },
                            leadingIcon = { Icon(Icons.Default.Domain, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = capacity,
                            onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) capacity = it },
                            label = { Text("Capacity") },
                            leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Base Price
                    OutlinedTextField(
                        value = basePrice,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) basePrice = it },
                        label = { Text("Base Price (per night)") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status Dropdown (only for editing)
                    if (room != null) {
                        var expandedStatus by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedStatus,
                            onExpandedChange = { expandedStatus = it }
                        ) {
                            OutlinedTextField(
                                value = status,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Status") },
                                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStatus,
                                onDismissRequest = { expandedStatus = false }
                            ) {
                                statusOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            status = option
                                            expandedStatus = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Room Image", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedImageUri != null) {
                        Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            Box(Modifier.fillMaxSize()) {
                                Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { selectedImageUri = null },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                                ) {
                                    Surface(
                                        color = Color.Red.copy(0.7f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color.White)
                                    }
                                }
                            }
                        }
                    } else if (room?.primaryImage != null) {
                        Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            Box(Modifier.fillMaxSize()) {
                                Image(
                                    painter = rememberAsyncImagePainter("${ApiConfig.BASE_URL}${room.primaryImage}"),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Button(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                                ) {
                                    Text("Change")
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth().height(120.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddAPhoto, null, Modifier.size(48.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Add Image")
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
                                if (roomNumber.isBlank()) {
                                    roomNumberError = true
                                    return@Button
                                }

                                val newRoom = Room(
                                    roomId = room?.roomId ?: 0,
                                    hotelId = room?.hotelId ?: 0,
                                    roomNumber = roomNumber,
                                    roomType = roomType,
                                    floor = floor.toIntOrNull() ?: 1,
                                    status = status,
                                    basePrice = basePrice.toDoubleOrNull() ?: 250.0,
                                    capacity = capacity.toIntOrNull() ?: 2,
                                    description = description.ifBlank { null },
                                    amenities = emptyList(),
                                    images = emptyList(),
                                    primaryImage = null
                                )
                                onSave(newRoom, selectedImageUri)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                if (room == null) Icons.Default.Add else Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (room == null) "Add" else "Save")
                        }
                    }
                }
            }
        }
    }
}


//// ===== MOCK DATA =====
//private fun previewRooms(): List<Room> = listOf(
//    Room(
//        roomId = 1,
//        hotelId = 1,
//        roomNumber = "101",
//        roomType = "Standard Room",
//        floor = 1,
//        status = "AVAILABLE",
//        basePrice = 250.0,
//        capacity = 2,
//        description = "Standard room with city view",
//        amenities = listOf("WiFi", "Air Conditioner", "TV")
//    ),
//    Room(
//        roomId = 2,
//        hotelId = 1,
//        roomNumber = "202",
//        roomType = "Deluxe Room",
//        floor = 2,
//        status = "OCCUPIED",
//        basePrice = 450.0,
//        capacity = 3,
//        description = "Deluxe room with balcony",
//        amenities = listOf("WiFi", "Balcony", "Mini Bar")
//    ),
//    Room(
//        roomId = 3,
//        hotelId = 1,
//        roomNumber = "303",
//        roomType = "Suite Room",
//        floor = 3,
//        status = "MAINTENANCE",
//        basePrice = 800.0,
//        capacity = 4,
//        description = "Luxury suite",
//        amenities = listOf("WiFi", "Jacuzzi", "Living Room")
//    )
//)
//
//// Thêm đoạn code này vào cuối file RoomsManagementScreen.kt
//
//// Note: Preview cho toàn bộ screen khó implement do cần ViewModel
//// Bạn có thể xem preview từng component bên dưới
//// Hoặc chạy app để xem full screen
//
//@Preview(showBackground = true)
//@Composable
//fun RoomManagementCardPreview() {
//    RoomManagementCard(
//        room = Room(
//            roomId = 1,
//            hotelId = 1,
//            roomNumber = "101",
//            roomType = "Deluxe Room",
//            floor = 1,
//            status = "AVAILABLE",
//            basePrice = 350.0,
//            capacity = 2,
//            description = "Luxurious room with ocean view",
//            amenities = listOf("WiFi", "Air Conditioner", "TV", "Mini Bar", "Balcony")
//        ),
//        onEdit = {},
//        onDelete = {}
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun RoomStatCardPreview() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        RoomStatCard(
//            title = "Total",
//            value = "24",
//            icon = Icons.Default.Hotel,
//            color = Color(0xFF2196F3),
//            modifier = Modifier.weight(1f)
//        )
//        RoomStatCard(
//            title = "Available",
//            value = "18",
//            icon = Icons.Default.CheckCircle,
//            color = Color(0xFF4CAF50),
//            modifier = Modifier.weight(1f)
//        )
//        RoomStatCard(
//            title = "Occupied",
//            value = "6",
//            icon = Icons.Default.People,
//            color = Color(0xFFFF9800),
//            modifier = Modifier.weight(1f)
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun RoomFormDialogPreview() {
//    RoomFormDialog(
//        room = null,
//        onDismiss = {},
//        onSave = {}
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun RoomFormDialogEditPreview() {
//    RoomFormDialog(
//        room = Room(
//            roomId = 1,
//            hotelId = 1,
//            roomNumber = "202",
//            roomType = "Suite Room",
//            floor = 2,
//            status = "OCCUPIED",
//            basePrice = 550.0,
//            capacity = 4,
//            description = "Premium suite with separate living area",
//            amenities = listOf("WiFi", "Jacuzzi", "Kitchen")
//        ),
//        onDismiss = {},
//        onSave = {}
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DetailChipPreview() {
//    Row(
//        modifier = Modifier.padding(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        DetailChip(icon = Icons.Default.Domain, text = "Floor 3")
//        DetailChip(icon = Icons.Default.People, text = "4 guests")
//        DetailChip(icon = Icons.Default.AttachMoney, text = "$450/night")
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun RoomManagementCardOccupiedPreview() {
//    RoomManagementCard(
//        room = Room(
//            roomId = 2,
//            hotelId = 1,
//            roomNumber = "305",
//            roomType = "Executive Suite",
//            floor = 3,
//            status = "OCCUPIED",
//            basePrice = 750.0,
//            capacity = 4,
//            description = "Premium executive suite",
//            amenities = listOf("WiFi", "Smart TV", "Jacuzzi", "Work Desk", "City View")
//        ),
//        onEdit = {},
//        onDelete = {}
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun RoomManagementCardMaintenancePreview() {
//    RoomManagementCard(
//        room = Room(
//            roomId = 3,
//            hotelId = 1,
//            roomNumber = "501",
//            roomType = "Standard Room",
//            floor = 5,
//            status = "MAINTENANCE",
//            basePrice = 250.0,
//            capacity = 2,
//            description = "Under maintenance",
//            amenities = listOf("WiFi", "TV")
//        ),
//        onEdit = {},
//        onDelete = {}
//    )
//}