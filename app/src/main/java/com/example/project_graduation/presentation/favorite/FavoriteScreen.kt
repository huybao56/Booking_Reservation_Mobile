package com.example.project_graduation.presentation.favorite

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.FavoriteDto
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
    preferencesManager: PreferencesManager,
    onNavigateBack: () -> Unit,
    onNavigateToHotelDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var currentUserId by remember { mutableStateOf<Int?>(null) }

    // Load userId từ preferences rồi load favorites
    LaunchedEffect(Unit) {
        val user = preferencesManager.getUser()
        currentUserId = user?.userId
        user?.userId?.let { viewModel.loadFavorites(it) }
    }

    // Snackbar cho remove action
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Favorites", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        if (state.favorites.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFEBEE), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${state.favorites.size} hotel${if (state.favorites.size > 1) "s" else ""}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color(0xFF2196F3)) }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                        Text(state.error ?: "Something went wrong", color = Color.Gray, fontSize = 14.sp)
                        TextButton(onClick = { currentUserId?.let { viewModel.loadFavorites(it) } }) {
                            Text("Retry", color = Color(0xFF2196F3))
                        }
                    }
                }
            }

            state.favorites.isEmpty() -> {
                EmptyFavoriteState(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    onNavigateBack = onNavigateBack
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(state.favorites, key = { it.favoriteId }) { favorite ->
                        FavoriteHotelCard(
                            favorite = favorite,
                            onRemove = {
                                currentUserId?.let { uid ->
                                    viewModel.toggleFavorite(uid, favorite.hotelId)
                                }
                            },
                            onClick = { onNavigateToHotelDetail(favorite.hotelId) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun FavoriteHotelCard(
    favorite: FavoriteDto,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            // Hotel image
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Hotel,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFF90CAF9)
                )
            }

            // Hotel info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = favorite.hotelName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    favorite.hotelAddress?.let {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                            Text(text = it, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    favorite.rating?.let { rating ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFFFC107))
                            Text(text = String.format("%.1f", rating), fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    // Added date
                    Text(
                        text = favorite.createdAt.take(10),
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }

            // Remove button
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoriteState(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFFFEBEE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = Color(0xFFE53935)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("No favorites yet", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tap the ♡ on any hotel to save it here",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateBack,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Explore hotels", fontWeight = FontWeight.Medium)
        }
    }
}