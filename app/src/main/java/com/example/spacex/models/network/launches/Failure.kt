package com.example.spacex.models.network.launches

data class Failure(
    val altitude: Int,
    val reason: String,
    val time: Int
)