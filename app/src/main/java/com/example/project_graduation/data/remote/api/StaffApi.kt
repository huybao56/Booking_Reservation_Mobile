package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.StaffBookingDto
import com.example.project_graduation.data.remote.dto.StaffConversationDto
import com.example.project_graduation.data.remote.dto.StaffDashboardStatsDto
import com.example.project_graduation.data.remote.dto.StaffInfoDto
import com.example.project_graduation.data.remote.dto.StaffMessageDto
import com.example.project_graduation.data.remote.dto.StaffRoomDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StaffApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()


    private val json = "application/json".toMediaType()

    //  BOOKINGS

    // GET /staff/bookings/hotel/{hotelId}
    suspend fun getBookingsByHotel(hotelId: Int): Result<List<StaffBookingDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/bookings/hotel/$hotelId")
                .get().build()
            val body = client.newCall(request).execute().use { it.body?.string() }
            val arr  = JSONObject(body!!).getJSONArray("bookings")
            Result.success(parseBookingList(arr))
        } catch (e: Exception) {
            Log.e("StaffApi", "getBookingsByHotel: ${e.message}")
            Result.failure(e)
        }
    }


    // PATCH /staff/bookings/{bookingId}/status
    suspend fun updateBookingStatus(bookingId: Int, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().put("status", status).toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/bookings/$bookingId/status")
                .patch(body).build()
            client.newCall(request).execute().use { resp ->
                if (resp.isSuccessful) Result.success(Unit)
                else Result.failure(Exception("HTTP ${resp.code}"))
            }
        } catch (e: Exception) {
            Log.e("StaffApi", "updateBookingStatus: ${e.message}")
            Result.failure(e)
        }
    }

    //  ROOMS


    // GET /staff/rooms/hotel/{hotelId}
    suspend fun getRoomsByHotel(hotelId: Int): Result<List<StaffRoomDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/rooms/hotel/$hotelId")
                .get().build()
            val body = client.newCall(request).execute().use { it.body?.string() }
            val arr  = JSONObject(body!!).getJSONArray("rooms")
            Result.success(parseRoomList(arr))
        } catch (e: Exception) {
            Log.e("StaffApi", "getRoomsByHotel: ${e.message}")
            Result.failure(e)
        }
    }

    // PATCH /staff/rooms/{roomId}/status
    suspend fun updateRoomStatus(roomId: Int, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().put("status", status).toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/rooms/$roomId/status")
                .patch(body).build()
            client.newCall(request).execute().use { resp ->
                if (resp.isSuccessful) Result.success(Unit)
                else Result.failure(Exception("HTTP ${resp.code}"))
            }
        } catch (e: Exception) {
            Log.e("StaffApi", "updateRoomStatus: ${e.message}")
            Result.failure(e)
        }
    }


    //  DASHBOARD

    // GET /staff/dashboard/hotel/{hotelId}
    suspend fun getDashboardStats(hotelId: Int): Result<StaffDashboardStatsDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/dashboard/hotel/$hotelId")
                .get().build()
            val body = client.newCall(request).execute().use { it.body?.string() }
            val obj  = JSONObject(body!!)
            Result.success(
                StaffDashboardStatsDto(
                    todayCheckIns    = obj.getInt("todayCheckIns"),
                    todayCheckOuts   = obj.getInt("todayCheckOuts"),
                    availableRooms   = obj.getInt("availableRooms"),
                    occupiedRooms    = obj.getInt("occupiedRooms"),
                    maintenanceRooms = obj.getInt("maintenanceRooms"),
                    pendingBookings  = obj.getInt("pendingBookings"),
                    totalRevenue     = obj.getDouble("totalRevenue")
                )
            )
        } catch (e: Exception) {
            Log.e("StaffApi", "getDashboardStats: ${e.message}")
            Result.failure(e)
        }
    }



    //  CHAT / CONVERSATIONS

    // GET /staff/conversations/hotel/{hotelId}
    suspend fun getConversations(hotelId: Int): Result<List<StaffConversationDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/conversations/hotel/$hotelId")
                .get().build()
            val body = client.newCall(request).execute().use { it.body?.string() }
            val arr  = JSONArray(body!!)
            Result.success(parseConversationList(arr))
        } catch (e: Exception) {
            Log.e("StaffApi", "getConversations: ${e.message}")
            Result.failure(e)
        }
    }

    // GET /staff/conversations/{conversationId}/messages
    suspend fun getMessages(conversationId: Int): Result<List<StaffMessageDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/conversations/$conversationId/messages")
                .get().build()
            val body = client.newCall(request).execute().use { it.body?.string() }
            val arr  = JSONArray(body!!)
            Result.success(parseMessageList(arr))
        } catch (e: Exception) {
            Log.e("StaffApi", "getMessages: ${e.message}")
            Result.failure(e)
        }
    }

    // POST /staff/conversations/{conversationId}/messages
    suspend fun sendMessage(conversationId: Int, staffUserId: Int, text: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject()
                .put("staffUserId", staffUserId)
                .put("messageText", text)
                .toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/conversations/$conversationId/messages")
                .post(payload).build()
            val body = client.newCall(request).execute().use { it.body?.string() }
            val msgId = JSONObject(body!!).getInt("messageId")
            Result.success(msgId)
        } catch (e: Exception) {
            Log.e("StaffApi", "sendMessage: ${e.message}")
            Result.failure(e)
        }
    }

    // PATCH /staff/conversations/{conversationId}/read
    suspend fun markAsRead(conversationId: Int, staffUserId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().put("staffUserId", staffUserId).toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/staff/conversations/$conversationId/read")
                .patch(body).build()
            client.newCall(request).execute().use { resp ->
                if (resp.isSuccessful) Result.success(Unit)
                else Result.failure(Exception("HTTP ${resp.code}"))
            }
        } catch (e: Exception) {
            Log.e("StaffApi", "markAsRead: ${e.message}")
            Result.failure(e)
        }
    }


    //  JSON PARSERS

    private fun parseBookingList(arr: JSONArray): List<StaffBookingDto> =
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            StaffBookingDto(
                bookingId       = o.getInt("bookingId"),
                bookingGroupId  = o.getString("bookingGroupId"),
                guestName       = o.getString("guestName"),
                guestPhone      = o.optString("guestPhone", null),
                roomId          = o.getInt("roomId"),
                roomNumber      = o.getString("roomNumber"),
                roomType        = o.getString("roomType"),
                floor           = o.optInt("floor", 1),
                checkIn         = o.getString("checkIn"),
                checkOut        = o.getString("checkOut"),
                status          = o.getString("status"),
                totalAmount     = o.getDouble("totalAmount"),
                guests          = o.optInt("guests", 1),
                specialRequests = o.optString("specialRequests", null)
            )
        }

    private fun parseRoomList(arr: JSONArray): List<StaffRoomDto> =
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            StaffRoomDto(
                roomId       = o.getInt("roomId"),
                roomNumber   = o.getString("roomNumber"),
                roomType     = o.getString("roomType"),
                floor        = o.optInt("floor", 1),
                status       = o.getString("status"),
                basePrice    = o.getDouble("basePrice"),
                capacity     = o.optInt("capacity", 2),
                currentGuest = o.optString("currentGuest", null),
                checkOutDate = o.optString("checkOutDate", null)
            )
        }

    private fun parseConversationList(arr: JSONArray): List<StaffConversationDto> =
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            StaffConversationDto(
                conversationId  = o.getInt("conversationId"),
                userId          = o.getInt("userId"),
                guestName       = o.getString("guestName"),
                guestPhone      = o.optString("guestPhone", null),
                staffUserId     = o.getInt("staffUserId"),
                lastMessage     = o.optString("lastMessage", null),
                lastMessageTime = o.optString("lastMessageTime", null),
                unreadCount     = o.optInt("unreadCount", 0)
            )
        }

    private fun parseMessageList(arr: JSONArray): List<StaffMessageDto> =
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            StaffMessageDto(
                messageId    = o.getInt("messageId"),
                senderId     = o.getInt("senderId"),
                senderName   = o.getString("senderName"),
                senderRole   = o.getString("senderRole"),
                messageText  = o.getString("messageText"),
                isRead       = o.getBoolean("isRead"),
                createdAt    = o.getString("createdAt")
            )
        }



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