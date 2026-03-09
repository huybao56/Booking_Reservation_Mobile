package com.example.project_graduation.data.remote.api

import android.util.Log
import com.example.project_graduation.data.remote.ApiConfig
import com.example.project_graduation.data.remote.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RoomApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Get available rooms for a hotel
     */
    suspend fun getAvailableRooms(
        hotelId: Int,
        checkIn: String,
        checkOut: String
    ): Result<List<RoomAvailabilityDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId/rooms/availability?checkIn=$checkIn&checkOut=$checkOut")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("RoomApi", "Status Code: ${response.code} Get rooms response: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val roomsArray = org.json.JSONArray(responseBody)
                val roomAvailabilities = mutableListOf<RoomAvailabilityDto>()

                for (i in 0 until roomsArray.length()) {
                    val roomObj = roomsArray.getJSONObject(i)

                    // Backend trả về AvailableRoomResponse: roomId, roomNumber, roomType, floor, basePrice, capacity, status
                    val roomDto = RoomDto(
                        roomId     = roomObj.getInt("roomId"),
                        hotelId    = hotelId,
                        roomNumber = roomObj.optString("roomNumber", ""),
                        roomType   = roomObj.optString("roomType", "Standard Room"),
                        floor      = roomObj.optInt("floor", 1),
                        status     = roomObj.optString("status", "AVAILABLE"),
                        basePrice  = roomObj.optDouble("basePrice", 100.0),
                        capacity   = roomObj.optInt("capacity", 2),
                        description = null,
                        amenities  = getAmenitiesForRoomType(roomObj.optString("roomType", "Standard Room")),
                        images     = emptyList(),
                        primaryImage = null
                    )

                    roomAvailabilities.add(
                        RoomAvailabilityDto(
                            room           = roomDto,
                            availableUnits = 1,
                            pricePerNight  = roomDto.basePrice
                        )
                    )
                }

                Log.d("RoomApi", "Found ${roomAvailabilities.size} rooms available for $checkIn → $checkOut")
                Result.success(roomAvailabilities)


//                val jsonResponse = JSONObject(responseBody)
//
////                if (jsonResponse.getBoolean("success")) {
//                val dataObject = jsonResponse.getJSONObject("data")
//                val roomsArray = dataObject.getJSONArray("rooms")
//
//                // Group rooms by roomType and calculate availability
////                    val roomsByType = mutableMapOf<String, MutableList<RoomDto>>()
//                val roomAvailabilities = mutableListOf<RoomAvailabilityDto>()
//
//                for (i in 0 until roomsArray.length()) {
//                    val roomObj = roomsArray.getJSONObject(i)
//
//                    val roomDto = parseRoomObject(roomObj)
//
////                    val roomDto = RoomDto(
////                        roomId = roomObj.getInt("roomId"),
////                        hotelId = roomObj.getInt("hotelId"),
////                        roomNumber = roomObj.optString("roomNumber", "N/A"),
////                        roomType = roomObj.optString("roomType", "Standard Room"),
////                        floor = roomObj.optInt("floor", 1),
////                        status = roomObj.optString("status", "AVAILABLE"),
////                        basePrice = roomObj.optDouble("basePrice", 100.0),
////                        capacity = roomObj.optInt("capacity", 2),
////                        description = getDescriptionForRoomType(
////                            roomObj.optString(
////                                "roomType",
////                                "Standard Room"
////                            )
////                        ),
////                        amenities = getAmenitiesForRoomType(
////                            roomObj.optString(
////                                "roomType",
////                                "Standard Room"
////                            )
////                        ),
//////                        imageUrl = null
////                    )
//
//                    Log.d(
//                        "RoomApi",
//                        "Room ${roomDto.roomNumber} has ${roomDto.amenities.size} amenities: ${roomDto.amenities}"
//                    )
//
//                    // Chỉ thêm phòng AVAILABLE
//                    if (roomDto.status == "AVAILABLE") {
//                        roomAvailabilities.add(
//                            RoomAvailabilityDto(
//                                room = roomDto,
//                                availableUnits = 1, // Mỗi phòng = 1 unit
//                                pricePerNight = roomDto.basePrice
//                            )
//                        )
//                    }
//
////                        val roomType = roomDto.roomType ?: "Standard Room"
////                        if (!roomsByType.containsKey(roomType)) {
////                            roomsByType[roomType] = mutableListOf()
////                        }
////                        roomsByType[roomType]?.add(roomDto)
//                }
//
//                // Convert to RoomAvailabilityDto - one entry per room type
////                    val roomAvailabilities = roomsByType.map { (_, roomsList) ->
////                        val firstRoom = roomsList.first()
////                        val availableCount = roomsList.count { it.status == "AVAILABLE" }
////
////                        RoomAvailabilityDto(
////                            room = firstRoom, // Use first room as representative
////                            availableUnits = availableCount,
////                            pricePerNight = firstRoom.basePrice ?: 100.0
////                        )
////                    }
//                Log.d("RoomApi", "Found ${roomAvailabilities.size} available rooms available for $checkIn → $checkOut")
//                Result.success(roomAvailabilities)
////                } else {
////                    Result.failure(Exception(jsonResponse.getString("message")))
////                }
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("RoomApi", "Error getting room availability: ${e.message}", e)
            Result.success(getMockRoomAvailability(hotelId))
        }
    }

    /**
     * Get all rooms for a hotel
     */
    suspend fun getRoomsByHotel(hotelId: Int): Result<List<RoomDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId/rooms")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("RoomApi_DEBUG", "Body: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)

//                if (jsonResponse.getBoolean("success")) {
                val dataObject = jsonResponse.getJSONObject("data")
                val roomsArray = dataObject.getJSONArray("rooms")

                for (i in 0 until roomsArray.length()) {
                    val roomObj = roomsArray.getJSONObject(i)
                    Log.d("RoomApi_DEBUG", "--- Room[$i] ---")
                    Log.d("RoomApi_DEBUG", "roomId: ${roomObj.optInt("roomId")}")
                    Log.d("RoomApi_DEBUG", "roomNumber: ${roomObj.optString("roomNumber")}")
                    Log.d("RoomApi_DEBUG", "has 'images' field: ${roomObj.has("images")}")
                    Log.d("RoomApi_DEBUG", "has 'primaryImage' field: ${roomObj.has("primaryImage")}")
                    if (roomObj.has("images")) {
                        Log.d("RoomApi_DEBUG", "images array length: ${roomObj.getJSONArray("images").length()}")
                        Log.d("RoomApi_DEBUG", "images content: ${roomObj.getJSONArray("images")}")
                    }
                    Log.d("RoomApi_DEBUG", "primaryImage: ${roomObj.optString("primaryImage")}")
                }

                val rooms = mutableListOf<RoomDto>()

                for (i in 0 until roomsArray.length()) {
                    val roomObj = roomsArray.getJSONObject(i)

                    rooms.add(parseRoomObject(roomObj))

//                    rooms.add(
//                        RoomDto(
//                            roomId = roomObj.getInt("roomId"),
//                            hotelId = roomObj.getInt("hotelId"),
//                            roomNumber = roomObj.optString("roomNumber", ""),
//                            roomType = roomObj.optString("roomType", "Standard Room"),
//                            floor = roomObj.optInt("floor", 1),
//                            status = roomObj.optString("status", "AVAILABLE"),
//                            basePrice = roomObj.optDouble("basePrice", 100.0),
//                            capacity = roomObj.optInt("capacity", 2),
//                            description = getDescriptionForRoomType(
//                                roomObj.optString(
//                                    "roomType",
//                                    "Standard Room"
//                                )
//                            ),
//                            amenities = getAmenitiesForRoomType(
//                                roomObj.optString(
//                                    "roomType",
//                                    "Standard Room"
//                                )
//                            ),
////                            imageUrl = null
//                        )
//                    )
                }

                Result.success(rooms)
//                } else {
//                    Result.failure(Exception(jsonResponse.getString("message")))
//                }
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("RoomApi", "Error getting rooms: ${e.message}", e)
            Result.success(getMockRooms(hotelId))
        }
    }

    private fun getDescriptionForRoomType(roomType: String): String {
        return when {
            roomType.contains("Standard", ignoreCase = true) ->
                "Comfortable standard room with city view. Perfect for couples or solo travelers."

            roomType.contains("Deluxe", ignoreCase = true) ->
                "Spacious deluxe room with balcony and premium amenities. Ideal for small families."

            roomType.contains("Suite", ignoreCase = true) || roomType.contains(
                "Executive",
                ignoreCase = true
            ) ->
                "Luxury suite with separate living area and stunning views."

            roomType.contains("Family", ignoreCase = true) ->
                "Large family room with multiple beds. Perfect for families."

            roomType.contains("Garden View", ignoreCase = true) ->
                "Peaceful room with beautiful garden views."

            else -> "Comfortable room with modern amenities."
        }
    }

    private fun getAmenitiesForRoomType(roomType: String): List<String> {
        return when {
            roomType.contains("Standard", ignoreCase = true) ->
                listOf("WiFi", "TV", "AC", "Mini Bar", "Safe")

            roomType.contains("Deluxe", ignoreCase = true) ->
                listOf("WiFi", "TV", "AC", "Mini Bar", "Balcony", "Coffee Maker", "Safe")

            roomType.contains("Suite", ignoreCase = true) || roomType.contains(
                "Executive",
                ignoreCase = true
            ) ->
                listOf(
                    "WiFi",
                    "TV",
                    "AC",
                    "Mini Bar",
                    "Balcony",
                    "Jacuzzi",
                    "Kitchen",
                    "Living Room",
                    "Safe"
                )

            roomType.contains("Family", ignoreCase = true) ->
                listOf("WiFi", "TV", "AC", "Kitchenette", "Two Bedrooms", "Safe")

            roomType.contains("Garden View", ignoreCase = true) ->
                listOf("WiFi", "TV", "AC", "Garden View", "Safe")

            else -> listOf("WiFi", "TV", "AC")
        }
    }

    private fun getMockRoomAvailability(hotelId: Int): List<RoomAvailabilityDto> {
        return listOf(
            // Standard Room 101
            RoomAvailabilityDto(
                room = RoomDto(
                    roomId = 1,
                    hotelId = hotelId,
                    roomNumber = "101",
                    roomType = "Standard Room",
                    floor = 1,
                    status = "AVAILABLE",
                    basePrice = 250.0,
                    capacity = 2,
                    description = "Comfortable standard room with city view.",
                    amenities = listOf("WiFi", "TV", "AC", "Mini Bar", "Safe"),
//                    imageUrl = null
                ),
                availableUnits = 1,
                pricePerNight = 250.0
            ),
            // Standard Room 202
            RoomAvailabilityDto(
                room = RoomDto(
                    roomId = 2,
                    hotelId = hotelId,
                    roomNumber = "202",
                    roomType = "Standard Room",
                    floor = 2,
                    status = "AVAILABLE",
                    basePrice = 250.0,
                    capacity = 2,
                    description = "Comfortable standard room with city view.",
                    amenities = listOf("WiFi", "TV", "AC", "Mini Bar", "Safe"),
//                    imageUrl = null
                ),
                availableUnits = 1,
                pricePerNight = 250.0
            ),
            // Standard Room 203
            RoomAvailabilityDto(
                room = RoomDto(
                    roomId = 3,
                    hotelId = hotelId,
                    roomNumber = "203",
                    roomType = "Standard Room",
                    floor = 2,
                    status = "AVAILABLE",
                    basePrice = 250.0,
                    capacity = 2,
                    description = "Comfortable standard room with city view.",
                    amenities = listOf("WiFi", "TV", "AC", "Mini Bar", "Safe"),
//                    imageUrl = null
                ),
                availableUnits = 1,
                pricePerNight = 250.0
            ),
            // Family Room 209
            RoomAvailabilityDto(
                room = RoomDto(
                    roomId = 9,
                    hotelId = hotelId,
                    roomNumber = "209",
                    roomType = "Family Room",
                    floor = 2,
                    status = "AVAILABLE",
                    basePrice = 500.0,
                    capacity = 4,
                    description = "Large family room with multiple beds.",
                    amenities = listOf("WiFi", "TV", "AC", "Kitchenette", "Two Bedrooms"),
//                    imageUrl = null
                ),
                availableUnits = 1,
                pricePerNight = 500.0
            ),
            // Deluxe Room 210
            RoomAvailabilityDto(
                room = RoomDto(
                    roomId = 10,
                    hotelId = hotelId,
                    roomNumber = "210",
                    roomType = "Deluxe Room",
                    floor = 2,
                    status = "AVAILABLE",
                    basePrice = 300.0,
                    capacity = 4,
                    description = "Spacious deluxe room with balcony.",
                    amenities = listOf("WiFi", "TV", "AC", "Mini Bar", "Balcony", "Coffee Maker"),
//                    imageUrl = null
                ),
                availableUnits = 1,
                pricePerNight = 300.0
            )
        )
    }


    private fun getMockRooms(hotelId: Int): List<RoomDto> {
        return getMockRoomAvailability(hotelId).map { it.room }
    }


    /**
     * Get single room by ID
     */
    suspend fun getRoomById(roomId: Int): Result<RoomDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/rooms/$roomId")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("RoomApi", "Get room $roomId - Status: ${response.code}")

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)

                val roomObj = if (jsonResponse.has("data")) {
                    jsonResponse.getJSONObject("data")
                } else {
                    jsonResponse
                }

                println("Loading Rooms: ${jsonResponse}")
                Result.success(parseRoomObject(roomObj))
//                if (jsonResponse.getBoolean("success")) {
//                    val roomObj = jsonResponse.getJSONObject("data")
//
//                    val roomDto = RoomDto(
//                        roomId = roomObj.getInt("roomId"),
//                        hotelId = roomObj.getInt("hotelId"),
//                        roomNumber = roomObj.getString("roomNumber"),
//                        roomType = roomObj.getString("roomType"),
//                        floor = roomObj.optInt("floor", 1),
//                        status = roomObj.optString("status", "AVAILABLE"),
//                        basePrice = roomObj.getDouble("basePrice"),
//                        capacity = roomObj.getInt("capacity"),
//                        description = roomObj.optString("description").takeIf { it != "null" },
//                        amenities = emptyList(), // Parse from JSON if backend returns it
////                        imageUrl = null
//                    )
//
//                    Result.success(roomDto)
//                } else {
//                    Result.failure(Exception(jsonResponse.getString("message")))
//                }
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("RoomApi", "Error getting room: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Create new room
     */
    suspend fun createRoom(
        hotelId: Int,
        roomNumber: String,
        roomType: String?,
        floor: Int,
        basePrice: Double,
        capacity: Int,
        description: String?,
        amenities: List<String>?
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
//                put("hotelId", hotelId)
                put("roomNumber", roomNumber)
                put("roomType", roomType)
                put("floor", floor)
                put("basePrice", basePrice)
                put("capacity", capacity)
//                put("description", description)


                // Backend might not support amenities yet
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

            Log.d("RoomApi", "Creating room with JSON: $jsonBody")

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/hotels/$hotelId/rooms")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("RoomApi", "Create room - Status: ${response.code}, Response: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
//                val roomId = jsonResponse.getJSONObject("data").getInt("roomId")
                val roomId = if (jsonResponse.has("data")) {
                    val dataObj = jsonResponse.getJSONObject("data")
                    dataObj.optInt("roomId", 0)
                } else {
                    // Trường hợp backend trả về roomId trực tiếp
                    jsonResponse.optInt("roomId", 0)
                }
                Result.success(roomId)
            } else {
                Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }
        } catch (e: Exception) {
            Log.e("RoomApi", "Error creating room: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Update existing room
     */
    suspend fun updateRoom(
        roomId: Int,
        roomNumber: String?,
        roomType: String?,
        floor: Int?,
        status: String?,
        basePrice: Double?,
        capacity: Int?,
        description: String?,
        amenities: List<String>?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                roomNumber?.let { put("roomNumber", it) }
                roomType?.let { put("roomType", it) }
                floor?.let { put("floor", it) }
                status?.let { put("status", it) }
                basePrice?.let { put("basePrice", it) }
                capacity?.let { put("capacity", it) }
                description?.let { put("description", it) }
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/rooms/$roomId")
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("RoomApi", "Update room $roomId - Status: ${response.code}")

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }
        } catch (e: Exception) {
            Log.e("RoomApi", "Error updating room: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Delete room
     */
    suspend fun deleteRoom(roomId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/rooms/$roomId")
                .delete()
                .build()

            val response = client.newCall(request).execute()

            Log.d("RoomApi", "Delete room $roomId - Status: ${response.code}")

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val responseBody = response.body?.string()
                Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }
        } catch (e: Exception) {
            Log.e("RoomApi", "Error deleting room: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun parseRoomObject(roomObj: JSONObject): RoomDto {
        // Parse images array
        val images = mutableListOf<ImageDtoResponse>()
        if (roomObj.has("images")) {
            val imagesArray = roomObj.getJSONArray("images")
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

        return RoomDto(
            roomId = roomObj.getInt("roomId"),
            hotelId = roomObj.getInt("hotelId"),
            roomNumber = roomObj.optString("roomNumber", ""),
            roomType = roomObj.optString("roomType", "Standard Room"),
            floor = roomObj.optInt("floor", 1),
            status = roomObj.optString("status", "AVAILABLE"),
            basePrice = roomObj.optDouble("basePrice", 100.0),
            capacity = roomObj.optInt("capacity", 2),
            description = if (roomObj.has("description") && !roomObj.isNull("description"))
                roomObj.getString("description") else null,
            amenities = getAmenitiesForRoomType(roomObj.optString("roomType", "Standard Room")),
            images = images,
            primaryImage = if (roomObj.has("primaryImage") && !roomObj.isNull("primaryImage"))
                roomObj.getString("primaryImage") else null
        )
    }

}