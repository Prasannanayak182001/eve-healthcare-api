package com.evehealthcare.models

import kotlinx.serialization.Serializable

@Serializable
data class SimulatePaymentRequest(
    val bookingId: String,
    val success: Boolean // true = SUCCESS, false = FAILED
)

@Serializable
data class WebhookRequest(
    val transactionId: String,
    val bookingId: String,
    val status: String // "SUCCESS" or "FAILED"
)

@Serializable
data class PaymentResponse(
    val paymentId: String,
    val bookingId: String,
    val transactionId: String,
    val status: String
)