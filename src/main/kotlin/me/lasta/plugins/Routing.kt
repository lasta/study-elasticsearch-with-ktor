package me.lasta.plugins

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.feature
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.HttpMethodRouteSelector
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing

@KtorExperimentalLocationsAPI
fun Application.configureRouting() {
    install(Locations) {
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get<MyLocation> {
            call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
        }
        // Register nested routes
        get<Type.Edit> {
            call.respondText("Inside $it")
        }
        get<Type.List> {
            call.respondText("Inside $it")
        }
        get("/routes") {
            val root = feature(Routing)
            val allRoutes = root.list(root)
            val allRoutesWithMethod = allRoutes.filter { it.selector is HttpMethodRouteSelector }
            call.respond(allRoutesWithMethod.map(Route::toString))
        }
        install(StatusPages) {
            exception<AuthenticationException> {
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}

@KtorExperimentalLocationsAPI
@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

@KtorExperimentalLocationsAPI
@Location("/type/{name}")
data class Type(val name: String) {
    @KtorExperimentalLocationsAPI
    @Location("/edit")
    data class Edit(val type: Type)

    @KtorExperimentalLocationsAPI
    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

private fun Route.list(root: Route): List<Route> = listOf(root) + root.children.flatMap { this.list(it) }

