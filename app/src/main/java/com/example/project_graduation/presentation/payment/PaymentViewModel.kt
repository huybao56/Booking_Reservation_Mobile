package com.example.project_graduation.presentation.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.api.BookingApi
import com.example.project_graduation.data.remote.api.BookingSessionApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// ─── Payment UI State ─────────────────────────────────────────────────────────
sealed class PaymentUiState {
    data class ReadyToPay(                     // Đang đếm ngược, chờ user confirm
        val remainingMs: Long
    ) : PaymentUiState()

    object Processing : PaymentUiState()      // Đang tạo booking + payment
    data class Success(
        val bookingId: String
    ) : PaymentUiState()

    object Expired : PaymentUiState()      // Hết giờ → back về RoomDetail
    data class Error(val message: String) : PaymentUiState()
}

class PaymentViewModel(
    private val preferencesManager: PreferencesManager,
    private val bookingSessionApi: BookingSessionApi = BookingSessionApi(),
    private val bookingApi: BookingApi = BookingApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(
        PaymentUiState.ReadyToPay(remainingMs = 600_000L)
    )
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _remainingMs = MutableStateFlow(0L)
    val remainingMs: StateFlow<Long> = _remainingMs.asStateFlow()

    private var webSocket: WebSocket? = null
    private var currentSessionId: String? = null

    private var countdownJob: Job? = null

    // FIX: Lưu timestamp bắt đầu để tính toán chính xác hơn
    private var countdownStartTime: Long = 0L
    private var countdownInitialMs: Long = 0L


    private val wsClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    // ─── Gọi ngay khi PaymentScreen mở ───────────────────────────────────────
    // sessionId nhận từ RoomDetailViewModel qua NavGraph
    fun initWithSession(sessionId: String, timeoutSeconds: Int) {
        countdownJob?.cancel()
        countdownJob = null
        webSocket?.close(1000, "Re-init")
        webSocket = null

        currentSessionId = sessionId
        _remainingMs.value = timeoutSeconds * 1000L
        _uiState.value = PaymentUiState.ReadyToPay(timeoutSeconds * 1000L)

        // Connect WebSocket → nhận countdown chính xác từ server
        connectWebSocket(sessionId)
    }

    // ─── Connect WebSocket ────────────────────────────────────────────────────
    private fun connectWebSocket(sessionId: String) {
        val request = Request.Builder()
            .url(bookingSessionApi.wsUrl(sessionId))
            .build()

        webSocket = wsClient.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("PaymentVM", "WS connected: $sessionId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("PaymentVM", "WS message: $text")
                try {
                    val json = JSONObject(text)
                    when (json.getString("type")) {

                        "SESSION_ACTIVE" -> {
                            val remaining = json.getLong("remainingMs")
                            _remainingMs.value = remaining
                            _uiState.value = PaymentUiState.ReadyToPay(remaining)
                            startLocalCountdown(remaining)
                        }

                        "SESSION_EXPIRED" -> {
                            _remainingMs.value = 0L
                            _uiState.value = PaymentUiState.Expired
                        }

                        "SESSION_CONFIRMED" -> {
                            // WS confirm xong → tạo booking + payment
                        }

                        "PONG" -> {
                            val remaining = json.getLong("remainingMs")
                            _remainingMs.value = remaining
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PaymentVM", "WS parse error: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("PaymentVM", "WS failure: ${t.message}")
                // WS lỗi → vẫn cho phép pay, dùng countdown local
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("PaymentVM", "WS closed: $reason")
            }
        })
    }

    // ─── Countdown local mỗi giây ────────────────────────────────────────────
    private fun startLocalCountdown(initialMs: Long): Job {
        countdownJob?.cancel() // Cancel countdown cũ nếu có

        countdownStartTime = System.currentTimeMillis()
        countdownInitialMs = initialMs

        countdownJob = viewModelScope.launch {
            while (true) {
                // Tính thời gian đã trôi qua THỰC TẾ
                val elapsedMs = System.currentTimeMillis() - countdownStartTime
                val remaining = (countdownInitialMs - elapsedMs).coerceAtLeast(0L)

                _remainingMs.value = remaining

                // Kiểm tra điều kiện dừng
                if (remaining <= 0L) {
                    Log.d("PaymentVM", "Countdown ended → Expired")
                    _uiState.value = PaymentUiState.Expired
                    webSocket?.close(1000, "Session expired")
                    webSocket = null
                    break
                }

                if (_uiState.value !is PaymentUiState.ReadyToPay) {
                    Log.d("PaymentVM", "State changed → Stop countdown")
                    break
                }

                // Delay ngắn hơn để update UI mượt hơn (mỗi 100ms thay vì 1000ms)
                kotlinx.coroutines.delay(100)
            }
        }

        return countdownJob!!
//        return viewModelScope.launch {
////            var remaining = initialMs
////            while (remaining > 0 && _uiState.value is PaymentUiState.ReadyToPay) {
////                kotlinx.coroutines.delay(1_000)
////                remaining -= 1_000
////                _remainingMs.value = maxOf(remaining, 0L)
////            }
//
//            var remaining = initialMs
//            while (remaining > 0
//                && _uiState.value is PaymentUiState.ReadyToPay
//                && webSocket != null
//            ) {
//                kotlinx.coroutines.delay(1_000)
//                remaining -= 1_000
//                _remainingMs.value = maxOf(remaining, 0L)
//            }
//
//            if (_uiState.value is PaymentUiState.ReadyToPay && webSocket != null) {
//                Log.d("PaymentVM", "Countdown ended → closing WebSocket")
//                _remainingMs.value = 0L
//                _uiState.value = PaymentUiState.Expired
//                webSocket?.close(1000, "Session expired")
//                webSocket = null
//                countdownJob = null
//            }
//        }
    }

    // ─── User nhấn "Confirm & Pay" ────────────────────────────────────────────
    fun confirmAndPay(
        roomId: Int,
        checkIn: String,
        checkOut: String,
        pricePerNight: Double,
        totalNights: Int,
        quantity: Int,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Processing

            try {
                val user = preferencesManager.getUser()
                if (user == null) {
                    _uiState.value = PaymentUiState.Error("Please login to continue")
                    return@launch
                }

                // 1. Gửi CONFIRM qua WebSocket → server đổi session PENDING → CONFIRMED
                webSocket?.send("CONFIRM")

                // 2. Tạo bookings (loop cho quantity)
                val totalPrice = pricePerNight * totalNights
                var sharedGroupId: String? = null
                val bookingIds = mutableListOf<Int>()

                for (i in 1..quantity) {
                    val result = bookingApi.createBooking(
                        userId = user.userId,
                        roomId = roomId,
                        checkIn = checkIn,
                        checkOut = checkOut,
                        totalPrice = totalPrice,
                        bookingGroupId = sharedGroupId
                    ).getOrThrow()

                    bookingIds.add(result.bookingId)
                    if (sharedGroupId == null) sharedGroupId = result.bookingGroupId
                    Log.d("PaymentVM", "Booking $i created: ${result.bookingId}")
                }

                // 3. Tạo payment
                val paymentResult = bookingApi.createPayment(
                    bookingGroupId = sharedGroupId!!,
                    provider = paymentMethod,
                    amount = totalPrice * quantity,
                    transactionId = "TXN${System.currentTimeMillis()}"
                ).getOrThrow()

                Log.d("PaymentVM", "Payment created: $paymentResult")
                _uiState.value = PaymentUiState.Success(bookingIds.first().toString())

            } catch (e: Exception) {
                Log.e("PaymentVM", "confirmAndPay error: ${e.message}", e)
                _uiState.value = PaymentUiState.Error(e.message ?: "Payment failed")
            }
        }
    }

    // ─── User back về RoomDetail → cancel session ─────────────────────────────
    fun cancelSession() {
//        webSocket?.send("CANCEL")
//        webSocket?.close(1000, "User cancelled")
        countdownJob?.cancel()
        countdownJob = null
        webSocket?.send("CANCEL")
        webSocket?.close(1000, "User cancelled")
        webSocket = null
        currentSessionId = null
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        webSocket?.send("CANCEL")
        webSocket?.close(1000, "ViewModel cleared")
        webSocket = null
    //        webSocket?.send("CANCEL")
//        webSocket?.close(1000, "ViewModel cleared")
    }
}