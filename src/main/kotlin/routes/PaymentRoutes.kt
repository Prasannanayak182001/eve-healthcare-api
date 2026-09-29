package com.evehealthcare.routes

import com.evehealthcare.db.Bookings
import com.evehealthcare.db.Payments
import com.evehealthcare.models.PaymentResponse
import com.evehealthcare.models.SimulatePaymentRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

fun Route.configurePaymentRouting() {
    
    // Simulate Payment (Protected - User initiates payment)
    post("/payments/simulate") {
        val principal = call.principal<JWTPrincipal>()
            ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Not authenticated"))
        
        val request = call.receive<SimulatePaymentRequest>()
        val bookingId = try { UUID.fromString(request.bookingId) } catch (e: Exception) { 
            return@post call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid booking ID")) 
        }

        val booking = transaction {
            Bookings.select { Bookings.id eq bookingId }.firstOrNull()
        }

        if (booking == null) {
            return@post call.respond(HttpStatusCode.NotFound, mapOf("message" to "Booking not found"))
        }

        // Prevent paying for an already confirmed/cancelled booking
        if (booking[Bookings.status] != "PENDING") {
            return@post call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Booking is not in PENDING state"))
        }

        val newStatus = if (request.success) "CONFIRMED" else "FAILED"
        val transactionId = "TXN-${UUID.randomUUID().toString().substring(0, 8)}" // Mock transaction ID

        transaction {
            // Update booking status
            Bookings.update({ Bookings.id eq bookingId }) {
                it[status] = newStatus
            }
            // Create payment record
            Payments.insert {
                it[Payments.bookingId] = bookingId // <-- FIXED THIS LINE
                it[Payments.providerTransactionId] = transactionId
                it[Payments.status] = newStatus
            }
        }

        call.respond(HttpStatusCode.OK, PaymentResponse(
            paymentId = UUID.randomUUID().toString(),
            bookingId = bookingId.toString(),
            transactionId = transactionId,
            status = newStatus
        ))
    }
}