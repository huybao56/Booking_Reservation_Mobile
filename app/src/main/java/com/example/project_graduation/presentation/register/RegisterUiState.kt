package com.example.project_graduation.presentation.register

import com.example.project_graduation.domain.model.User

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val token: String, val user: User) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}