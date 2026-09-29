package com.evehealthcare.routes

import com.evehealthcare.USER_ID_CLAIM
import com.evehealthcare.db.Users
import com.evehealthcare.generateToken
import com.evehealthcare.models.AuthResponse
import com.evehealthcare.models.ErrorResponse
import com.evehealthcare.models.LoginRequest
import com.evehealthcare.models.SignupRequest
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

// Change this function signature
fun Route.configureAuthRouting() {
    val config = ConfigFactory.load()
    val jwtSecret = config.getString("jwt.secret")
    val jwtIssuer = config.getString("jwt.issuer")
    val jwtAudience = config.getString("jwt.audience")

    // Remove the outer routing { } block and use route instead
    route("/auth") {
        
        // POST /auth/signup
        post("/signup") {
            val request = call.receive<SignupRequest>()
            
            // Basic validation
            if (request.email.isBlank() || request.password.length < 6) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid email or password (min 6 chars)"))
                return@post
            }

            val userId = transaction {
                // Check if user already exists
                val existingUser = Users.select { Users.email eq request.email }.firstOrNull()
                if (existingUser != null) {
                    return@transaction null
                }

                // Hash password and insert
                val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
                Users.insert {
                    it[email] = request.email
                    it[passwordHash] = hashedPassword
                } get Users.id
            }

            if (userId == null) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("Email already registered"))
                return@post
            }

            val token = generateToken(userId.toString(), jwtSecret, jwtIssuer, jwtAudience)
            call.respond(HttpStatusCode.Created, AuthResponse(userId.toString(), request.email, token))
        }

        // POST /auth/login
        post("/login") {
            val request = call.receive<LoginRequest>()

            val user = transaction {
                Users.select { Users.email eq request.email }.firstOrNull()
            }

            if (user == null || !BCrypt.checkpw(request.password, user[Users.passwordHash])) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid email or password"))
                return@post
            }

            val userIdString = user[Users.id].toString()
            val token = generateToken(userIdString, jwtSecret, jwtIssuer, jwtAudience)
            
            call.respond(HttpStatusCode.OK, AuthResponse(userIdString, request.email, token))
        }
    }
}