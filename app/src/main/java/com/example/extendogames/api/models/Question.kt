package com.example.extendogames.api.models

data class Question(
    val id: Int,
    val userEmail: String,
    val text: String,
    val answers: List<Answer>
)
