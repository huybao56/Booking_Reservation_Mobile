package com.example.project_graduation.data.remote.api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class UploadApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Upload ảnh cho hotel
     * @param hotelId ID của hotel
     * @param imageUri URI của ảnh từ device
     * @param context Android context để đọc file
     * @param isPrimary Đánh dấu ảnh này là ảnh chính
     * @param displayOrder Thứ tự hiển thị
     * @param caption Mô tả ảnh
     * @return Result với imageUrl nếu thành công
     */
    suspend fun uploadHotelImage(
        hotelId: Int,
        imageUri: Uri,
        context: Context,
        isPrimary: Boolean = false,
        displayOrder: Int = 0,
        caption: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        Log.d("UploadApi", "Starting upload for hotel $hotelId")
        try {

            // 1. Convert URI to File
            val file = uriToFile(imageUri, context)
            if (file == null || !file.exists()) {
                return@withContext Result.failure(Exception("Cannot read image file"))
            }

            Log.d("UploadApi", "File prepared: ${file.name}, size: ${file.length()} bytes")

            // 2. Create multipart request body
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "image",
                    file.name,
                    file.asRequestBody("image/*".toMediaType())
                )
                .addFormDataPart("isPrimary", isPrimary.toString())
                .addFormDataPart("displayOrder", displayOrder.toString())

            // Add caption if provided
            if (!caption.isNullOrBlank()) {
                requestBody.addFormDataPart("caption", caption)
            }

            // 3. Create HTTP request
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId/images")
                .post(requestBody.build())
                .build()

            Log.d("UploadApi", "Sending request to: ${ApiConfig.BASE_URL}/hotels/$hotelId/images")

            // 4. Execute request
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("UploadApi", "Response code: ${response.code}")
            Log.d("UploadApi", "Response body: $responseBody")

            // 5. Clean up temp file
            file.delete()

            // 6. Parse response
            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)

                // ✅ SỬA: Backend trả về trực tiếp, KHÔNG có wrapper "data"
                val imageUrl = if (jsonResponse.has("imageUrl")) {
                    // Response structure: { "imageUrl": "...", "imageHotelId": ... }
                    jsonResponse.getString("imageUrl")
                } else if (jsonResponse.has("data")) {
                    // Fallback: nếu có wrapper "data"
                    val dataObject = jsonResponse.getJSONObject("data")
                    dataObject.getString("imageUrl")
                } else {
                    Log.e("UploadApi", "Cannot find imageUrl in response")
                    return@withContext Result.failure(Exception("Invalid response: missing imageUrl"))
                }

                Log.d("UploadApi", "Upload successful! Image URL: $imageUrl")
                Result.success(imageUrl)

            //                // Kiểm tra response structure từ backend
//                if (jsonResponse.has("data")) {
//                    val dataObject = jsonResponse.getJSONObject("data")
//                    val imageUrl = dataObject.getString("imageUrl")
//                    Log.d("UploadApi", "Upload successful! Image URL: $imageUrl")
//                    Result.success(imageUrl)
//                } else {
//                    Log.e("UploadApi", "Unexpected response structure")
//                    Result.failure(Exception("Invalid response structure"))
//                }

            } else {
                val errorMessage = "Upload failed: HTTP ${response.code} - $responseBody"
                Log.e("UploadApi", errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("UploadApi", "Upload error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Upload ảnh cho room
     */
    suspend fun uploadRoomImage(
        roomId: Int,
        imageUri: Uri,
        context: Context,
        isPrimary: Boolean = false,
        displayOrder: Int = 0,
        caption: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        Log.d("UploadApi", "Starting upload for room $roomId")
        try {

            val file = uriToFile(imageUri, context)
            if (file == null || !file.exists()) {
                return@withContext Result.failure(Exception("Cannot read image file"))
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "image",
                    file.name,
                    file.asRequestBody("image/*".toMediaType())
                )
                .addFormDataPart("isPrimary", isPrimary.toString())
                .addFormDataPart("displayOrder", displayOrder.toString())

            if (!caption.isNullOrBlank()) {
                requestBody.addFormDataPart("caption", caption)
            }

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/rooms/$roomId/images")
                .post(requestBody.build())
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            file.delete()

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)

                // Giống như hotel - check cả 2 cách trả về
                val imageUrl = if (jsonResponse.has("imageUrl")) {
                    jsonResponse.getString("imageUrl")
                } else if (jsonResponse.has("data")) {
                    val dataObject = jsonResponse.getJSONObject("data")
                    dataObject.getString("imageUrl")
                } else {
                    Log.e("UploadApi", "Cannot find imageUrl in response")
                    return@withContext Result.failure(Exception("Invalid response: missing imageUrl"))
                }

                Log.d("UploadApi", "Room image uploaded! URL: $imageUrl")
                Result.success(imageUrl)

//                if (jsonResponse.has("data")) {
//                    val dataObject = jsonResponse.getJSONObject("data")
//                    val imageUrl = dataObject.getString("imageUrl")
//                    Log.d("UploadApi", "Upload successful! Image URL: $imageUrl")
//                    Result.success(imageUrl)
//                } else {
//                    Result.failure(Exception("Invalid response structure"))
//                }
            } else {
                Result.failure(Exception("Upload failed: HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("UploadApi", "Upload error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Convert Uri to File
     */
    private fun uriToFile(uri: Uri, context: Context): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")

            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            tempFile
        } catch (e: Exception) {
            Log.e("UploadApi", "Error converting URI to file: ${e.message}")
            null
        }
    }

    /**
     * Delete hotel image
     */
    suspend fun deleteHotelImage(
        hotelId: Int,
        imageHotelId: Int
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId/images/$imageHotelId")
                .delete()
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Delete failed: HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Set primary image
     */
    suspend fun setPrimaryHotelImage(
        hotelId: Int,
        imageHotelId: Int
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId/images/$imageHotelId/primary")
                .put(okhttp3.RequestBody.create(null, ByteArray(0)))
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Set primary failed: HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}