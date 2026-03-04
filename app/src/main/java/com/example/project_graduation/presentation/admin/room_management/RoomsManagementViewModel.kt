package com.example.project_graduation.presentation.admin.room_management

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.remote.api.UploadApi
import com.example.project_graduation.domain.model.Room
import com.example.project_graduation.domain.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomsManagementViewModel(
    private val roomRepository: RoomRepository,
    private val uploadApi: UploadApi
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    private val _uploadProgress = MutableStateFlow<String?>(null)
    val uploadProgress: StateFlow<String?> = _uploadProgress.asStateFlow()

    private val _currentHotelId = MutableStateFlow<Int?>(null)
    val currentHotelId: StateFlow<Int?> = _currentHotelId.asStateFlow()

    private val _currentHotelName = MutableStateFlow<String>("")
    val currentHotelName: StateFlow<String> = _currentHotelName.asStateFlow()

    // Room statistics
    val totalRooms get() = _rooms.value.size
    val availableRooms get() = _rooms.value.count { it.status == "AVAILABLE" }
    val occupiedRooms get() = _rooms.value.count { it.status == "OCCUPIED" }
    val maintenanceRooms get() = _rooms.value.count { it.status == "MAINTENANCE" }

    fun loadRooms(hotelId: Int, hotelName: String) {
        viewModelScope.launch {
            _currentHotelId.value = hotelId
            _currentHotelName.value = hotelName
            _isLoading.value = true
            _error.value = null

            Log.d("RoomsManagementVM", "Loading rooms for hotel: $hotelId ($hotelName)")

            roomRepository.getRoomsByHotel(hotelId).fold(
                onSuccess = { rooms ->
                    _rooms.value = rooms
                    Log.d("RoomsManagementVM", "Loaded ${rooms.size} rooms")
                },
                onFailure = { error ->
                    _rooms.value = emptyList()
                    _error.value = error.message ?: "Failed to load rooms"
                    Log.e("RoomsManagementVM", "Error: ${error.message}")
                }
            )

            _isLoading.value = false
        }
    }
    /**
     * Tạo room mới với ảnh
     * Luồng: Tạo room -> Upload ảnh -> Reload danh sách
     */
    fun createRoomWithImage(
        hotelId: Int,
        room: Room,
        imageUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _uploadProgress.value = null

            Log.d("RoomsManagementVM", "Creating room: ${room.roomNumber}")

            try {
                // Bước 1: Tạo room trước
                _uploadProgress.value = "Creating room..."

                roomRepository.createRoom(
                    hotelId = hotelId,
                    roomNumber = room.roomNumber,
                    roomType = room.roomType,
                    floor = room.floor ?: 1,
                    basePrice = room.basePrice ?: 0.0,
                    capacity = room.capacity ?: 2,
                    description = room.description,
                    amenities = room.amenities ?: emptyList()
                ).fold(
                    onSuccess = { roomId ->
                        Log.d("RoomsManagementVM", "Room created with ID: $roomId")

                        // Bước 2: Upload ảnh nếu có
                        if (imageUri != null) {
                            _uploadProgress.value = "Uploading image..."

                            uploadApi.uploadRoomImage(
                                roomId = roomId,
                                imageUri = imageUri,
                                context = context,
                                isPrimary = true,
                                displayOrder = 0
                            ).fold(
                                onSuccess = { imageUrl ->
                                    Log.d("RoomsManagementVM", "Image uploaded: $imageUrl")
                                    _operationSuccess.value = "Room created successfully with image!"
                                    loadRooms(hotelId, _currentHotelName.value)
                                },
                                onFailure = { error ->
                                    Log.e("RoomsManagementVM", "Image upload failed: ${error.message}")
                                    _operationSuccess.value = "Room created but image upload failed"
                                    _isLoading.value = false
                                    loadRooms(hotelId, _currentHotelName.value)
                                }
                            )
                        } else {
                            _operationSuccess.value = "Room created successfully!"
                            loadRooms(hotelId, _currentHotelName.value)
                        }
                    },
                    onFailure = { error ->
                        _error.value = "Failed to create room: ${error.message}"
                        Log.e("RoomsManagementVM", "Error creating room: ${error.message}")
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("RoomsManagementVM", "Exception: ${e.message}", e)
                _isLoading.value = false
            } finally {
                _uploadProgress.value = null
            }
        }
    }

    /**
     * Cập nhật room với ảnh mới (nếu có)
     */
    fun updateRoomWithImage(
        roomId: Int,
        room: Room,
        newImageUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _uploadProgress.value = null

            Log.d("RoomsManagementVM", "Updating room: $roomId")

            try {
                // Bước 1: Update room info
                _uploadProgress.value = "Updating room..."

                roomRepository.updateRoom(
                    roomId = roomId,
                    roomNumber = room.roomNumber,
                    roomType = room.roomType,
                    floor = room.floor,
                    status = room.status,
                    basePrice = room.basePrice,
                    capacity = room.capacity,
                    description = room.description,
                    amenities = room.amenities
                ).fold(
                    onSuccess = {
                        Log.d("RoomsManagementVM", "Room updated successfully")

                        // Bước 2: Upload ảnh mới nếu có
                        if (newImageUri != null) {
                            _uploadProgress.value = "Uploading new image..."

                            uploadApi.uploadRoomImage(
                                roomId = roomId,
                                imageUri = newImageUri,
                                context = context,
                                isPrimary = true,
                                displayOrder = 0
                            ).fold(
                                onSuccess = { imageUrl ->
                                    Log.d("RoomsManagementVM", "New image uploaded: $imageUrl")
                                    _operationSuccess.value = "Room updated with new image!"
                                    _currentHotelId.value?.let { hotelId ->
                                        loadRooms(hotelId, _currentHotelName.value)
                                    }
                                },
                                onFailure = { error ->
                                    Log.e("RoomsManagementVM", "Image upload failed: ${error.message}")
                                    _operationSuccess.value = "Room updated but image upload failed"
                                    _isLoading.value = false
                                    _currentHotelId.value?.let { hotelId ->
                                        loadRooms(hotelId, _currentHotelName.value)
                                    }
                                }
                            )
                        } else {
                            _operationSuccess.value = "Room updated successfully!"
                            _currentHotelId.value?.let { hotelId ->
                                loadRooms(hotelId, _currentHotelName.value)
                            }
                        }
                    },
                    onFailure = { error ->
                        _error.value = "Failed to update room: ${error.message}"
                        Log.e("RoomsManagementVM", "Error updating room: ${error.message}")
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("RoomsManagementVM", "Exception: ${e.message}", e)
                _isLoading.value = false
            } finally {
                _uploadProgress.value = null
            }
        }
    }
    fun createRoom(
        hotelId: Int,
        roomNumber: String,
        roomType: String,
        floor: Int,
        basePrice: Double,
        capacity: Int,
        description: String?,
        amenities: List<String>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("RoomsManagementVM", "Creating room: $roomNumber")

            roomRepository.createRoom(
                hotelId = hotelId,
                roomNumber = roomNumber,
                roomType = roomType,
                floor = floor,
                basePrice = basePrice,
                capacity = capacity,
                description = description,
                amenities = amenities
            ).fold(
                onSuccess = { roomId ->
                    _operationSuccess.value = "Room $roomNumber created successfully!"
                    Log.d("RoomsManagementVM", "Room created with ID: $roomId")

                    // Reload rooms list
                    loadRooms(hotelId, _currentHotelName.value)
                },
                onFailure = { error ->
                    _error.value = "Failed to create room: ${error.message}"
                    Log.e("RoomsManagementVM", "Error creating room: ${error.message}")
                    _isLoading.value = false
                }
            )


            // Mock success for now
//            _operationSuccess.value = "Room $roomNumber created successfully!"
//            loadRooms(hotelId, _currentHotelName.value)
        }
    }

    fun updateRoom(
        roomId: Int,
        roomNumber: String?,
        roomType: String?,
        floor: Int?,
        status: String?,
        basePrice: Double?,
        capacity: Int?,
        description: String?,
        amenities: List<String>?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("RoomsManagementVM", "Updating room: $roomId")

            roomRepository.updateRoom(
                roomId = roomId,
                roomNumber = roomNumber,
                roomType = roomType,
                floor = floor,
                status = status,
                basePrice = basePrice,
                capacity = capacity,
                description = description,
                amenities = amenities
            ).fold(
                onSuccess = {
                    _operationSuccess.value = "Room updated successfully!"
                    Log.d("RoomsManagementVM", "Room updated successfully")

                    // Reload rooms list
                    _currentHotelId.value?.let { hotelId ->
                        loadRooms(hotelId, _currentHotelName.value)
                    }
                },
                onFailure = { error ->
                    _error.value = "Failed to update room: ${error.message}"
                    Log.e("RoomsManagementVM", "Error updating room: ${error.message}")
                    _isLoading.value = false
                }
            )


//            _operationSuccess.value = "Room updated successfully!"
//            _currentHotelId.value?.let { hotelId ->
//                loadRooms(hotelId, _currentHotelName.value)
//            }
        }
    }

    fun deleteRoom(roomId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("RoomsManagementVM", "Deleting room: $roomId")

            roomRepository.deleteRoom(roomId).fold(
                onSuccess = {
                    _operationSuccess.value = "Room deleted successfully!"
                    Log.d("RoomsManagementVM", "Room deleted successfully")

                    // Reload rooms list
                    _currentHotelId.value?.let { hotelId ->
                        loadRooms(hotelId, _currentHotelName.value)
                    }
                },
                onFailure = { error ->
                    _error.value = "Failed to delete room: ${error.message}"
                    Log.e("RoomsManagementVM", "Error deleting room: ${error.message}")
                    _isLoading.value = false
                }
            )

//            _operationSuccess.value = "Room deleted successfully!"
//            _currentHotelId.value?.let { hotelId ->
//                loadRooms(hotelId, _currentHotelName.value)
//            }
        }
    }

    fun clearSuccessMessage() {
        _operationSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }
}