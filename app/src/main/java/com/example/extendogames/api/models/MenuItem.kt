package com.example.extendogames.api.models

data class MenuItem(
    val id: Int,
    val name: String,
    val price: Double,
    val image_url: String
)

data class MenuResponse(
    val menu: List<MenuItem>
)

