package com.evehealthcare.db

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        try {
            println("🔧 Initializing database connection...")
            
            val config = ConfigFactory.load()
            val dbConfig = config.getConfig("database")

            val hikariConfig = HikariConfig().apply {
                jdbcUrl = dbConfig.getString("url")
                driverClassName = dbConfig.getString("driver")
                username = dbConfig.getString("user")
                password = dbConfig.getString("password")
                maximumPoolSize = 10
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }

            val ds = HikariDataSource(hikariConfig)
            Database.connect(ds)
            
            println("✅ Database connection established!")
            println(" Creating tables...")
            
            transaction {
                SchemaUtils.create(
                    Users, 
                    Centres, 
                    DiagnosticTests, 
                    Bookings, 
                    Payments
                )
            }
            
            println("✅ Database connected and tables created successfully!")
        } catch (e: Exception) {
            println("❌ Database initialization failed: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}