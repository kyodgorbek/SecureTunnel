package com.yodgorbek.securetunnel.backend

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("SecureTunnel Backend Service is Running")
            }
            get("/health") {
                call.respondText("OK")
            }
        }
    }.start(wait = true)
}
