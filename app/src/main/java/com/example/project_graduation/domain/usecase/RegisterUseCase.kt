package com.example.project_graduation.domain.usecase

import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        phone: String?
    ): Result<Pair<String, User>> {
        // Validation
        if (username.isBlank()) {
            return Result.failure(Exception("Username cannot be empty"))
        }

        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("Password cannot be empty"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }

        if (password != confirmPassword) {
            return Result.failure(Exception("Passwords do not match"))
        }

        return repository.register(username, email, password, phone)
    }
}