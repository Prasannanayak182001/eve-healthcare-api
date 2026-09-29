package com.evehealthcare.routes

import com.evehealthcare.db.Centres
import com.evehealthcare.db.DiagnosticTests
import com.evehealthcare.models.CentreResponse
import com.evehealthcare.models.CreateCentreRequest
import com.evehealthcare.models.CreateTestRequest
import com.evehealthcare.models.TestResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

// 1. Public Routes (Anyone can view)
fun Route.configurePublicCentreTestRouting() {
    get("/centres") {
        val centres = transaction {
            Centres.selectAll().map {
                CentreResponse(
                    id = it[Centres.id].toString(),
                    name = it[Centres.name],
                    location = it[Centres.location]
                )
            }
        }
        call.respond(HttpStatusCode.OK, centres)
    }

    get("/centres/{id}/tests") {
        val centreIdString = call.parameters["id"]
        val centreId = try {
            UUID.fromString(centreIdString)
        } catch (e: Exception) {
            return@get call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid centre ID format"))
        }

        val tests = transaction {
            DiagnosticTests.selectAll()
                .where { DiagnosticTests.centreId eq centreId }
                .map {
                    TestResponse(
                        id = it[DiagnosticTests.id].toString(),
                        centreId = it[DiagnosticTests.centreId].toString(),
                        name = it[DiagnosticTests.name],
                        price = it[DiagnosticTests.price].toDouble()
                    )
                }
        }
        call.respond(HttpStatusCode.OK, tests)
    }

    get("/tests") {
        val tests = transaction {
            DiagnosticTests.selectAll().map {
                TestResponse(
                    id = it[DiagnosticTests.id].toString(),
                    centreId = it[DiagnosticTests.centreId].toString(),
                    name = it[DiagnosticTests.name],
                    price = it[DiagnosticTests.price].toDouble()
                )
            }
        }
        call.respond(HttpStatusCode.OK, tests)
    }
}

// 2. Protected Routes (Requires JWT, handled by authenticate {} block in Routing.kt)
fun Route.configureProtectedCentreTestRouting() {
    post("/centres") {
        val request = call.receive<CreateCentreRequest>()
        
        if (request.name.isBlank() || request.location.isBlank()) {
            return@post call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Name and location are required"))
        }

        val centreId = transaction {
            Centres.insert {
                it[name] = request.name
                it[location] = request.location
            } get Centres.id
        }

        call.respond(HttpStatusCode.Created, CentreResponse(
            id = centreId.toString(),
            name = request.name,
            location = request.location
        ))
    }

    post("/tests") {
        val request = call.receive<CreateTestRequest>()
        
        if (request.name.isBlank() || request.price <= 0) {
            return@post call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Valid name and positive price are required"))
        }

        val requestCentreId = try {
            UUID.fromString(request.centreId)
        } catch (e: Exception) {
            return@post call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid centre ID format"))
        }

        val centreExists = transaction {
            Centres.selectAll().where { Centres.id eq requestCentreId }.any()
        }

        if (!centreExists) {
            return@post call.respond(HttpStatusCode.NotFound, mapOf("message" to "Centre not found"))
        }

        val testId = transaction {
            DiagnosticTests.insert {
                it[centreId] = requestCentreId
                it[name] = request.name
                it[price] = java.math.BigDecimal.valueOf(request.price)
            } get DiagnosticTests.id
        }

        call.respond(HttpStatusCode.Created, TestResponse(
            id = testId.toString(),
            centreId = request.centreId,
            name = request.name,
            price = request.price
        ))
    }
}