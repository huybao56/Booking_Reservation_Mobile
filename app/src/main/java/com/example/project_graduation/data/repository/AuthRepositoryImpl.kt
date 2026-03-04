package com.example.project_graduation.data.repository

import com.example.project_graduation.data.mapper.toDomain
import com.example.project_graduation.data.remote.api.AuthApi
import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.repository.AuthRepository
import com.example.project_graduation.data.local.PreferencesManager

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Pair<String, User>> {
        return try {
            val result = authApi.login(email, password)

            result.fold(
                onSuccess = { response ->
                    if (response.token != null && response.user != null) {
                        val user = response.user.toDomain()
//                        save user
                        preferencesManager.saveUser(response.token, user)
                        Result.success(Pair(response.token, user))
//                        Result.success(Pair(response.token, response.user.toDomain()))
                    } else {
                        Result.failure(Exception("Missing token or user data"))
                    }
                },
                onFailure = { exception ->
                    Result.failure(Exception("Invalid password or email"))
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        phone: String?
    ): Result<Pair<String, User>> {
        return try {
            val result = authApi.register(username, email, password, phone)

            result.fold(
                onSuccess = { response ->
                    if (response.token != null && response.user != null) {
                        val user = response.user.toDomain()
//                        save user
                        preferencesManager.saveUser(response.token, user)
                        Result.success(Pair(response.token, user))
//                        Result.success(Pair(response.token, response.user.toDomain()))
                    } else {
                        Result.failure(Exception("Missing token or user data"))
                    }
                },
                onFailure = { exception ->
                    Result.failure(Exception("Email already exists"))
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun saveUser(token: String, user: User) {
        preferencesManager.saveUser(token, user)
    }

    override suspend fun getCurrentUser(): User? {
        return preferencesManager.getUser()
    }

    override suspend fun logout() {
        preferencesManager.clearUserData()
    }

    override suspend fun isLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn()
    }
}