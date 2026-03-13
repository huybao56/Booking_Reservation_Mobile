package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class LockSessionResult(
    val sessionId: String,
    val expiresAt: String,
    val timeoutSeconds: Int
)

class BookingSessionApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // ─── POST /booking-sessions/lock ─────────────────────────────────────────
    suspend fun lockRoom(
        userId: Int,
        roomId: Int,
        checkIn: String,
        checkOut: String
    ): Result<LockSessionResult> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("userId", userId)
                put("roomId", roomId)
                put("checkIn", checkIn)
                put("checkOut", checkOut)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/booking-sessions/lock")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("BookingSessionApi", "Lock response [${response.code}]: $responseBody")
//
//            when (response.code) {
//                201 -> {
//                    val json = JSONObject(responseBody!!)
//                    Result.success(
//                        LockSessionResult(
//                            sessionId = json.getString("sessionId"),
//                            expiresAt = json.getString("expiresAt"),
//                            timeoutSeconds = json.getInt("timeoutSeconds")
//                        )
//                    )
//                }
//
//                409 -> {
//                    // Phòng đang bị giữ bởi user khác
//                    val json = JSONObject(responseBody ?: "{}")
//                    val expiresAt = json.optString("expiresAt", "")
//                    Result.failure(Exception("ROOM_LOCKED:$expiresAt"))
//                }
//
//                else -> Result.failure(Exception("HTTP ${response.code}"))
//            }
            return@withContext when {
                // ✅ 200-299 đều là success
                response.isSuccessful && responseBody != null -> {
                    val json = JSONObject(responseBody)
                    Result.success(
                        LockSessionResult(
                            sessionId = json.getString("sessionId"),
                            expiresAt = json.getString("expiresAt"),
                            timeoutSeconds = json.getInt("timeoutSeconds")
                        )
                    )
                }
                // Phòng đang bị giữ bởi user khác
                response.code == 409 -> {
                    val json = JSONObject(responseBody ?: "{}")
                    val expiresAt = json.optString("expiresAt", "")
                    Result.failure(Exception("ROOM_LOCKED:$expiresAt"))
                }
                // Lỗi khác
                else -> Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("BookingSessionApi", "lockRoom error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ─── PATCH /booking-sessions/{id}/confirm ────────────────────────────────
    suspend fun confirmSession(sessionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/booking-sessions/$sessionId/confirm")
                .patch("".toRequestBody())
                .build()

            val response = client.newCall(request).execute()
            Log.d("BookingSessionApi", "Confirm response [${response.code}]")

            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("HTTP ${response.code}"))
        } catch (e: Exception) {
            Log.e("BookingSessionApi", "confirmSession error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ─── WebSocket URL helper ─────────────────────────────────────────────────
    fun wsUrl(sessionId: String): String =
        "ws://10.0.2.2:8080/booking-sessions/$sessionId/ws"
}