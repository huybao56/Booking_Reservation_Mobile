package com.example.project_graduation.presentation.favorite


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.data.remote.dto.FavoriteDto
import com.example.project_graduation.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoriteState(
    val favorites: List<FavoriteDto> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),   // dùng để check nhanh trên HotelCard
    val isLoading: Boolean = false,
    val error: String? = null
)

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteState())
    val state: StateFlow<FavoriteState> = _state.asStateFlow()

    // Load danh sách favorites + ids
    fun loadFavorites(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            favoriteRepository.getFavorites(userId).fold(
                onSuccess = { list ->
                    _state.value = _state.value.copy(
                        favorites  = list,
                        favoriteIds = list.map { it.hotelId }.toSet(),
                        isLoading  = false
                    )
                    Log.d("FavoriteViewModel", "Loaded ${list.size} favorites")
                },
                onFailure = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error     = it.message
                    )
                }
            )
        }
    }

    // Load chỉ ids — dùng trên HomeScreen để highlight nút ❤️
    fun loadFavoriteIds(userId: Int) {
        viewModelScope.launch {
            favoriteRepository.getFavoriteIds(userId).fold(
                onSuccess = { ids ->
                    val idSet = ids.toSet()
                    // Chỉ update nếu data thực sự thay đổi — tránh recompose thừa
                    if (idSet != _state.value.favoriteIds) {
                        _state.value = _state.value.copy(favoriteIds = idSet)
                        Log.d("FavoriteViewModel", "favoriteIds updated: $idSet")
                    }
                            //                    _state.value = _state.value.copy(favoriteIds = ids.toSet())
                },
                onFailure = {
                    Log.e("FavoriteViewModel", "loadFavoriteIds error: ${it.message}")
                }
            )
        }
    }

    // Toggle favorite — add nếu chưa có, remove nếu đã có
    fun toggleFavorite(userId: Int, hotelId: Int) {
        val isFav = _state.value.favoriteIds.contains(hotelId)
        viewModelScope.launch {
            if (isFav) {
                // Optimistic update — xóa trước rồi call API
                _state.value = _state.value.copy(
                    favoriteIds = _state.value.favoriteIds - hotelId,
                    favorites   = _state.value.favorites.filter { it.hotelId != hotelId }
                )
                favoriteRepository.removeFavorite(userId, hotelId).fold(
                    onSuccess = { Log.d("FavoriteViewModel", "Removed hotel $hotelId") },
                    onFailure = {
                        // Rollback nếu API fail
//                        Log.e("FavoriteViewModel", "Remove failed: ${it.message}")
//                        loadFavorites(userId)
                        Log.e("FavoriteViewModel", "Remove failed: ${it.message}")
                        _state.value = _state.value.copy(
                            favoriteIds = _state.value.favoriteIds + hotelId
                        )
                    }

                )
            } else {
                // Optimistic update — thêm id trước rồi call API
                _state.value = _state.value.copy(
                    favoriteIds = _state.value.favoriteIds + hotelId
                )
                favoriteRepository.addFavorite(userId, hotelId).fold(
                    onSuccess = {
                        Log.d("FavoriteViewModel", "Added hotel $hotelId")
//                        // Reload để lấy full data (hotelName, address, ...)
//                        loadFavorites(userId)

                        // KHÔNG gọi loadFavorites() — tránh trigger LaunchedEffect vòng lặp
                        // FavoriteScreen sẽ load khi user mở tab đó
                    },
                    onFailure = {
                        Log.e("FavoriteViewModel", "Add failed: ${it.message}")
                        _state.value = _state.value.copy(
                            favoriteIds = _state.value.favoriteIds - hotelId
                        )
                    }
                )
            }
        }
    }

    fun isFavorite(hotelId: Int): Boolean =
        _state.value.favoriteIds.contains(hotelId)
}