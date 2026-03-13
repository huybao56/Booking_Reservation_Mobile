package com.example.project_graduation.presentation.room_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.remote.api.BookingSessionApi
import com.example.project_graduation.data.remote.api.LockSessionResult
import com.example.project_graduation.domain.model.Room
import com.example.project_graduation.domain.repository.RoomRepository
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

data class RoomDetailState(
    val room: Room? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// ─── Session state ────────────────────────────────────────────────────────────
sealed class BookNowState {
    object Idle      : BookNowState()              // Chưa làm gì
    object Locking   : BookNowState()              // Đang gọi POST /lock
    data class Success(                            // Lock thành công → navigate
        val sessionId      : String,
        val expiresAt      : String,
        val timeoutSeconds : Int
    ) : BookNowState()
    data class RoomLocked(                         // Phòng bị user khác giữ
        val expiresAt: String
    ) : BookNowState()
    data class Error(val message: String) : BookNowState()
}

class RoomDetailViewModel(
    private val roomRepository: RoomRepository,
    private val bookingSessionApi : BookingSessionApi = BookingSessionApi()
) : ViewModel() {

    private val _state = MutableStateFlow(RoomDetailState())
    val state: StateFlow<RoomDetailState> = _state.asStateFlow()


    // ─── Book Now state ───────────────────────────────────────────────────────
    private val _bookNowState = MutableStateFlow<BookNowState>(BookNowState.Idle)
    val bookNowState: StateFlow<BookNowState> = _bookNowState.asStateFlow()



    fun loadRoomDetail(roomId: Int) {
        viewModelScope.launch {
            _state.value = RoomDetailState(isLoading = true)
            Log.d("RoomDetailViewModel", "Loading room ID: $roomId")

            roomRepository.getRoomById(roomId).fold(
                onSuccess = { room ->
                    _state.value = RoomDetailState(room = room, isLoading = false)
                    Log.d(
                        "RoomDetailViewModel",
                        "Loaded room: ${room.roomNumber}, images: ${room.images.size}"
                    )
                },
                onFailure = { error ->
                    _state.value = RoomDetailState(
                        isLoading = false,
                        error = error.message ?: "Failed to load room"
                    )
                    Log.e("RoomDetailViewModel", "Error: ${error.message}")
                }
            )
        }
    }

    // ─── User nhấn "Book Now" → lock phòng → navigate PaymentScreen ──────────
    fun onBookNow(userId: Int, roomId: Int, checkIn: String, checkOut: String) {
        viewModelScope.launch {
            _bookNowState.value = BookNowState.Locking

            bookingSessionApi.lockRoom(userId, roomId, checkIn, checkOut).fold(
                onSuccess = { result ->
                    Log.d("RoomDetailVM", "Lock success → sessionId: ${result.sessionId}")
                    // Emit Success → NavGraph sẽ navigate sang PaymentScreen
                    _bookNowState.value = BookNowState.Success(
                        sessionId      = result.sessionId,
                        expiresAt      = result.expiresAt,
                        timeoutSeconds = result.timeoutSeconds
                    )
                },
                onFailure = { error ->
                    val msg = error.message ?: ""
                    _bookNowState.value = if (msg.startsWith("ROOM_LOCKED:")) {
                        BookNowState.RoomLocked(msg.removePrefix("ROOM_LOCKED:"))
                    } else {
                        BookNowState.Error(msg)
                    }
                }
            )
        }
    }

    // ─── Reset sau khi đã navigate hoặc dismiss dialog ───────────────────────
    fun resetBookNowState() {
        _bookNowState.value = BookNowState.Idle
    }
}