package com.evehealthcare.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateCentreRequest(
    val name: String,
    val location: String
)

@Serializable
data class CentreResponse(
    val id: String, // Changed to String for JSON serialization
    val name: String,
    val location: String
)

@Serializable
data class CreateTestRequest(
    val centreId: String, // Changed to String
    val name: String,
    val price: Double
)

@Serializable
data class TestResponse(
    val id: String, // Changed to String
    val centreId: String, // Changed to String
    val name: String,
    val price: Double
)