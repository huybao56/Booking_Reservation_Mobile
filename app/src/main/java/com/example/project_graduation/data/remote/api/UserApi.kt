package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.StaffConversationDto
import com.example.project_graduation.data.remote.dto.StaffMessageDto
import com.example.project_graduation.data.remote.dto.UserConversationDto
import com.example.project_graduation.data.remote.dto.UserDto
import com.example.project_graduation.data.remote.dto.UserMessageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class   UserApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = "application/json".toMediaType()

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

    // CHAT
// GET /user/conversations?userId={userId}
    suspend fun getMyConversations(userId: Int): Result<List<UserConversationDto>> =
        withContext(Dispatchers.IO) {
            Log.d("UserApi", "▶ getMyConversations userId=$userId")
            try {
                val request = Request.Builder()
                    .url("${ApiConfig.BASE_URL}/user/conversations?userId=$userId")
                    .get().build()
                val body = client.newCall(request).execute().use { it.body?.string() }
                Log.d("UserApi", "  ◀ body=$body")
                val arr = JSONArray(body!!)
                Result.success(parseConversationList(arr))
            } catch (e: Exception) {
                Log.e("UserApi", "getMyConversations: ${e.message}")
                Result.failure(e)
            }
        }

    // POST /user/conversations  { userId, hotelId }  → conversationId
    suspend fun createOrGetConversation(
        userId: Int,
        hotelId: Int
    ): Result<UserConversationDto> = withContext(Dispatchers.IO) {
        Log.d("UserApi", "▶ createOrGetConversation userId=$userId hotelId=$hotelId")
        try {
            val payload = JSONObject()
                .put("userId", userId)
                .put("hotelId", hotelId)
                .toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/user/conversations")
                .post(payload).build()
            val body = client.newCall(request).execute().use { it.body?.string() }
            Log.d("UserApi", "  ◀ body=$body")
            val conv = parseConversationObj(JSONObject(body!!))
            Log.d("UserApi", "  conversationId=${conv.conversationId}")
            Result.success(conv)
        } catch (e: Exception) {
            Log.e("UserApi", "createOrGetConversation: ${e.message}")
            Result.failure(e)
        }
    }

    // GET /user/conversations/{conversationId}/messages
    suspend fun getMessages(conversationId: Int): Result<List<UserMessageDto>> =
        withContext(Dispatchers.IO) {
            Log.d("UserApi", "▶ getMessages conversationId=$conversationId")
            try {
                val request = Request.Builder()
                    .url("${ApiConfig.BASE_URL}/user/conversations/$conversationId/messages")
                    .get().build()
                val body = client.newCall(request).execute().use { it.body?.string() }
                Log.d("UserApi", "  ◀ body=$body")
                val arr = JSONArray(body!!)
                Result.success(parseMessageList(arr))
            } catch (e: Exception) {
                Log.e("UserApi", "getMessages: ${e.message}")
                Result.failure(e)
            }
        }


    // POST /user/conversations/{conversationId}/messages  { userId, messageText }
    suspend fun sendMessage(
        conversationId: Int,
        userId: Int,
        text: String
    ): Result<Int> = withContext(Dispatchers.IO) {
        Log.d("UserApi", "▶ sendMessage convId=$conversationId userId=$userId text=$text")
        try {
            val payload = JSONObject()
                .put("userId", userId)
                .put("messageText", text)
                .toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/user/conversations/$conversationId/messages")
                .post(payload).build()
            val body = client.newCall(request).execute().use { it.body?.string() }
            Log.d("UserApi", "  ◀ body=$body")
            val msgId = JSONObject(body!!).getInt("messageId")
            Log.d("UserApi", "  messageId=$msgId")
            Result.success(msgId)
        } catch (e: Exception) {
            Log.e("UserApi", "sendMessage: ${e.message}")
            Result.failure(e)
        }
    }

    // PATCH /user/conversations/{conversationId}/read  { userId }
    suspend fun markAsRead(conversationId: Int, userId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            Log.d("UserApi", "▶ markAsRead convId=$conversationId userId=$userId")
            try {
                val payload = JSONObject().put("userId", userId)
                    .toString().toRequestBody(json)
                val request = Request.Builder()
                    .url("${ApiConfig.BASE_URL}/user/conversations/$conversationId/read")
                    .patch(payload).build()
                client.newCall(request).execute().use { resp ->
                    if (resp.isSuccessful) Result.success(Unit)
                    else Result.failure(Exception("HTTP ${resp.code}"))
                }
            } catch (e: Exception) {
                Log.e("UserApi", "markAsRead: ${e.message}")
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

    private fun parseConversationList(arr: JSONArray): List<UserConversationDto> =
        (0 until arr.length()).map { i -> parseConversationObj(arr.getJSONObject(i)) }

    private fun parseConversationObj(o: JSONObject) = UserConversationDto(
        conversationId = o.getInt("conversationId"),
        userId = o.getInt("userId"),
        hotelId = o.optInt("hotelId", 0),
        hotelName = o.optString("hotelName", "Khách sạn"),
        staffUserId = o.optInt("staffUserId", 0),
        lastMessage = o.optString("lastMessage").takeIf { it.isNotEmpty() && it != "null" },
        lastMessageTime = o.optString("lastMessageTime").takeIf { it.isNotEmpty() && it != "null" },
        unreadCount = o.optInt("unreadCount", 0),
        isOnline = o.optBoolean("isOnline", false)
    )

    private fun parseMessageList(arr: JSONArray): List<UserMessageDto> =
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            UserMessageDto(
                messageId   = o.getInt("messageId"),
                senderId    = o.getInt("senderId"),
                senderName  = o.getString("senderName"),
                senderRole  = o.optString("senderRole", "USER"),
                messageText = o.getString("messageText"),
                isRead      = o.getBoolean("isRead"),
                createdAt   = o.getString("createdAt")
            )
        }
}