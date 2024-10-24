package com.example.routes

import com.example.model.User
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {
    route("/user") {
        post {
            val user = call.receive<User>()
            val savedUser = userService.saveUser(user)
            call.respond(HttpStatusCode.Created, savedUser)
        }

        get {
            val username = call.parameters["username"] ?: throw IllegalArgumentException("Missing username")
            val password = call.parameters["password"] ?: throw IllegalArgumentException("Missing password")
            val user = userService.getUserByUsername(username, password)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}