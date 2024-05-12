package com.example.extendogames.api.requests

data class CheckAvailabilityRequest(
    val seatNumber: Int,
    val date: String,
    val startTime: String,
    val duration: Int
)
