package me.lasta

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.lasta.plugins.*

@KtorExperimentalLocationsAPI
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureHTTP()
        configureMonitoring()
        configureSerialization()
    }.start(wait = true)
}
