package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.LoginRequest
import com.example.project_graduation.data.remote.dto.LoginResponse
import com.example.project_graduation.data.remote.dto.RegisterRequest
import com.example.project_graduation.data.remote.dto.RegisterResponse
import com.example.project_graduation.data.remote.dto.StaffInfoDto
import com.example.project_graduation.data.remote.dto.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AuthApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun login(email: String, password: String): Result<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("AuthApi", "login() called — email=$email")
//                build json
                val json = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }

//                save json as string
                val requestBody = json.toString()
                    .toRequestBody("application/json".toMediaType())

//                post request api
                val request = Request.Builder()
                    .url("${ApiConfig.BASE_URL}/auth/login")
                    .post(requestBody)
                    .build()

//                response request
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""


                Log.d("AuthApi", "HTTP ${response.code}")
                Log.d("AuthApi", "Body: $responseBody")

//                status http 200-299
                if (response.isSuccessful) {

                    val jsonResponse = JSONObject(responseBody)

                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

//                    if having success
                    if (success) {
                        val token = jsonResponse.optString("token", null)
                        val userJson = jsonResponse.optJSONObject("user")

                        val userDto = userJson?.let {
                            UserDto(
                                userId = it.getInt("userId"),
                                username = it.getString("username"),
                                email = it.getString("email"),
                                phone = it.optString("phone", null),
                                createdAt = it.getString("createdAt"),
                                role = it.getString("role")
                            )
                        }

                        Log.d("AuthApi", "user: userId=${userDto?.userId}  role=${userDto?.role}")

// ── Parse staffInfo (chỉ có khi role=STAFF) ─────────────
                        val staffJson = jsonResponse.optJSONObject("staffInfo")
                        val staffInfoDto = staffJson?.let {
                            StaffInfoDto(
                                staffId = it.getInt("staffId"),
                                hotelId = it.getInt("hotelId"),
                                hotelName = it.getString("hotelName"),
                                position = it.getString("position"),
                                canChat = it.getBoolean("canChat")
                            )
                        }
                        Log.d(
                            "AuthApi",
                            "  staffInfo: ${if (staffInfoDto != null) "staffId=${staffInfoDto.staffId}  hotel=${staffInfoDto.hotelName}" else "null (not STAFF)"}"
                        )


//                        build json result
                        Result.success(
                            LoginResponse(
                                success = true,
                                message = message,
                                token = token,
                                user = userDto,
                                staffInfo = staffInfoDto
                            )
                        )
                    } else {
                        Result.failure(Exception(message))
                    }
                } else {
                    Result.failure(Exception("HTTP ${response.code}: ${responseBody}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("${e.message}"))
            }
        }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        phone: String? = null
    ): Result<RegisterResponse> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("username", username)
                put("email", email)
                put("password", password)
                phone?.let { put("phone", it) }
            }

            val requestBody = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/auth/register")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonResponse = JSONObject(responseBody)
                val success = jsonResponse.getBoolean("success")
                val message = jsonResponse.getString("message")

                if (success) {
                    val token = jsonResponse.optString("token", null)
                    val userJson = jsonResponse.optJSONObject("user")

                    val userDto = userJson?.let {
                        UserDto(
                            userId = it.getInt("userId"),
                            username = it.getString("username"),
                            email = it.getString("email"),
                            phone = it.optString("phone", null),
                            createdAt = it.getString("createdAt"),
                            role = it.getString("role")
                        )
                    }

                    Result.success(
                        RegisterResponse(
                            success = true,
                            message = message,
                            token = token,
                            user = userDto
                        )
                    )
                } else {
                    Result.failure(Exception(message))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${responseBody}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}