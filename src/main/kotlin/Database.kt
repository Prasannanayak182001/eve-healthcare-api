package com.evehealthcare

import com.evehealthcare.db.DatabaseFactory
import io.ktor.server.application.*

fun Application.configureDatabase() {
    DatabaseFactory.init()
}