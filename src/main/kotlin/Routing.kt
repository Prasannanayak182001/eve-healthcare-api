package com.evehealthcare

import com.evehealthcare.db.Bookings
import com.evehealthcare.db.Payments
import com.evehealthcare.models.WebhookRequest
import com.evehealthcare.routes.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("EVE Healthcare API is running! ")
        }
        
        // 1. Public Routes
        configureAuthRouting()
        configurePublicCentreTestRouting()
        
        // Public Webhook (Payment gateways call this without user JWT)
        post("/payments/webhook") {
            val request = call.receive<WebhookRequest>()
            val bookingId = try { UUID.fromString(request.bookingId) } catch (e: Exception) { 
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid booking ID")) 
            }

            transaction {
                // IDEMPOTENCY CHECK: If transaction ID already exists, do nothing and return 200 OK
                val existingPayment = Payments.select { Payments.providerTransactionId eq request.transactionId }.firstOrNull()
                if (existingPayment != null) return@transaction 

                // Verify booking exists
                val booking = Bookings.select { Bookings.id eq bookingId }.firstOrNull()
                if (booking == null) return@transaction

                // Process the webhook
                Payments.insert {
                    it[Payments.bookingId] = bookingId
                    it[Payments.providerTransactionId] = request.transactionId
                    it[Payments.status] = request.status
                }
                
                Bookings.update({ Bookings.id eq bookingId }) {
                    it[status] = request.status
                }
            }
            
            call.respond(HttpStatusCode.OK, mapOf("message" to "Webhook processed successfully"))
        }

        // 2. Protected Routes (Requires JWT)
        authenticate {
            configureProtectedCentreTestRouting()
            configureBookingRouting()
            configurePaymentRouting() // Adds POST /payments/simulate
        }
    }
}