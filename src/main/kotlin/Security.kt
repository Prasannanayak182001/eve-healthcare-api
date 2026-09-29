package com.evehealthcare

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

// We will use this constant to identify our JWT principal
const val USER_ID_CLAIM = "userId"

fun Application.configureSecurity() {
    val config = ConfigFactory.load()
    val jwtSecret = config.getString("jwt.secret")
    val jwtIssuer = config.getString("jwt.issuer")
    val jwtAudience = config.getString("jwt.audience")
    val jwtRealm = config.getString("jwt.realm")

    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(USER_ID_CLAIM).asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

// Helper function to generate a JWT token
fun generateToken(userId: String, jwtSecret: String, jwtIssuer: String, jwtAudience: String): String {
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim(USER_ID_CLAIM, userId)
        .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 hours
        .sign(Algorithm.HMAC256(jwtSecret))
}