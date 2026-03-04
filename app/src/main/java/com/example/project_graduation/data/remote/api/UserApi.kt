package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class   UserApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getAllUsers(): Result<List<UserDto>> = withContext(Dispatchers.IO) {
        Log.d("UserApi", "Fetching users from API...")
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/users")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("UserApi", "Response: $responseBody")

            if (responseBody != null) {
                val users = parseUsersFromJson(responseBody)
                Log.d("UserApi", "Parsed ${users.size} users")
                Result.success(users)
            } else {
                Log.e("UserApi", "Empty response body")
                Result.failure(Exception("Empty response"))
            }
        } catch (e: Exception) {
            Log.e("UserApi", "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: Int): Result<UserDto> = withContext(Dispatchers.IO) {
        Log.d("UserApi", "Fetching user $userId from API...")
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/users/$userId")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("UserApi", "Response: $responseBody")

            if (responseBody != null) {
                val user = parseUserFromJson(responseBody)
                Result.success(user)
            } else {
                Log.e("UserApi", "Empty response body")
                Result.failure(Exception("Empty response"))
            }
        } catch (e: Exception) {
            Log.e("UserApi", "Error: ${e.message}")
            Result.failure(e)
        }
    }

    private fun parseUsersFromJson(jsonString: String): List<UserDto> {
        val users = mutableListOf<UserDto>()

        try {
            val jsonObject = JSONObject(jsonString)

            // API response structure: { "data": { "users": [...] } }
            if (jsonObject.has("data")) {
                val dataObject = jsonObject.getJSONObject("data")

                if (dataObject.has("users")) {
                    val usersArray = dataObject.getJSONArray("users")

                    for (i in 0 until usersArray.length()) {
                        val userObj = usersArray.getJSONObject(i)
                        users.add(
                            UserDto(
                                userId = userObj.getInt("userId"),
                                username = userObj.getString("username"),
                                email = userObj.getString("email"),
                                phone = userObj.optString("phone", null),
                                createdAt = userObj.getString("createdAt"),
                                role = userObj.getString("role")
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("UserApi", "Error parsing JSON: ${e.message}")
        }

        return users
    }

    private fun parseUserFromJson(jsonString: String): UserDto {
        val jsonObject = JSONObject(jsonString)
        val dataObject = jsonObject.getJSONObject("data")

        return UserDto(
            userId = dataObject.getInt("userId"),
            username = dataObject.getString("username"),
            email = dataObject.getString("email"),
            phone = dataObject.optString("phone", null),
            createdAt = dataObject.getString("createdAt"),
            role = dataObject.getString("role")
        )
    }
}