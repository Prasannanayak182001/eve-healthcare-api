package com.evehealthcare

import com.evehealthcare.db.DatabaseFactory
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    // 1. Initialize the database connection pool FIRST
    DatabaseFactory.init()
    
    // 2. Start the Ktor server (this loads all modules from application.conf)
    EngineMain.main(args)
}