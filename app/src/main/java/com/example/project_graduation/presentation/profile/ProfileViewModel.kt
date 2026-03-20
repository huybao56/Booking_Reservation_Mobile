package com.example.project_graduation.presentation.profile


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.repository.UserRepository
import com.example.project_graduation.domain.usecase.GetUserProfileUseCase
import com.example.project_graduation.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

//data class EditProfileUiState(
//    val fullName: String = "",
//    val email: String = "",
//    val phone: String = "",
//    val avatarUrl: String? = null,
//    val isLoading: Boolean = false,
//    val isSaving: Boolean = false,
//    val successMessage: String? = null,
//    val errorMessage: String? = null
//)

data class EditProfileUiState(
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,   // pre-fill đang load
    val isSaving: Boolean = false,    // đang gọi API save
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _editState = MutableStateFlow(EditProfileUiState())
    val editState: StateFlow<EditProfileUiState> = _editState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = ProfileState(isLoading = true)

            try {
                val user = getUserProfileUseCase()

                if (user != null) {
                    _state.value = ProfileState(user = user, isLoading = false)
                    Log.d("ProfileViewModel", "Loaded user: ${user.username}")
                } else {
                    _state.value = ProfileState(
                        isLoading = false,
                        error = "No user found. Please login again."
                    )
                    Log.d("ProfileViewModel", "No user data found")
                }
            } catch (e: Exception) {
                _state.value = ProfileState(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
                Log.d("ProfileViewModel", "Error loading profile: ${e.message}")
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("ContentValues", "Logging out user: ${_state.value.user?.username}")

                logoutUseCase()
                Log.d("ProfileViewModel", "Logout successful")

                // Clear local state
                _state.value = ProfileState()

                onLogoutComplete()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Logout error: ${e.message}")
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    // ── Gọi khi mở EditProfileScreen — pre-fill form từ user hiện tại ────────
    fun initEditProfile() {
        val user = _state.value.user ?: return
        _editState.value = EditProfileUiState(
            username = user.username,
            email = user.email,
            phone = user.phone ?: ""
        )
    }

    // ── Gọi khi nhấn Save Changes ────────────────────────────────────────────
    fun updateProfile(
        username: String,
        email: String,
        phone: String?,
        currentPassword: String? = null,
        newPassword: String? = null
    ) {
        val currentUser = _state.value.user
        if (currentUser == null) {
            _editState.value = _editState.value.copy(errorMessage = "User not found. Please login again.")
            return
        }

        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true, errorMessage = null, successMessage = null)

            val result = userRepository.updateUser(
                userId = currentUser.userId,
                token = currentUser.token,
                username = username,
                email = email,
                phone = phone?.takeIf { it.isNotBlank() },
                currentPassword = currentPassword?.takeIf { it.isNotBlank() },
                newPassword = newPassword?.takeIf { it.isNotBlank() }
            )

            result.fold(
                onSuccess = { updatedUser ->
                    // Cập nhật lại ProfileState để màn hình Profile cũng mới
                    _state.value = ProfileState(user = updatedUser)
                    _editState.value = EditProfileUiState(
                        username = updatedUser.username,
                        email = updatedUser.email,
                        phone = updatedUser.phone ?: "",
                        isSaving = false,
                        successMessage = "Profile updated successfully!"
                    )
                    Log.d("ProfileViewModel", "Profile updated: ${updatedUser.username}")
                },
                onFailure = { error ->
                    _editState.value = _editState.value.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Update failed. Please try again."
                    )
                    Log.e("ProfileViewModel", "Update failed: ${error.message}")
                }
            )
        }
    }

    // ── Clear messages sau khi Snackbar hiển thị xong ────────────────────────
    fun clearEditMessages() {
        _editState.value = _editState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }

}
