package com.example.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.get


fun Route.healthRouting() {
    route("/finnconnect/health") {
        get {
            call.response.status(HttpStatusCode.OK)
            call.respondText { "SUCCESS" }
        }
    }
}