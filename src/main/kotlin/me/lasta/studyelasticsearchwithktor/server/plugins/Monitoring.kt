package me.lasta.studyelasticsearchwithktor.server.plugins

import io.ktor.features.*
import org.slf4j.event.*
import io.micrometer.prometheus.*
import io.ktor.metrics.micrometer.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        // ...
    }

    routing {
        get("/metrics-micrometer") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
