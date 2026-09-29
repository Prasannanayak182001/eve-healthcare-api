# EVE Healthcare API

A robust, production-ready REST API for managing diagnostic centre bookings, built with **Kotlin**, **Ktor**, and **PostgreSQL**.

## 🚀 Features
- **JWT Authentication**: Secure user signup and login with BCrypt password hashing.
- **Resource Management**: CRUD operations for Diagnostic Centres and Tests.
- **Booking System**: State-machine based booking flow (PENDING -> CONFIRMED/FAILED).
- **Idempotent Webhooks**: Safely handles duplicate payment gateway callbacks without data corruption.
- **Production-Grade DB**: Uses HikariCP connection pooling for high performance.
- **OpenAPI Documentation**: Auto-generated Swagger UI for easy API exploration.

## 🛠️ Tech Stack
- **Language**: Kotlin
- **Framework**: Ktor 3.x
- **Database**: PostgreSQL (Neon Serverless)
- **ORM**: Exposed (Kotlin SQL Framework)
- **Connection Pool**: HikariCP
- **Auth**: JWT (JSON Web Tokens) + BCrypt

## 🗄️ Database Design & Edge Cases
- **Idempotency Key**: The `payments` table uses a `UNIQUE` constraint on `provider_transaction_id`. If a payment gateway retries a webhook due to network issues, the database rejects the duplicate insert, preventing double-charging or state corruption.
- **Denormalization**: The `bookings` table includes a `centre_id` column. This prevents expensive `JOIN` queries when fetching user history and preserves historical accuracy even if a test is moved to a different centre later.
- **Connection Pooling**: HikariCP is configured with a pool size of 10 to handle concurrent requests efficiently without exhausting database connections.

## 🏃‍️ How to Run

### Prerequisites
- JDK 17 or higher
- Internet connection (connects to Neon Cloud DB)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/eve-healthcare-api.git
   cd eve-healthcare-api

Start the server:

   # Windows
.\gradlew.bat run

# Mac/Linux
./gradlew run

The API will be available at http://localhost:8080.
📡 API Endpoints
Authentication
POST /auth/signup - Register a new user
POST /auth/login - Login and receive JWT token
Centres & Tests (Public Read / Protected Write)
GET /centres - List all centres
POST /centres - Create a centre (Requires JWT)
GET /tests - List all tests
POST /tests - Create a test (Requires JWT)
Bookings & Payments
POST /bookings - Create a booking (Requires JWT)
GET /bookings - Get user's bookings (Requires JWT)
POST /payments/simulate - Simulate a payment (Requires JWT)
POST /payments/webhook - Handle payment callback (Public, Idempotent)