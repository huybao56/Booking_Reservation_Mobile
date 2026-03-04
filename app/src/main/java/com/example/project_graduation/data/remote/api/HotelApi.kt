package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.HotelDto
import com.example.project_graduation.data.remote.dto.ImageDtoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class HotelApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getAllHotels(): Result<List<HotelDto>> = withContext(Dispatchers.IO) {
        Log.d("HotelApi", "Fetching hotels from API...")
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("HotelApi", "Response: $responseBody")

//            if (response.isSuccessful) {
//                val jsonResponse = JSONObject(responseBody)
//                val hotelsArray = jsonResponse.getJSONArray("hotels")
//
//                val hotels = (0 until hotelsArray.length()).map { i ->
//                    val hotel = hotelsArray.getJSONObject(i)
//                    HotelDto(
//                        hotelId = hotel.getInt("hotelId"),
//                        hotelName = hotel.getString("hotelName"),
//                        description = hotel.optString("description", null),
//                        rating = hotel.optDouble("rating", 0.0),
//                        hotelAddress = hotel.optString("hotelAddress", null)
//                    )
//                }
//
//                Result.success(hotels)
//            } else {
//                Result.failure(Exception("HTTP ${response.code}"))
//            }
            if (responseBody != null) {
                val hotels = parseHotelsFromJson(responseBody)
                Log.d("HotelApi", "Parsed ${hotels.size} hotels")
                Result.success(hotels)
            } else {
                Log.e("HotelApi", "Empty response body")
                Result.failure(Exception("Empty response"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHotelById(hotelId: Int): Result<HotelDto> = withContext(Dispatchers.IO) {
        Log.d("HotelApi", "Fetching hotel $hotelId from API...")
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (responseBody != null) {
                val hotel = parseHotelFromJson(responseBody)
                Result.success(hotel)
            } else {
                Result.failure(Exception("Empty response"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun createHotel(
//        hotelName: String,
//        description: String?,
//        rating: Double?,
//        hotelAddress: String?
        hotel: HotelDto
    ): Result<Int> = withContext(Dispatchers.IO) {
        Log.d("HotelApi", "Creating hotel: ${hotel.hotelName}")
        try {
            val jsonObject = JSONObject().apply {
                put("hotelName", hotel.hotelName)
                put("description", hotel.description ?: "")
                put("rating", hotel.rating ?: 4.5)
                put("hotelAddress", hotel.hotelAddress ?: "")
            }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("HotelApi", "Create response: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
//                val dataObject = jsonResponse.getJSONObject("data")
                val hotelId = jsonResponse.getInt("hotelId")
                Log.d("HotelApi", "Hotel created successfully with ID: $hotelId")
                Result.success(hotelId)
            } else {
                Log.e("HotelApi", "Failed to create hotel: HTTP ${response.code}")
                Result.failure(Exception("HTTP ${response.code}: ${responseBody}"))
            }
        } catch (e: Exception) {
            Log.e("HotelApi", "Error creating hotel: ${e.message}")
            Result.failure(e)
        }
    }

    // update

    suspend fun updateHotel(
        hotelId: Int,
//        hotelName: String?,
//        description: String?,
//        rating: Double?,
//        hotelAddress: String?
        hotel: HotelDto
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        Log.d("HotelApi", "Updating hotel $hotelId")
        try {
            val jsonObject = JSONObject()
            hotel.hotelName?.let { jsonObject.put("hotelName", it) }
            hotel.description?.let { jsonObject.put("description", it) }
            hotel.rating?.let { jsonObject.put("rating", it) }
            hotel.hotelAddress?.let { jsonObject.put("hotelAddress", it) }

            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId")
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("HotelApi", "Update response: $responseBody")

            if (response.isSuccessful) {
                Log.d("HotelApi", "Hotel updated successfully")
                Result.success(true)
            } else {
                Log.e("HotelApi", "Failed to update hotel: HTTP ${response.code}")
                Result.failure(Exception("HTTP ${response.code}: ${responseBody}"))
            }
        } catch (e: Exception) {
            Log.e("HotelApi", "Error updating hotel: ${e.message}")
            Result.failure(e)
        }
    }

    // delete

    suspend fun deleteHotel(hotelId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        Log.d("HotelApi", "Deleting hotel $hotelId")
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId")
                .delete()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("HotelApi", "Delete response: $responseBody")

            if (response.isSuccessful) {
                Log.d("HotelApi", "Hotel deleted successfully")
                Result.success(true)
            } else {
                Log.e("HotelApi", "Failed to delete hotel: HTTP ${response.code}")
                Result.failure(Exception("HTTP ${response.code}: ${responseBody}"))
            }
        } catch (e: Exception) {
            Log.e("HotelApi", "Error deleting hotel: ${e.message}")
            Result.failure(e)
        }
    }



    private fun parseHotelsFromJson(jsonString: String): List<HotelDto> {
        val hotels = mutableListOf<HotelDto>()

        try {
            val jsonObject = JSONObject(jsonString)

            // Assuming API response structure: { "data": { "hotels": [...] } }
            if (jsonObject.has("data")) {
                val dataObject = jsonObject.getJSONObject("data")

                if (dataObject.has("hotels")) {
                    val hotelsArray = dataObject.getJSONArray("hotels")

                    for (i in 0 until hotelsArray.length()) {
                        val hotelObj = hotelsArray.getJSONObject(i)
                        hotels.add(
                            parseHotelObject(hotelObj)
//                            HotelDto(
//                                hotelId = hotelObj.getInt("hotelId"),
//                                hotelName = hotelObj.getString("hotelName"),
//                                description = hotelObj.optString("description"),
//                                rating = hotelObj.optDouble("rating"),
//                                hotelAddress = hotelObj.optString("hotelAddress")
//                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("HotelApi", "Error parsing JSON: ${e.message}")
        }

        return hotels
    }

    private fun parseHotelFromJson(jsonString: String): HotelDto {
        val jsonObject = JSONObject(jsonString)
        val dataObject = jsonObject.getJSONObject("data")

        return parseHotelObject(dataObject)
//        return HotelDto(
//            hotelId = dataObject.getInt("hotelId"),
//            hotelName = dataObject.getString("hotelName"),
//            description = dataObject.optString("description"),
//            rating = dataObject.optDouble("rating"),
//            hotelAddress = dataObject.optString("hotelAddress")
//        )
    }


    private fun parseHotelObject(hotelObj: JSONObject): HotelDto {
        // Parse images array

        val images = mutableListOf<ImageDtoResponse>()
        if (hotelObj.has("images")) {
            val imagesArray = hotelObj.getJSONArray("images")
            for (j in 0 until imagesArray.length()) {
                val imgObj = imagesArray.getJSONObject(j)
                images.add(
                    ImageDtoResponse(
                        imageId = imgObj.getInt("imageId"),
                        imageUrl = imgObj.getString("imageUrl"),
                        isPrimary = imgObj.optBoolean("isPrimary", false),
                        displayOrder = imgObj.optInt("displayOrder", 0),
                        caption = if (imgObj.has("caption") && !imgObj.isNull("caption"))
                            imgObj.getString("caption") else null
                    )
                )
            }
        }

        return HotelDto(
            hotelId = hotelObj.getInt("hotelId"),
            hotelName = hotelObj.getString("hotelName"),
            description = if (hotelObj.has("description") && !hotelObj.isNull("description"))
                hotelObj.getString("description") else null,
            rating = if (hotelObj.has("rating") && !hotelObj.isNull("rating"))
                hotelObj.getDouble("rating") else null,
            hotelAddress = if (hotelObj.has("hotelAddress") && !hotelObj.isNull("hotelAddress"))
                hotelObj.getString("hotelAddress") else null,
            images = images,
            primaryImage = if (hotelObj.has("primaryImage") && !hotelObj.isNull("primaryImage"))
                hotelObj.getString("primaryImage") else null,
            createdAt = if (hotelObj.has("createdAt") && !hotelObj.isNull("createdAt"))
                hotelObj.getString("createdAt") else null
        )
    }

}
