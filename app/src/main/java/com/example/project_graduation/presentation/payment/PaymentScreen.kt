package com.example.project_graduation.presentation.payment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.api.BookingApi
import com.example.project_graduation.domain.model.Room
import kotlinx.coroutines.launch

//data class PaymentUiState(
//    val room: Room? = null,
//    val quantity: Int = 1,
//    val pricePerNight: Double = 0.0,
//    val totalNights: Int = 1,
//    val checkIn: String = "",
//    val checkOut: String = "",
//    val hotelName: String = "",
//    val isProcessing: Boolean = false,
//    val paymentSuccess: Boolean = false,
//    val error: String? = null
//)

enum class PaymentMethod {
//    CREDIT_CARD,
//    DEBIT_CARD,
//    PAYPAL,
    CASH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    room: Room,
    quantity: Int,
    pricePerNight: Double,
    totalNights: Int,
    checkIn: String,
    checkOut: String,
    hotelName: String,
    preferencesManager: PreferencesManager,
    viewModel: PaymentViewModel,
    onBack: () -> Unit,
    onPaymentSuccess: (bookingId: String) -> Unit,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()
    val uiState    by viewModel.uiState.collectAsState()
    val remainingMs by viewModel.remainingMs.collectAsState()


    val bookingApi = remember { BookingApi() }


    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
//    var bookingId by remember { mutableStateOf("") }
    var successBookingId by remember { mutableStateOf("") }

    val subtotal = pricePerNight * totalNights * quantity
//    val tax = subtotal * 0.1 // 10% tax
//    val totalPrice = subtotal + tax
    val totalPrice = subtotal

    // Xử lý kết quả từ ViewModel
    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is PaymentUiState.Success -> {
                successBookingId = s.bookingId
                showSuccessDialog = true
            }
            is PaymentUiState.Expired -> {
                // Hết giờ → back về RoomDetail
                onBack()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
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
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Countdown Banner
            item {
                val minutes  = (remainingMs / 60000).toInt()
                val seconds  = ((remainingMs % 60000) / 1000).toInt()
                val isUrgent = remainingMs in 1..59999

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUrgent) Color(0xFFFFEBEE) else Color(0xFFE3F2FD)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (isUrgent) Color(0xFFF44336) else Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Room held for you",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                "%02d:%02d remaining".format(minutes, seconds),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUrgent) Color(0xFFF44336) else Color(0xFF2196F3)
                            )
                        }
                    }
                }
            }
            // Booking Summary Card
            item {
                BookingSummaryCard(
                    hotelName = hotelName,
                    roomType = room.roomType,
                    quantity = quantity,
                    checkIn = checkIn,
                    checkOut = checkOut,
                    nights = totalNights
                )
            }

            // Payment Method Selection
            item {
                Text(
                    text = "Payment Method",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            item {
                PaymentMethodSelector(
                    selectedMethod = selectedPaymentMethod,
                    onMethodSelected = { selectedPaymentMethod = it }
                )
            }

            // Payment Details Form
//            if (selectedPaymentMethod == PaymentMethod.CREDIT_CARD ||
//                selectedPaymentMethod == PaymentMethod.DEBIT_CARD
//            ) {
//                item {
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(16.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color.White),
//                        elevation = CardDefaults.cardElevation(4.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(20.dp),
//                            verticalArrangement = Arrangement.spacedBy(16.dp)
//                        ) {
//                            Text(
//                                text = "Card Details",
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = Color.Black
//                            )
//
//                            // Card Number
//                            OutlinedTextField(
//                                value = cardNumber,
//                                onValueChange = { if (it.length <= 16) cardNumber = it },
//                                label = { Text("Card Number") },
//                                placeholder = { Text("1234 5678 9012 3456") },
//                                leadingIcon = {
//                                    Icon(Icons.Default.CreditCard, contentDescription = null)
//                                },
//                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                                modifier = Modifier.fillMaxWidth(),
//                                singleLine = true
//                            )
//
//                            // Card Holder Name
//                            OutlinedTextField(
//                                value = cardHolderName,
//                                onValueChange = { cardHolderName = it },
//                                label = { Text("Card Holder Name") },
//                                placeholder = { Text("John Doe") },
//                                leadingIcon = {
//                                    Icon(Icons.Default.Person, contentDescription = null)
//                                },
//                                modifier = Modifier.fillMaxWidth(),
//                                singleLine = true
//                            )
//
//                            // Expiry Date and CVV
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.spacedBy(12.dp)
//                            ) {
//                                OutlinedTextField(
//                                    value = expiryDate,
//                                    onValueChange = { if (it.length <= 5) expiryDate = it },
//                                    label = { Text("Expiry") },
//                                    placeholder = { Text("MM/YY") },
//                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                                    modifier = Modifier.weight(1f),
//                                    singleLine = true
//                                )
//
//                                OutlinedTextField(
//                                    value = cvv,
//                                    onValueChange = { if (it.length <= 3) cvv = it },
//                                    label = { Text("CVV") },
//                                    placeholder = { Text("123") },
//                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                                    modifier = Modifier.weight(1f),
//                                    singleLine = true
//                                )
//                            }
//                        }
//                    }
//                }
//            }

            // Price Breakdown
            item {
                PriceBreakdownCard(
                    subtotal = subtotal,
//                    tax = tax,
                    total = totalPrice
                )
            }
            // Error Message
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFD32F2F),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Pay Now Button
            item {
                Button(
                    onClick = {
////                        isProcessing = true
////                        // Simulate payment processing
////                        showSuccessDialog = true
//                        scope.launch {
//                            isProcessing = true
//                            errorMessage = null
//
//                            try {
//                                // 1. Get user ID
//                                val user = preferencesManager.getUser()
//                                if (user == null) {
//                                    errorMessage = "Please login to continue"
//                                    isProcessing = false
//                                    return@launch
//                                }
//
//                                val userId = user.userId
//                                Log.d("PaymentScreen", "Creating booking for userId: $userId")
//
//                                // 2. Create bookings (loop for quantity)
//                                val bookingIds = mutableListOf<Int>()
//                                var sharedBookingGroupId: String? = null
//
//                                for (i in 1..quantity) {
//                                    Log.d("PaymentScreen", "Creating booking $i/$quantity...")
//
//                                    val bookingResult = bookingApi.createBooking(
//                                        userId = userId,
//                                        roomId = room.roomId,
//                                        checkIn = checkIn,
//                                        checkOut = checkOut,
//                                        totalPrice = pricePerNight * totalNights,
//                                        bookingGroupId = sharedBookingGroupId  // Null for first, then reuse
//                                    )
//
//                                    if (bookingResult.isSuccess) {
//                                        val result = bookingResult.getOrNull()!!  // ✅ Lấy CreateBookingResult
//
//                                        bookingIds.add(result.bookingId)  // ✅ Access bookingId property
//
//                                        // Get bookingGroupId from first booking
//                                        if (sharedBookingGroupId == null) {
//                                            sharedBookingGroupId = result.bookingGroupId  // ✅ Access bookingGroupId property
//                                            Log.d("PaymentScreen", "Using booking group ID: $sharedBookingGroupId")
//                                        }
//
//                                        Log.d("PaymentScreen", "Booking $i created: ID=${result.bookingId}, Group=${result.bookingGroupId}")
//                                    } else {
//                                        val error = bookingResult.exceptionOrNull()?.message ?: "Unknown error"
//                                        throw Exception("Failed to create booking $i: $error")
//                                    }
//                                }
//
//                                // 3. Create payment with shared bookingGroupId
//                                if (sharedBookingGroupId != null) {
//                                    Log.d("PaymentScreen", "Creating payment for group: $sharedBookingGroupId")
//                                    Log.d("PaymentScreen", "Total amount: $$totalPrice")
//
//                                    val paymentResult = bookingApi.createPayment(
//                                        bookingGroupId = sharedBookingGroupId,
//                                        provider = selectedPaymentMethod.name,
//                                        amount = totalPrice,
//                                        transactionId = "TXN${System.currentTimeMillis()}"
//                                    )
//
//                                    if (paymentResult.isSuccess) {
//                                        val paymentId = paymentResult.getOrNull()!!
//                                        successBookingId = bookingIds.first().toString()
//                                        showSuccessDialog = true
//                                        Log.d("PaymentScreen", "✅ Payment successful! PaymentID=$paymentId")
//                                        Log.d("PaymentScreen", "✅ BookingIDs: $bookingIds")
//                                    } else {
//                                        val error = paymentResult.exceptionOrNull()?.message ?: "Payment failed"
//                                        throw Exception(error)
//                                    }
//                                } else {
//                                    throw Exception("No booking group ID available")
//                                }
//
//
//
//
//
////                                // 2. Create bookings (loop for quantity)
////                                val bookingIds = mutableListOf<Int>()
//////                                var firstBookingGroupId: String? = null
////                                var sharedBookingGroupId: String? = null
////
////                                for (i in 1..quantity) {
////                                    val bookingResult = bookingApi.createBooking(
////                                        userId = userId,
////                                        roomId = room.roomId,
////                                        checkIn = checkIn,
////                                        checkOut = checkOut,
////                                        totalPrice = pricePerNight * totalNights,
////                                        bookingGroupId = sharedBookingGroupId
////                                    )
////
////                                    if (bookingResult.isSuccess) {
////                                        val createdBookingId = bookingResult.getOrNull()!!
////
////                                        bookingIds.add(createdBookingId)
////
////                                        // TODO: Get booking_group_id from backend response
////                                        // For now, use temporary UUID
////                                        if (firstBookingGroupId == null) {
////                                            firstBookingGroupId =
////                                                java.util.UUID.randomUUID().toString()
////                                        }
////
//////                                        if (firstBookingGroupId == null) {
//////                                            // Get booking_group_id from first booking
//////                                            // You'll need to fetch this from backend
//////                                            firstBookingGroupId = "temp-group-id" // TODO: Get from API
//////                                        }
////////                                        bookingId = createdBookingId.toString()
////                                        Log.d(
////                                            "PaymentScreen",
////                                            "Booking $i created: $createdBookingId"
////                                        )
////                                    } else {
////                                        throw Exception("Failed to create booking")
////                                    }
////                                }
////
////                                // 3. Create payment
////                                if (firstBookingGroupId != null) {
////                                    val paymentResult = bookingApi.createPayment(
////                                        bookingGroupId = firstBookingGroupId,
////                                        provider = selectedPaymentMethod.name,
////                                        amount = totalPrice,
////                                        transactionId = "TXN${System.currentTimeMillis()}"
////                                    )
////
//////                                    if (paymentResult.isSuccess) {
//////                                        showSuccessDialog = true
//////                                    }
////                                    if (paymentResult.isSuccess) {
////                                        successBookingId = bookingIds.first().toString()
////                                        showSuccessDialog = true
////                                        Log.d("PaymentScreen", "Payment successful!")
////                                    } else {
////                                        throw Exception(
////                                            paymentResult.exceptionOrNull()?.message
////                                                ?: "Payment failed"
////                                        )
////                                    }
////                                }
//                            } catch (e: Exception) {
//                                Log.e("PaymentScreen", "Error: ${e.message}")
//                                errorMessage = e.message ?: "An error occurred. Please try again."
//                            } finally {
//                                isProcessing = false
//                            }
//                        }
                        viewModel.confirmAndPay(
                            roomId        = room.roomId,
                            checkIn       = checkIn,
                            checkOut      = checkOut,
                            pricePerNight = pricePerNight,
                            totalNights   = totalNights,
                            quantity      = quantity,
                            paymentMethod = selectedPaymentMethod.name
                        )

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
//                    enabled = !isProcessing && isPaymentFormValid(
                    enabled = uiState is PaymentUiState.ReadyToPay && isPaymentFormValid(
                        selectedPaymentMethod,
                        cardNumber,
                        cardHolderName,
                        expiryDate,
                        cvv
                    )
                ) {
//                    if (isProcessing) {
                    if (uiState is PaymentUiState.Processing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Pay $${"%.2f".format(totalPrice)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        PaymentSuccessDialog(
//            bookingId = "BK${System.currentTimeMillis().toString().takeLast(8)}",
//            totalAmount = totalPrice,
//            onDismiss = {
//                showSuccessDialog = false
//                onPaymentSuccess("BK${System.currentTimeMillis().toString().takeLast(8)}")
//            }
            bookingId = successBookingId,
            totalAmount = totalPrice,
            onDismiss = {
                showSuccessDialog = false
                onPaymentSuccess(successBookingId)
            }
        )
    }
}

@Composable
fun BookingSummaryCard(
    hotelName: String,
    roomType: String,
    quantity: Int,
    checkIn: String,
    checkOut: String,
    nights: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Booking Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            HorizontalDivider(color = Color.LightGray)

            // Hotel Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hotel",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = hotelName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            // Room Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Room Type",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = roomType,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            // Quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Number of Rooms",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$quantity room${if (quantity > 1) "s" else ""}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            HorizontalDivider(color = Color.LightGray)

            // Check-in
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Login,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Check-in",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = checkIn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            // Check-out
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Check-out",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = checkOut,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            // Nights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.NightsStay,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nights",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "$nights night${if (nights > 1) "s" else ""}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
//        PaymentMethodItem(
//            icon = Icons.Default.CreditCard,
//            title = "Credit Card",
//            isSelected = selectedMethod == PaymentMethod.CREDIT_CARD,
//            onClick = { onMethodSelected(PaymentMethod.CREDIT_CARD) }
//        )
//
//        PaymentMethodItem(
//            icon = Icons.Default.AccountBalance,
//            title = "Debit Card",
//            isSelected = selectedMethod == PaymentMethod.DEBIT_CARD,
//            onClick = { onMethodSelected(PaymentMethod.DEBIT_CARD) }
//        )
//
//        PaymentMethodItem(
//            icon = Icons.Default.AccountBalanceWallet,
//            title = "PayPal",
//            isSelected = selectedMethod == PaymentMethod.PAYPAL,
//            onClick = { onMethodSelected(PaymentMethod.PAYPAL) }
//        )

        PaymentMethodItem(
            icon = Icons.Default.Money,
            title = "Pay at Hotel (Cash)",
            isSelected = selectedMethod == PaymentMethod.CASH,
            onClick = { onMethodSelected(PaymentMethod.CASH) }
        )
    }
}

@Composable
fun PaymentMethodItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFF2196F3),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFF2196F3).copy(alpha = 0.1f)
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF2196F3) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = Color.Black
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF2196F3)
                )
            )
        }
    }
}

@Composable
fun PriceBreakdownCard(
    subtotal: Double,
//    tax: Double,
    total: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Price Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            HorizontalDivider(color = Color.LightGray)

            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$${"%.2f".format(subtotal)}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            // Tax
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "Tax (10%)",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//                Text(
//                    text = "$${"%.2f".format(tax)}",
//                    fontSize = 14.sp,
//                    color = Color.Black
//                )
//            }

            HorizontalDivider(color = Color.LightGray)

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "$${"%.2f".format(total)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
fun PaymentSuccessDialog(
    bookingId: String,
    totalAmount: Double,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(64.dp)
            )
        },
        title = {
            Text(
                text = "Payment Successful!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Your booking has been confirmed",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Booking ID",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = bookingId,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Amount Paid",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$${"%.2f".format(totalAmount)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("View My Bookings")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

fun isPaymentFormValid(
    method: PaymentMethod,
    cardNumber: String,
    cardHolderName: String,
    expiryDate: String,
    cvv: String
): Boolean {
    return when (method) {
//        PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> {
//            cardNumber.length == 16 &&
//                    cardHolderName.isNotBlank() &&
//                    expiryDate.length >= 4 &&
//                    cvv.length == 3
//        }

//        PaymentMethod.PAYPAL,
        PaymentMethod.CASH -> true
    }
}

// ============= PREVIEWS =============

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PaymentScreenPreview() {
//    val mockRoom = Room(
//        roomId = 1,
//        hotelId = 1,
//        roomNumber = "201",
//        roomType = "Deluxe Room",
//        floor = 2,
//        status = "AVAILABLE",
//        basePrice = 350.0,
//        capacity = 3,
//        description = "Spacious deluxe room",
//        amenities = listOf("WiFi", "TV", "AC"),
//        imageUrl = null
//    )
//
//    MaterialTheme {
//        PaymentScreen(
//            room = mockRoom,
//            quantity = 2,
//            pricePerNight = 350.0,
//            totalNights = 3,
//            checkIn = "2026-01-15",
//            checkOut = "2026-01-18",
//            hotelName = "Grand Palace Hotel",
////            preferencesManager = PreferencesManager,
//            onBack = {},
//            onPaymentSuccess = {}
//        )
//    }
//}