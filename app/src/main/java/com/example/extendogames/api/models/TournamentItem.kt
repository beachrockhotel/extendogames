package com.example.extendogames.api.models

data class TournamentItem(
    val id: Int,
    val title: String,
    val location: String,
    val start_time: String,
    val end_time: String,
    val discipline: String,
    val prize_pool: String,
    val image_url: String?,
    val description: String
)

