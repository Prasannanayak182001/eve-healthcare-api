package com.evehealthcare.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

// 1. Users Table
object Users : Table("users") {
    val id = uuid("id").autoGenerate()
    val email = varchar("email", 255).uniqueIndex() // Unique constraint for login
    val passwordHash = varchar("password_hash", 255)
    val createdAt = datetime("created_at").clientDefault { java.time.LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}

// 2. Centres Table
object Centres : Table("centres") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    val location = varchar("location", 255)
    val createdAt = datetime("created_at").clientDefault { java.time.LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}

// 3. Diagnostic Tests Table
object DiagnosticTests : Table("diagnostic_tests") {
    val id = uuid("id").autoGenerate()
    val centreId = uuid("centre_id").references(Centres.id) // Foreign Key
    val name = varchar("name", 255)
    val price = decimal("price", 10, 2) // 10 digits total, 2 decimal places for currency
    val createdAt = datetime("created_at").clientDefault { java.time.LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}

// 4. Bookings Table
object Bookings : Table("bookings") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id)
    val testId = uuid("test_id").references(DiagnosticTests.id)
    val centreId = uuid("centre_id").references(Centres.id) // Denormalized for faster queries
    val appointmentTime = datetime("appointment_time")
    val amount = decimal("amount", 10, 2)
    val status = varchar("status", 50) // PENDING, CONFIRMED, FAILED, CANCELLED
    val createdAt = datetime("created_at").clientDefault { java.time.LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}

// 5. Payments Table (Crucial for Webhook Idempotency)
object Payments : Table("payments") {
    val id = uuid("id").autoGenerate()
    val bookingId = uuid("booking_id").references(Bookings.id)
    
    // 🔑 THE SECRET SAUCE FOR IDEMPOTENCY: 
    // A unique constraint on the provider's transaction ID. 
    // If a duplicate webhook arrives, the DB will reject the insert, preventing duplicate state changes.
    val providerTransactionId = varchar("provider_transaction_id", 255).uniqueIndex() 
    
    val status = varchar("status", 50) // SUCCESS, FAILED
    val createdAt = datetime("created_at").clientDefault { java.time.LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}