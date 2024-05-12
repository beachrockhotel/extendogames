package com.example.extendogames.api.models

data class Order(
    val user_name: String,
    val user_email: String,
    val table_number: String,
    val items: List<OrderItem>,
    val total_price: Double,
    val order_date: String
)