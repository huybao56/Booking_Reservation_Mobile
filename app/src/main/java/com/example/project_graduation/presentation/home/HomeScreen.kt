package com.example.project_graduation.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_graduation.data.repository.AuthRepositoryImpl
import com.example.project_graduation.domain.model.Hotel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import coil.compose.AsyncImage
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.presentation.favorite.FavoriteViewModel
import com.example.project_graduation.presentation.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    favoriteViewModel : FavoriteViewModel,
    onNavigateToProfile: (() -> Unit)? = null,
    onNavigateToHotelDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val hotels by viewModel.hotels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchCriteria by viewModel.searchCriteria.collectAsState()
//    val profileState by profileViewModel.state.collectAsState()
//    val favoriteState by favoriteViewModel.state.collectAsState()

    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    val favoriteState by favoriteViewModel.state.collectAsStateWithLifecycle()

    // Reload favoriteIds mỗi khi màn hình resume (quay lại từ FavoriteScreen)
    LaunchedEffect(profileState.user?.userId) {
        profileState.user?.userId?.let { uid ->
            favoriteViewModel.loadFavoriteIds(uid)
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredHotels = remember(hotels, searchQuery) {
        if (searchQuery.isBlank()) {
            hotels
        } else {
            hotels.filter { it.hotelName.startsWith(searchQuery, ignoreCase = true) }
        }
    }

    // Log mỗi khi searchCriteria thay đổi
    LaunchedEffect(searchCriteria) {
        Log.d("ContentValues", "HomeScreen searchCriteria changed: $searchCriteria")
    }
//    LaunchedEffect(Unit) {
//        viewModel.loadHotels()
//    }

    LaunchedEffect(hotels) {
        Log.d("HomeScreen", "Hotels count: ${hotels.size}")
    }

    // Log mỗi khi searchCriteria thay đổi
    LaunchedEffect(searchCriteria) {
        Log.d("ContentValues", "HomeScreen searchCriteria changed: $searchCriteria")
    }



    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Home",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    // Notification icon
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = {
                        Log.d("ContentValues", "Logout button clicked")

                        // UPDATED: Call logout from ViewModel
                        profileViewModel.logout {
                            // After successful logout, navigate
                            onLogout()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hi There",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Discover Your\nDream Hotel Today",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            lineHeight = 30.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9))
                            .clickable { onNavigateToProfile?.let { it() } },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search hotel by name...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF2196F3)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF2196F3)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
//                LazyRow(
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    items(categories) { category ->
//                        CategoryCard(
//                            icon = category.icon,
//                            label = category.label,
//                            isSelected = selectedCategory == category.label,
//                            onClick = { selectedCategory = category.label }
//                        )
//                    }
//                }

                DateSelectionCard(
                    currentSearchCriteria = searchCriteria,
                    onSearchClick = { checkIn, checkOut, guests ->
                        Log.d("HomeScreen", "Search: $checkIn to $checkOut, $guests guests")

                        // Save search criteria in ViewModel
                        viewModel.updateSearchCriteria(checkIn, checkOut, guests)

                        // Filter hotels
//                        viewModel.filterHotels()

                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (filteredHotels.isEmpty() && searchQuery.isNotBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hotels found for \"$searchQuery\"",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredHotels) { hotel ->
                    HotelCard(
                        hotel,
//                        isFavorite = favoriteViewModel.isFavorite(hotel.hotelId),
                        isFavorite = favoriteState.favoriteIds.contains(hotel.hotelId),
                        onFavoriteClick = {
//                            val userId = // lấy từ preferencesManager hoặc profileViewModel
//                                favoriteViewModel.toggleFavorite(userId, hotel.hotelId)
                            profileState.user?.userId?.let { uid ->
                                favoriteViewModel.toggleFavorite(uid, hotel.hotelId)
                            }
                        },
                        onClick = { onNavigateToHotelDetail(hotel.hotelId) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Extra space để tránh bị che bởi bottom nav
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

//            items(properties) { property ->
//                PropertyCard(property)
//                Spacer(modifier = Modifier.height(16.dp))
//            }
        }
    }
}


@Composable
fun HotelCard(
    hotel: Hotel,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit
) {
//    var isFavorite by remember { mutableStateOf(false) }

    // Lấy ảnh primary hoặc ảnh đầu tiên
    val displayImageUrl = hotel.primaryImage
        ?: hotel.images.firstOrNull { it.isPrimary }?.imageUrl
        ?: hotel.images.firstOrNull()?.imageUrl

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

//            AsyncImage(
//                model = hotel.imageUrl ?: "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
//                contentDescription = hotel.hotelName,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop, //  KEY PROPERTY - Tự động resize và crop
//            )

            if (displayImageUrl != null) {
                val fullImageUrl = "${ApiConfig.BASE_URL}$displayImageUrl"

                Log.d("HotelCard", "Loading image: $fullImageUrl")

                AsyncImage(
                    model = fullImageUrl,
                    contentDescription = hotel.hotelName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(android.R.drawable.ic_menu_gallery),
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                )
            } else {
                // Placeholder nếu không có ảnh
                Log.d("HotelCard", "No image for ${hotel.hotelName}, using placeholder")

                // Gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hotel,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Image",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color(0xFFE0E0E0)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Image,
//                        contentDescription = null,
//                        modifier = Modifier.size(64.dp),
//                        tint = Color.Gray
//                    )
//                }
            }

//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color(0xFFE0E0E0))
//            )

            // Overlay tối nhẹ
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.Right
                ) {
//                    // Location Tag
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
//                            .padding(horizontal = 12.dp, vertical = 6.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.LocationOn,
//                            contentDescription = null,
//                            tint = Color.Gray,
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = hotel.hotelAddress ?: "No address",
//                            fontSize = 12.sp,
//                            color = Color.Gray
//                        )
//                    }

                    // Favorite Button
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Hotel Info Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White.copy(alpha = 0.95f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = hotel.hotelName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Rating
                    hotel.rating?.let { rating ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", rating),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF616161)
                            )
                        }
                    }
                }
            }
        }
    }
}


//data class Category(
//    val icon: ImageVector,
//    val label: String
//)

//data class Property(
//    val name: String,
//    val price: String,
//    val location: String,
//    val isFavorite: Boolean
//)

//val categories = listOf(
//    Category(Icons.Default.Home, "Home"),
//    Category(Icons.Default.Build, "Apartment"),
//    Category(Icons.Default.BusinessCenter, "Villa"),
//    Category(Icons.Default.Cottage, "Bungalo"),
//    Category(Icons.Default.Landscape, "Empty land"),
//    Category(Icons.Default.Business, "Office")
//)

//val properties = listOf(
//    Property(
//        name = "Royal Apartment",
//        price = "$46,100.00",
//        location = "New York",
//        isFavorite = false
//    ),
//    Property(
//        name = "Luxury Villa",
//        price = "$125,000.00",
//        location = "Los Angeles",
//        isFavorite = true
//    ),
//    Property(
//        name = "Modern House",
//        price = "$78,500.00",
//        location = "Miami",
//        isFavorite = false
//    )
//)


@Composable
fun DateSelectionCard(
    modifier: Modifier = Modifier,
    currentSearchCriteria: SearchCriteria? = null,
    onSearchClick: (checkIn: String, checkOut: String, guests: Int) -> Unit = { _, _, _ -> }
) {
    var checkInDate by remember(currentSearchCriteria?.checkIn) {
        mutableStateOf(
            if (!currentSearchCriteria?.checkIn.isNullOrEmpty()) {
                try {
                    LocalDate.parse(currentSearchCriteria!!.checkIn)
                } catch (e: Exception) {
                    Log.d("ContentValues", "Parse checkIn failed, using today")
                    LocalDate.now()
                }
            } else {
                LocalDate.now()
            }
        )
    }

    var checkOutDate by remember(currentSearchCriteria) {
        mutableStateOf(
            if (!currentSearchCriteria?.checkOut.isNullOrEmpty()) {
                try {
                    LocalDate.parse(currentSearchCriteria!!.checkOut)
                } catch (e: Exception) {
                    Log.d("ContentValues", "Parse checkOut failed, using tomorrow")
                    LocalDate.now().plusDays(1)
                }
            } else {
                LocalDate.now().plusDays(1)
            }
        )
    }


//    var numberOfGuests by remember { mutableStateOf(2) }

    var numberOfGuests by remember(currentSearchCriteria?.guests) {
        mutableStateOf(currentSearchCriteria?.guests ?: 2)
    }

    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    LaunchedEffect(currentSearchCriteria) {
        if (currentSearchCriteria != null) {
            Log.d("ContentValues", "DateSelectionCard loaded with: $currentSearchCriteria")
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Book Your Stay",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check-in & Check-out Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Check-in Date
                DatePickerField(
                    label = "Check-in",
                    date = checkInDate,
                    onClick = { showCheckInPicker = true },
                    modifier = Modifier.weight(1f)
                )

                // Check-out Date
                DatePickerField(
                    label = "Check-out",
                    date = checkOutDate,
                    onClick = { showCheckOutPicker = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Number of Guests
            GuestSelector(
                numberOfGuests = numberOfGuests,
                onIncrease = {
                    if (numberOfGuests < 10) {
                        numberOfGuests++
                        // Tự động update lên ViewModel khi thay đổi
                        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                        Log.d("ContentValues", "Guests increased to: $numberOfGuests")
                        onSearchClick(
                            checkInDate.format(formatter),
                            checkOutDate.format(formatter),
                            numberOfGuests
                        )
                    }
                },
                onDecrease = {
                    if (numberOfGuests > 1) {
                        numberOfGuests--
                        // Tự động update lên ViewModel khi thay đổi
                        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                        Log.d("ContentValues", "Guests decreased to: $numberOfGuests")
                        onSearchClick(
                            checkInDate.format(formatter),
                            checkOutDate.format(formatter),
                            numberOfGuests
                        )
                    }
                }
            )

//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Search Button
//            Button(
//                onClick = {
//                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
//                    onSearchClick(
//                        checkInDate.format(formatter),
//                        checkOutDate.format(formatter),
//                        numberOfGuests
//                    )
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF2196F3)
//                )
//            ) {
//                Icon(
//                    Icons.Default.Search,
//                    contentDescription = null,
//                    modifier = Modifier.size(20.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    "Search Hotels",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }

            // Night count
            val nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate)
            if (nights > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$nights night${if (nights > 1) "s" else ""} • $numberOfGuests guest${if (numberOfGuests > 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    // Date Pickers
    if (showCheckInPicker) {
        CalendarDatePickerDialog(
            title = "Select Check-in Date",
            currentDate = checkInDate,
            minDate = LocalDate.now(),
            onDateSelected = {
                checkInDate = it
                if (checkOutDate <= checkInDate) {
                    checkOutDate = checkInDate.plusDays(1)
                }
                // LOG và UPDATE ngay khi chọn ngày
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                val newCheckIn = checkInDate.format(formatter)
                val newCheckOut = checkOutDate.format(formatter)

                Log.d("ContentValues", "Check-in date selected: $newCheckIn")
                Log.d("ContentValues", "Auto-updated check-out to: $newCheckOut")

                // Update lên ViewModel ngay
                onSearchClick(newCheckIn, newCheckOut, numberOfGuests)
                showCheckInPicker = false
            },
            onDismiss = { showCheckInPicker = false }
        )
    }

    if (showCheckOutPicker) {
        CalendarDatePickerDialog(
            title = "Select Check-out Date",
            currentDate = checkOutDate,
            minDate = checkInDate.plusDays(1),
            onDateSelected = {
                checkOutDate = it
                // LOG và UPDATE ngay khi chọn ngày
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                val newCheckIn = checkInDate.format(formatter)
                val newCheckOut = checkOutDate.format(formatter)

                Log.d("ContentValues", "Check-out date selected: $newCheckOut")

                // Update lên ViewModel ngay
                onSearchClick(newCheckIn, newCheckOut, numberOfGuests)

                showCheckOutPicker = false
            },
            onDismiss = { showCheckOutPicker = false }
        )
    }
}

@Composable
private fun DatePickerField(
    label: String,
    date: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MMM dd")),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("yyyy")),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
private fun GuestSelector(
    numberOfGuests: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guests",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(18.dp),
                        tint = if (numberOfGuests > 1) Color(0xFF2196F3) else Color.Gray
                    )
                }


                Text(
                    text = numberOfGuests.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(min = 24.dp)
                )

                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(18.dp),
                        tint = if (numberOfGuests < 10) Color(0xFF2196F3) else Color.Gray
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarDatePickerDialog(
    title: String,
    currentDate: LocalDate,
    minDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate
            .atStartOfDay()
            .atZone(java.time.ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // FIX: Convert millis to LocalDate in UTC
                val date = java.time.Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(java.time.ZoneId.of("UTC"))
                    .toLocalDate()

                return if (minDate != null) {
                    !date.isBefore(minDate)
                } else {
                    !date.isBefore(LocalDate.now())
                }
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // FIX: Convert back from UTC
                        val selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text("OK", color = Color(0xFF2196F3))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            title = {
                Text(
                    text = title,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
            },
            headline = {
                // Show selected date
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = java.time.Instant.ofEpochMilli(millis)
                        .atZone(java.time.ZoneId.of("UTC"))
                        .toLocalDate()
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")),
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = Color(0xFF2196F3),
                todayContentColor = Color(0xFF2196F3),
                todayDateBorderColor = Color(0xFF2196F3)
            )
        )
    }
}