package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.StaffInfoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StaffApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // GET /staff/userId={userId}
    // Gọi khi resume app mà không có staffInfo trong bộ nhớ
    suspend fun getMyStaffInfo(userId: Int): Result<StaffInfoDto> = withContext(Dispatchers.IO) {
        Log.d("StaffApi", "Fetching staff info for userId=$userId")
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/user/$userId")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            Log.d("StaffApi", "Response: $body")

            if (!response.isSuccessful || body == null) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $body"))
            }

            Result.success(parseStaffInfo(body))
        } catch (e: Exception) {
            Log.e("StaffApi", "Error: ${e.message}")
            Result.failure(e)
        }
    }

    private fun parseStaffInfo(jsonString: String): StaffInfoDto {
        val obj = JSONObject(jsonString)
        return StaffInfoDto(
            staffId = obj.getInt("staffId"),
            hotelId = obj.getInt("hotelId"),
            hotelName = obj.getString("hotelName"),
            position = obj.getString("position"),
            canChat = obj.getBoolean("canChat")
        )
    }
}