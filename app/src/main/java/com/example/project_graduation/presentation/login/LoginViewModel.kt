package com.example.project_graduation.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_graduation.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            val result = loginUseCase(email, password)

            _uiState.value = result.fold(
                onSuccess = { (token, user) ->
                    LoginUiState.Success(token, user)
                },
                onFailure = { exception ->
                    LoginUiState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}