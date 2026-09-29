package com.evehealthcare.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val testId: String,
    val centreId: String,
    val appointmentTime: String // ISO format: "2026-10-15T10:30:00"
)

@Serializable
data class BookingResponse(
    val id: String,
    val userId: String,
    val testId: String,
    val centreId: String,
    val appointmentTime: String,
    val amount: Double,
    val status: String
)