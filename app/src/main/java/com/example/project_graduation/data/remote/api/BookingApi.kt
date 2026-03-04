package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.BookingDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit


data class CreateBookingResult(
    val bookingId: Int,
    val bookingGroupId: String
)

class BookingApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()


    /**
     * Get all bookings (Admin)
     */

    suspend fun getAllBookings(): Result<List<BookingDto>> = withContext(Dispatchers.IO) {
        Log.d("BookingApi", "Fetching all bookings from API...")
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/bookings")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("BookingApi", "Response: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val bookings = parseBookingsFromJson(responseBody)
                Log.d("BookingApi", "Parsed ${bookings.size} bookings")
                Result.success(bookings)
            } else {
                Log.e("BookingApi", "Failed to get bookings: HTTP ${response.code}")
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("BookingApi", "Error getting bookings: ${e.message}", e)
            Result.failure(e)
        }
    }


    /**
     * Create booking
     * Backend response: { "message": "Booking created successfully", "bookingId": 123 }
     */

    suspend fun createBooking(
        userId: Int,
        roomId: Int,
        checkIn: String,
        checkOut: String,
        totalPrice: Double,
        bookingGroupId: String? = null
    ): Result<CreateBookingResult> = withContext(Dispatchers.IO) {
        try {
//            val bookingGroupId = UUID.randomUUID().toString()

            val jsonObject = JSONObject().apply {
                put("userId", userId)
                put("roomId", roomId)
                put("status", "CONFIRMED")
                put("checkIn", checkIn)
                put("checkOut", checkOut)
                put("totalPrice", totalPrice)
//                put("bookingGroupId", bookingGroupId)
                if (bookingGroupId != null) {
                    put("bookingGroupId", bookingGroupId)
                }
            }


            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/bookings")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("BookingApi", "Create booking response: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)

                val bookingId = jsonResponse.getInt("bookingId")
                val returnedGroupId = jsonResponse.getString("bookingGroupId")

                Log.d("BookingApi", "Booking created successfully: $bookingId, GroupID=$returnedGroupId")
                Result.success(CreateBookingResult(bookingId, returnedGroupId))
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("BookingApi", "Error creating booking: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Backend response: { "message": "Payment created successfully", "paymentId": 456 }
     * Create payment
     */
    suspend fun createPayment(
        bookingGroupId: String,
        provider: String,
        amount: Double,
        transactionId: String
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val jsonObject = JSONObject().apply {
                put("bookingGroupId", bookingGroupId)
                put("provider", provider)
                put("amount", amount)
                put("status", "PENDING")
                put("transactionId", transactionId)
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/booking-payments")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("BookingApi", "Payment response: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)

//                val paymentId = jsonResponse.getInt("bookingPaymentId")

                val paymentId = try {
                    jsonResponse.getInt("paymentId")
                } catch (e: Exception) {
                    // Nếu backend trả về "bookingPaymentId"
                    jsonResponse.getInt("bookingPaymentId")
                }

                Log.d("BookingApi", "Payment created successfully: $paymentId")

                Result.success(paymentId)
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("BookingApi", "Error creating payment: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get bookings by user ID
     * Backend response: { "success": true, "message": "...", "data": { "bookings": [...] } }
     */
    suspend fun getBookingsByUserId(userId: Int): Result<List<BookingDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${ApiConfig.BASE_URL}/bookings/user/$userId")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("BookingApi", "Get bookings response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    val bookings = parseBookingsFromJson(responseBody)
                    Log.d("BookingApi", "Parsed ${bookings.size} bookings")
                    Result.success(bookings)
                } else {
                    Log.e("BookingApi", "Failed to get bookings: HTTP ${response.code}")
                    Result.failure(Exception("HTTP ${response.code}"))
                }
            } catch (e: Exception) {
                Log.e("BookingApi", "Error getting bookings: ${e.message}", e)
                Result.failure(e)
            }
        }

    /**
     * Get booking by ID
     */
    suspend fun getBookingById(bookingId: Int): Result<BookingDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/bookings/$bookingId")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("BookingApi", "Get booking response: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val booking = parseBookingFromJson(responseBody)
                Result.success(booking)
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("BookingApi", "Error getting booking: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Parse bookings from JSON
     */
    private fun parseBookingsFromJson(jsonString: String): List<BookingDto> {
        val bookings = mutableListOf<BookingDto>()

        try {
            val jsonObject = JSONObject(jsonString)

            // ✅ Parse theo structure: { "data": { "bookings": [...] } }
            if (jsonObject.has("data")) {
                val dataObject = jsonObject.getJSONObject("data")

                if (dataObject.has("bookings")) {
                    val bookingsArray = dataObject.getJSONArray("bookings")

                    for (i in 0 until bookingsArray.length()) {
                        val bookingObj = bookingsArray.getJSONObject(i)

                        bookings.add(
                            BookingDto(
                                bookingId = bookingObj.getInt("bookingId"),
                                userId = bookingObj.getInt("userId"),
                                status = bookingObj.optString("status", "PENDING"),
                                checkIn = bookingObj.optString("checkIn"),
                                checkOut = bookingObj.optString("checkOut"),
                                totalPrice = bookingObj.optDouble("totalPrice", 0.0),
                                createdAt = bookingObj.optString("createdAt"),
                                roomId = bookingObj.getInt("roomId"),
                                bookingGroupId = bookingObj.optString("bookingGroupId", null)
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BookingApi", "Error parsing bookings JSON: ${e.message}", e)
        }

        return bookings
    }

    /**
     * Parse single booking from JSON
     */
    private fun parseBookingFromJson(jsonString: String): BookingDto {
        val jsonObject = JSONObject(jsonString)
        val dataObject = jsonObject.getJSONObject("data")

        return BookingDto(
            bookingId = dataObject.getInt("bookingId"),
            userId = dataObject.getInt("userId"),
            status = dataObject.optString("status", "PENDING"),
            checkIn = dataObject.optString("checkIn"),
            checkOut = dataObject.optString("checkOut"),
            totalPrice = dataObject.optDouble("totalPrice", 0.0),
            createdAt = dataObject.optString("createdAt"),
            roomId = dataObject.getInt("roomId"),
            bookingGroupId = dataObject.optString("bookingGroupId", null)
        )
    }

}


