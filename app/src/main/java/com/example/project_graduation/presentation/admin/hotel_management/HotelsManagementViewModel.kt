package com.example.project_graduation.presentation.admin.hotel_management

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.remote.api.UploadApi
import com.example.project_graduation.domain.model.Hotel
import com.example.project_graduation.domain.repository.HotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotelsManagementViewModel(
    private val hotelRepository: HotelRepository,
    private val uploadApi: UploadApi
) : ViewModel() {

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()

    // Upload progress
    private val _uploadProgress = MutableStateFlow<String?>(null)
    val uploadProgress: StateFlow<String?> = _uploadProgress.asStateFlow()

    fun loadHotels() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("HotelsManagementVM", "Loading hotels from API...")

            hotelRepository.getAllHotels().fold(
                onSuccess = { hotels ->
                    _hotels.value = hotels
                    Log.d("HotelsManagementVM", "Loaded ${hotels.size} hotels successfully")
                },
                onFailure = { error ->
                    _hotels.value = emptyList()
                    _error.value = error.message ?: "Failed to load hotels"
                    Log.e("HotelsManagementVM", "Error loading hotels: ${error.message}")
                }
            )

            _isLoading.value = false
        }
    }

    // TODO: Implement CRUD operations
//    fun createHotel(
//        hotelName: String,
//        description: String?,
//        rating: Double?,
//        hotelAddress: String?
//    ) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//
//            Log.d("HotelsManagementVM", "Creating hotel: $hotelName")
//
//            hotelRepository.createHotel(hotelName, description, rating, hotelAddress).fold(
//                onSuccess = { hotelId ->
//                    _operationSuccess.value = "Hotel created successfully!"
//                    Log.d("HotelsManagementVM", "Hotel created with ID: $hotelId")
//
//                    // Reload hotels list
//                    loadHotels()
//                },
//                onFailure = { error ->
//                    _error.value = "Failed to create hotel: ${error.message}"
//                    Log.e("HotelsManagementVM", "Error creating hotel: ${error.message}")
//                    _isLoading.value = false
//                }
//            )
//        }
//    }
//
//    fun updateHotel(
//        hotelId: Int,
//        hotelName: String?,
//        description: String?,
//        rating: Double?,
//        hotelAddress: String?
//    ) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//
//            Log.d("HotelsManagementVM", "Updating hotel: $hotelId")
//
//            hotelRepository.updateHotel(hotelId, hotelName, description, rating, hotelAddress).fold(
//                onSuccess = {
//                    _operationSuccess.value = "Hotel updated successfully!"
//                    Log.d("HotelsManagementVM", "Hotel updated successfully")
//
//                    // Reload hotels list
//                    loadHotels()
//                },
//                onFailure = { error ->
//                    _error.value = "Failed to update hotel: ${error.message}"
//                    Log.e("HotelsManagementVM", "Error updating hotel: ${error.message}")
//                    _isLoading.value = false
//                }
//            )
//        }
//    }

    /**
     * Tạo hotel mới với ảnh
     * Luồng: Tạo hotel -> Upload ảnh -> Reload danh sách
     */
    fun createHotelWithImage(
        hotel: Hotel,
        imageUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _uploadProgress.value = null

            Log.d("HotelsManagementVM", "Creating hotel: ${hotel.hotelName}")

            try {
                // Bước 1: Tạo hotel trước (không có ảnh)
                _uploadProgress.value = "Creating hotel..."

                val createResult = hotelRepository.createHotel(hotel)

                createResult.fold(
                    onSuccess = { hotelId ->
                        Log.d("HotelsManagementVM", "Hotel created with ID: $hotelId")

                        // Bước 2: Upload ảnh nếu có
                        if (imageUri != null) {
                            _uploadProgress.value = "Uploading image..."

                            uploadApi.uploadHotelImage(
                                hotelId = hotelId,
                                imageUri = imageUri,
                                context = context,
                                isPrimary = true, // Ảnh đầu tiên là ảnh chính
                                displayOrder = 0
                            ).fold(
                                onSuccess = { imageUrl ->
                                    Log.d("HotelsManagementVM", "Image uploaded: $imageUrl")
                                    _operationSuccess.value = "Hotel created successfully with image!"

                                    // Reload danh sách để thấy ảnh
                                    loadHotels()
                                },
                                onFailure = { error ->
                                    Log.e("HotelsManagementVM", "Image upload failed: ${error.message}")
                                    _operationSuccess.value = "Hotel created but image upload failed"
                                    _isLoading.value = false
                                    loadHotels()
                                }
                            )
                        } else {
                            // Không có ảnh
                            _operationSuccess.value = "Hotel created successfully!"
                            loadHotels()
                        }
                    },
                    onFailure = { error ->
                        _error.value = "Failed to create hotel: ${error.message}"
                        Log.e("HotelsManagementVM", "Error creating hotel: ${error.message}")
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("HotelsManagementVM", "Exception: ${e.message}", e)
                _isLoading.value = false
            } finally {
                _uploadProgress.value = null
            }
        }
    }


    /**
     * Cập nhật hotel với ảnh mới (nếu có)
     */
    fun updateHotelWithImage(
        hotelId: Int,
        hotel: Hotel,
        newImageUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _uploadProgress.value = null

            Log.d("HotelsManagementVM", "Updating hotel: $hotelId")

            try {
                // Bước 1: Update hotel info
                _uploadProgress.value = "Updating hotel..."

                hotelRepository.updateHotel(hotelId, hotel).fold(
                    onSuccess = {
                        Log.d("HotelsManagementVM", "Hotel updated successfully")

                        // Bước 2: Upload ảnh mới nếu có
                        if (newImageUri != null) {
                            _uploadProgress.value = "Uploading new image..."

                            uploadApi.uploadHotelImage(
                                hotelId = hotelId,
                                imageUri = newImageUri,
                                context = context,
                                isPrimary = true,
                                displayOrder = 0
                            ).fold(
                                onSuccess = { imageUrl ->
                                    Log.d("HotelsManagementVM", "New image uploaded: $imageUrl")
                                    _operationSuccess.value = "Hotel updated with new image!"
                                    loadHotels()
                                },
                                onFailure = { error ->
                                    Log.e("HotelsManagementVM", "Image upload failed: ${error.message}")
                                    _operationSuccess.value = "Hotel updated but image upload failed"
                                    _isLoading.value = false
                                    loadHotels()
                                }
                            )
                        } else {
                            _operationSuccess.value = "Hotel updated successfully!"
                            loadHotels()
                        }
                    },
                    onFailure = { error ->
                        _error.value = "Failed to update hotel: ${error.message}"
                        Log.e("HotelsManagementVM", "Error updating hotel: ${error.message}")
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("HotelsManagementVM", "Exception: ${e.message}", e)
                _isLoading.value = false
            } finally {
                _uploadProgress.value = null
            }
        }
    }


    /**
     * Phương thức cũ - giữ để backward compatibility
     */
    fun createHotel(hotel: Hotel) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("HotelsManagementVM", "Creating hotel: ${hotel.hotelName}")

            hotelRepository.createHotel(hotel).fold(
                onSuccess = { hotelId ->
                    _operationSuccess.value = "Hotel created successfully!"
                    Log.d("HotelsManagementVM", "Hotel created with ID: $hotelId")
                    loadHotels()
                },
                onFailure = { error ->
                    _error.value = "Failed to create hotel: ${error.message}"
                    Log.e("HotelsManagementVM", "Error creating hotel: ${error.message}")
                    _isLoading.value = false
                }
            )
        }
    }

    fun updateHotel(hotelId: Int, hotel: Hotel) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("HotelsManagementVM", "Updating hotel: $hotelId")

            hotelRepository.updateHotel(hotelId, hotel).fold(
                onSuccess = {
                    _operationSuccess.value = "Hotel updated successfully!"
                    Log.d("HotelsManagementVM", "Hotel updated successfully")
                    loadHotels()
                },
                onFailure = { error ->
                    _error.value = "Failed to update hotel: ${error.message}"
                    Log.e("HotelsManagementVM", "Error updating hotel: ${error.message}")
                    _isLoading.value = false
                }
            )
        }
    }





    fun deleteHotel(hotelId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("HotelsManagementVM", "Deleting hotel: $hotelId")

            hotelRepository.deleteHotel(hotelId).fold(
                onSuccess = {
                    _operationSuccess.value = "Hotel deleted successfully!"
                    Log.d("HotelsManagementVM", "Hotel deleted successfully")

                    // Reload hotels list
                    loadHotels()
                },
                onFailure = { error ->
                    _error.value = "Failed to delete hotel: ${error.message}"
                    Log.e("HotelsManagementVM", "Error deleting hotel: ${error.message}")
                    _isLoading.value = false
                }
            )
        }
    }

    fun clearSuccessMessage() {
        _operationSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }
}