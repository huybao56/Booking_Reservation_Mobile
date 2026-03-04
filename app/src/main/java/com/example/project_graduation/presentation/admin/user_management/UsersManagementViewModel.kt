package com.example.project_graduation.presentation.admin.user_management

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersManagementViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("UsersManagementVM", "Loading users from API...")

            userRepository.getAllUsers().fold(
                onSuccess = { users ->
                    _users.value = users
                    Log.d("UsersManagementVM", "Loaded ${users.size} users successfully")
                },
                onFailure = { error ->
                    _users.value = emptyList()
                    _error.value = error.message ?: "Failed to load users"
                    Log.e("UsersManagementVM", "Error loading users: ${error.message}")
                }
            )

            _isLoading.value = false
        }
    }

    // TODO: Implement CRUD operations later
    fun createUser(user: User) {
        viewModelScope.launch {
            // TODO: Call API to create user
            Log.d("UsersManagementVM", "Create user: ${user.username}")
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            // TODO: Call API to update user
            Log.d("UsersManagementVM", "Update user: ${user.username}")
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            // TODO: Call API to delete user
            Log.d("UsersManagementVM", "Delete user ID: $userId")
        }
    }
}