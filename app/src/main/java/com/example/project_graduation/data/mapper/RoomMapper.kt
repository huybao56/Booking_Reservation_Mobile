package com.example.project_graduation.data.mapper

import com.example.project_graduation.data.remote.dto.RoomDto
import com.example.project_graduation.domain.model.Room
import kotlin.Int

fun RoomDto.toDomain(): Room {
    return Room(
        roomId = roomId,
        hotelId = hotelId,
        roomNumber = roomNumber,
        roomType = roomType,
        floor = floor,
        status = status,
        basePrice = basePrice,
        capacity = capacity,
        description = description,
        amenities = amenities,
        images = emptyList(),
        primaryImage = null

//        imageUrl = imageUrl
    )
}