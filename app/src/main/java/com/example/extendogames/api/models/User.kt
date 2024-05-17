package com.example.extendogames.api.models

data class User(
    var name: String = "",
    var email: String = "",
    var userPrivileges: Boolean = false,
    var balance: Double = 0.0
)