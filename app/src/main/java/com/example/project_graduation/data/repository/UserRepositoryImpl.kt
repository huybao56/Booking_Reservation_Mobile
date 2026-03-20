package com.example.project_graduation.data.repository

import android.util.Log
import com.example.project_graduation.data.local.PreferencesManager
import com.example.project_graduation.data.remote.api.UserApi
import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.model.UserRole
import com.example.project_graduation.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userApi: UserApi,
    private val preferencesManager: PreferencesManager
) : UserRepository {

    override suspend fun getAllUsers(): Result<List<User>> {
        return userApi.getAllUsers().map { dtoList ->
            dtoList.map { dto ->
                User(
                    userId = dto.userId,
                    username = dto.username,
                    email = dto.email,
                    phone = dto.phone,
                    createdAt = dto.createdAt,
                    role = UserRole.fromString(dto.role),
                    token = ""
                )
            }
        }
    }

    override suspend fun getUserById(userId: Int): Result<User> {
        return userApi.getUserById(userId).map { dto ->
            User(
                userId = dto.userId,
                username = dto.username,
                email = dto.email,
                phone = dto.phone,
                createdAt = dto.createdAt,
                role = UserRole.fromString(dto.role),
                token = ""
            )
        }
    }
    // ─── UPDATE PROFILE ─────────────────────────────────────────────────────────
    // 1. Gọi API PUT /users/{userId} để cập nhật database
    // 2. Nếu thành công → lưu lại vào PreferencesManager (DataStore local)
    override suspend fun updateUser(
        userId: Int,
        token: String,
        username: String,
        email: String,
        phone: String?,
        currentPassword: String?,
        newPassword: String?
    ): Result<User> {
        // Lấy user hiện tại để giữ lại createdAt và role (backend không trả về)
        val currentUser = preferencesManager.getUser()

        return userApi.updateUser(
            userId = userId,
            token = token,
            username = username,
            email = email,
            phone = phone,
            currentPassword = currentPassword,
            newPassword = newPassword
        ).map { dto ->
            // Merge theo thứ tự ưu tiên:
            // 1. field được gửi lên (không null/blank) → đây là giá trị mới
            // 2. dto trả về từ backend nếu có
            // 3. fallback về currentUser local (không thay đổi)
            val updatedUser = User(
                userId    = dto.userId.takeIf { it > 0 } ?: (currentUser?.userId ?: userId),
                username  = username?.takeIf { it.isNotBlank() }
                    ?: dto.username.takeIf { it.isNotBlank() }
                    ?: currentUser?.username ?: "",
                email     = email?.takeIf { it.isNotBlank() }
                    ?: dto.email.takeIf { it.isNotBlank() }
                    ?: currentUser?.email ?: "",
                phone     = phone?.takeIf { it.isNotBlank() }
                    ?: currentUser?.phone,
                createdAt = currentUser?.createdAt ?: "",
                role      = currentUser?.role ?: UserRole.USER,
                token     = token
            )
            // Lưu vào PreferencesManager — đồng bộ FE với DB
            preferencesManager.saveUser(token, updatedUser)
            Log.d("UserRepositoryImpl", "Saved to prefs: username=${updatedUser.username} email=${updatedUser.email} phone=${updatedUser.phone}")
            updatedUser
        }
    }
}