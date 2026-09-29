package com.evehealthcare.routes

import com.evehealthcare.db.Bookings
import com.evehealthcare.db.Centres
import com.evehealthcare.db.DiagnosticTests
import com.evehealthcare.models.BookingResponse
import com.evehealthcare.models.CreateBookingRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

fun Route.configureBookingRouting() {
    
    // POST /bookings - Create a new booking (Protected)
    post("/bookings") {
        val principal = call.principal<JWTPrincipal>()
            ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Not authenticated"))
        
        val userId = principal.payload.getClaim("userId").asString()
        val request = call.receive<CreateBookingRequest>()
        
        // Validate appointment time format
        val appointmentTime = try {
            LocalDateTime.parse(request.appointmentTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            return@post call.respond(HttpStatusCode.BadRequest, 
                mapOf("message" to "Invalid appointment time format. Use ISO format: 2026-10-15T10:30:00"))
        }
        
        // Validate appointment is in the future
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            return@post call.respond(HttpStatusCode.BadRequest, 
                mapOf("message" to "Appointment time must be in the future"))
        }
        
        // Parse IDs
        val testId = try {
            UUID.fromString(request.testId)
        } catch (e: Exception) {
            return@post call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid test ID format"))
        }
        
        val centreId = try {
            UUID.fromString(request.centreId)
        } catch (e: Exception) {
            return@post call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid centre ID format"))
        }
        
        // Verify test exists and get its price
        val test = transaction {
            DiagnosticTests.select { DiagnosticTests.id eq testId }.firstOrNull()
        }
        
        if (test == null) {
            return@post call.respond(HttpStatusCode.NotFound, mapOf("message" to "Test not found"))
        }
        
        // Verify centre exists
        val centreExists = transaction {
            Centres.select { Centres.id eq centreId }.firstOrNull()
        }
        
        if (centreExists == null) {
            return@post call.respond(HttpStatusCode.NotFound, mapOf("message" to "Centre not found"))
        }
        
        // Verify test belongs to the specified centre
        if (test[DiagnosticTests.centreId] != centreId) {
            return@post call.respond(HttpStatusCode.BadRequest, 
                mapOf("message" to "Test does not belong to the specified centre"))
        }
        
        val testPrice = test[DiagnosticTests.price]
        val userIdUUID = UUID.fromString(userId)
        
        // Create booking with PENDING status
        val bookingId = transaction {
            Bookings.insert {
                it[Bookings.userId] = userIdUUID
                it[Bookings.testId] = testId
                it[Bookings.centreId] = centreId
                it[Bookings.appointmentTime] = appointmentTime
                it[Bookings.amount] = testPrice
                it[Bookings.status] = "PENDING"
            } get Bookings.id
        }
        
        call.respond(HttpStatusCode.Created, BookingResponse(
            id = bookingId.toString(),
            userId = userId,
            testId = testId.toString(),
            centreId = centreId.toString(),
            appointmentTime = appointmentTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            amount = testPrice.toDouble(),
            status = "PENDING"
        ))
    }
    
    // GET /bookings - Get all bookings for the authenticated user (Protected)
    get("/bookings") {
        val principal = call.principal<JWTPrincipal>()
            ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Not authenticated"))
        
        val userId = principal.payload.getClaim("userId").asString()
        val userIdUUID = UUID.fromString(userId)
        
        val bookings = transaction {
            Bookings.select { Bookings.userId eq userIdUUID }.map {
                BookingResponse(
                    id = it[Bookings.id].toString(),
                    userId = it[Bookings.userId].toString(),
                    testId = it[Bookings.testId].toString(),
                    centreId = it[Bookings.centreId].toString(),
                    appointmentTime = it[Bookings.appointmentTime].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    amount = it[Bookings.amount].toDouble(),
                    status = it[Bookings.status]
                )
            }
        }
        
        call.respond(HttpStatusCode.OK, bookings)
    }
    
    // GET /bookings/{id} - Get a specific booking (Protected)
    get("/bookings/{id}") {
        val principal = call.principal<JWTPrincipal>()
            ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Not authenticated"))
        
        val userId = principal.payload.getClaim("userId").asString()
        val bookingIdString = call.parameters["id"]
        
        val bookingId = try {
            UUID.fromString(bookingIdString)
        } catch (e: Exception) {
            return@get call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid booking ID format"))
        }
        
        val booking = transaction {
            Bookings.select { Bookings.id eq bookingId }.firstOrNull()
        }
        
        if (booking == null) {
            return@get call.respond(HttpStatusCode.NotFound, mapOf("message" to "Booking not found"))
        }
        
        // Verify the booking belongs to the authenticated user
        if (booking[Bookings.userId].toString() != userId) {
            return@get call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You can only view your own bookings"))
        }
        
        call.respond(HttpStatusCode.OK, BookingResponse(
            id = booking[Bookings.id].toString(),
            userId = booking[Bookings.userId].toString(),
            testId = booking[Bookings.testId].toString(),
            centreId = booking[Bookings.centreId].toString(),
            appointmentTime = booking[Bookings.appointmentTime].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            amount = booking[Bookings.amount].toDouble(),
            status = booking[Bookings.status]
        ))
    }
}