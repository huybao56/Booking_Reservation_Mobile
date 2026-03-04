package com.example.project_graduation.domain.usecase

import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.repository.AuthRepository

class GetUserProfileUseCase(
    private val authRepository: AuthRepository
) {
//    get user from local storage
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}