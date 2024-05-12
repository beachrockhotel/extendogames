package com.example.extendogames.api.models

data class ReservationResponse(
    val reservation_id: Int,
    val date: String,
    val time: String,
    val duration: Int,
    val seat_number: String,
    val user_email: String,
    val user_name: String,
    val message: String
)


data class ReservationRequest(
    val placeNumber: String,
    val date: String,
    val time: String,
    val duration: Int,
    val userEmail: String,
    val userName: String
)


