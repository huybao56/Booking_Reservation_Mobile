package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.BookingPaymentDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class BookingPaymentApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getAllPayments(): Result<List<BookingPaymentDto>> = withContext(Dispatchers.IO) {
        Log.d("BookingPaymentApi", "Fetching all payments from API...")
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/booking-payments")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("BookingPaymentApi", "Response: $responseBody")

            if (responseBody != null) {
                val payments = parsePaymentsFromJson(responseBody)
                Log.d("BookingPaymentApi", "Parsed ${payments.size} payments")
                Result.success(payments)
            } else {
                Log.e("BookingPaymentApi", "Empty response body")
                Result.failure(Exception("Empty response"))
            }
        } catch (e: Exception) {
            Log.e("BookingPaymentApi", "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getPaymentsByBookingGroupId(bookingGroupId: String): Result<List<BookingPaymentDto>> =
        withContext(Dispatchers.IO) {
            Log.d("BookingPaymentApi", "Fetching payments for group $bookingGroupId...")
            try {
                val request = Request.Builder()
                    .url("${ApiConfig.BASE_URL}/booking-payments/group-id/$bookingGroupId")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("BookingPaymentApi", "Response: $responseBody")

                if (responseBody != null) {
                    val payments = parsePaymentsFromJson(responseBody)
                    Log.d("BookingPaymentApi", "Parsed ${payments.size} payments for group")
                    Result.success(payments)
                } else {
                    Log.e("BookingPaymentApi", "Empty response body")
                    Result.failure(Exception("Empty response"))
                }
            } catch (e: Exception) {
                Log.e("BookingPaymentApi", "Error: ${e.message}")
                Result.failure(e)
            }
        }

    private fun parsePaymentsFromJson(jsonString: String): List<BookingPaymentDto> {
        val payments = mutableListOf<BookingPaymentDto>()

        try {
            val jsonObject = JSONObject(jsonString)

            // API response structure: { "data": { "payments": [...] } }
            if (jsonObject.has("data")) {
                val dataObject = jsonObject.getJSONObject("data")

                if (dataObject.has("payments")) {
                    val paymentsArray = dataObject.getJSONArray("payments")

                    for (i in 0 until paymentsArray.length()) {
                        val paymentObj = paymentsArray.getJSONObject(i)
                        payments.add(
                            BookingPaymentDto(
                                paymentId = paymentObj.getInt("paymentId"),
                                provider = paymentObj.optString("provider", null),
                                amount = paymentObj.optDouble("amount", 0.0),
                                status = paymentObj.optString("status", null),
                                transactionId = paymentObj.optString("transactionId", null),
                                paidAt = paymentObj.optString("paidAt", null),
                                bookingGroupId = paymentObj.optString("bookingGroupId", null)
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BookingPaymentApi", "Error parsing JSON: ${e.message}")
        }

        return payments
    }
}