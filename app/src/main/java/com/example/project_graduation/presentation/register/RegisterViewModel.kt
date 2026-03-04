package com.example.project_graduation.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        phone: String?
    ) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            val result = registerUseCase(username, email, password, confirmPassword, phone)

            _uiState.value = result.fold(
                onSuccess = { (token, user) ->
                    RegisterUiState.Success(token, user)
                },
                onFailure = { exception ->
                    RegisterUiState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}