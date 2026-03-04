package com.example.project_graduation.presentation.profile


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.model.User
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

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

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
}
