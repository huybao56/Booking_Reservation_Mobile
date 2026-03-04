package com.example.project_graduation.presentation.login

import com.example.project_graduation.domain.model.User

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val token: String, val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}