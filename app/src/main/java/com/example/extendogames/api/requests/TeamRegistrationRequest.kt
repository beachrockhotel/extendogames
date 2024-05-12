package com.example.extendogames.api.requests

import com.google.gson.annotations.SerializedName

data class TeamRegistrationRequest(
    @SerializedName("representative_name")
    val name: String,
    @SerializedName("representative_email")
    val email: String,
    @SerializedName("team_name")
    val teamName: String,
    val members: String,
    @SerializedName("tournament_name")
    val tournamentName: String,
    val discipline: String
)

