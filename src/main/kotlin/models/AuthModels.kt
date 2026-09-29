package com.evehealthcare.models

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val userId: String, // Changed from UUID to String to fix serialization
    val email: String,
    val token: String
)

@Serializable
data class ErrorResponse(
    val message: String
)