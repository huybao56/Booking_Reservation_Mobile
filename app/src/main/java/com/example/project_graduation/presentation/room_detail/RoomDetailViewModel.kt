package com.example.project_graduation.presentation.room_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.model.Room
import com.example.project_graduation.domain.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RoomDetailState(
    val room: Room? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RoomDetailViewModel(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RoomDetailState())
    val state: StateFlow<RoomDetailState> = _state.asStateFlow()

    fun loadRoomDetail(roomId: Int) {
        viewModelScope.launch {
            _state.value = RoomDetailState(isLoading = true)
            Log.d("RoomDetailViewModel", "Loading room ID: $roomId")

            roomRepository.getRoomById(roomId).fold(
                onSuccess = { room ->
                    _state.value = RoomDetailState(room = room, isLoading = false)
                    Log.d("RoomDetailViewModel", "Loaded room: ${room.roomNumber}, images: ${room.images.size}")
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
}