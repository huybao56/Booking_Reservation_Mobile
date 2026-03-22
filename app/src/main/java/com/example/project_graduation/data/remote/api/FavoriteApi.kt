package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.FavoriteDto
import com.example.project_graduation.data.remote.dto.FavoriteActionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class FavoriteApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = "application/json".toMediaType()

    // GET /favorites?userId={userId}
    suspend fun getFavorites(userId: Int): Result<List<FavoriteDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/favorites?userId=$userId")
                .get().build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Log.d("FavoriteApi", "getFavorites [${response.code}]: $body")

            if (response.isSuccessful && body != null) {
                val arr = JSONObject(body).getJSONArray("favorites")
                Result.success(parseFavoriteList(arr))
            } else {
                Result.failure(Exception("Failed to load favorites (${response.code})"))
            }
        } catch (e: Exception) {
            Log.e("FavoriteApi", "getFavorites error: ${e.message}")
            Result.failure(e)
        }
    }

    // POST /favorites  { userId, hotelId }
    suspend fun addFavorite(userId: Int, hotelId: Int): Result<FavoriteActionDto> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject()
                .put("userId", userId)
                .put("hotelId", hotelId)
                .toString().toRequestBody(json)

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/favorites")
                .post(payload).build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Log.d("FavoriteApi", "addFavorite [${response.code}]: $body")

            if (response.isSuccessful && body != null) {
                Result.success(parseFavoriteAction(body))
            } else {
                Result.failure(Exception("Failed to add favorite (${response.code})"))
            }
        } catch (e: Exception) {
            Log.e("FavoriteApi", "addFavorite error: ${e.message}")
            Result.failure(e)
        }
    }

    // DELETE /favorites/{hotelId}?userId={userId}
    suspend fun removeFavorite(userId: Int, hotelId: Int): Result<FavoriteActionDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/favorites/$hotelId?userId=$userId")
                .delete().build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Log.d("FavoriteApi", "removeFavorite [${response.code}]: $body")

            if (response.isSuccessful && body != null) {
                Result.success(parseFavoriteAction(body))
            } else {
                Result.failure(Exception("Failed to remove favorite (${response.code})"))
            }
        } catch (e: Exception) {
            Log.e("FavoriteApi", "removeFavorite error: ${e.message}")
            Result.failure(e)
        }
    }

    // GET /favorites/ids?userId={userId}
    suspend fun getFavoriteIds(userId: Int): Result<List<Int>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/favorites/ids?userId=$userId")
                .get().build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                val arr = JSONObject(body).getJSONArray("hotelIds")
                val ids = (0 until arr.length()).map { arr.getInt(it) }
                Result.success(ids)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Log.e("FavoriteApi", "getFavoriteIds error: ${e.message}")
            Result.success(emptyList())
        }
    }

    private fun parseFavoriteList(arr: JSONArray): List<FavoriteDto> =
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            FavoriteDto(
                favoriteId   = o.getInt("favoriteId"),
                userId       = o.getInt("userId"),
                hotelId      = o.getInt("hotelId"),
                hotelName    = o.getString("hotelName"),
                hotelAddress = o.optString("hotelAddress").takeIf { it.isNotEmpty() && it != "null" },
                rating       = if (o.isNull("rating")) null else o.getDouble("rating"),
                createdAt    = o.getString("createdAt")
            )
        }

    private fun parseFavoriteAction(body: String): FavoriteActionDto {
        val o = JSONObject(body)
        return FavoriteActionDto(
            message    = o.getString("message"),
            favoriteId = if (o.isNull("favoriteId")) null else o.getInt("favoriteId"),
            isFavorite = o.getBoolean("isFavorite")
        )
    }
}