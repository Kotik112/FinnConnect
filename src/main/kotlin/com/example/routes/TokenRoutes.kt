package com.example.routes

import com.example.model.TokenResponse
import com.example.service.OAuthTokenService
import io.ktor.http.*
import io.ktor.server.response.respond
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.tokenRoutes(tokenService: OAuthTokenService) {
    // Save a token
    post("/token") {
        val tokenResponse = call.receive<TokenResponse>()
        val isSaved = tokenService.saveToken(tokenResponse)
        if (isSaved) {
            call.respond(HttpStatusCode.Created, "Token saved successfully.")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Failed to save token.")
        }
    }

    // Retrieve a token by user ID
    get("/token/{userId}") {
        val userId = call.parameters["userId"]
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, MISSING_TOKEN)
            return@get
        }

        val token = tokenService.getToken(userId)
        if (token != null) {
            call.respond(HttpStatusCode.OK, token)
        } else {
            call.respond(HttpStatusCode.NotFound, "Token not found.")
        }
    }

    // Check if a token is expired
    get("/token/expired/{userId}") {
        val userId = call.parameters["userId"]
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, MISSING_TOKEN)
            return@get
        }

        val token = tokenService.getToken(userId)
        if (token != null) {
            val isExpired = tokenService.isTokenExpired(token)
            call.respond(HttpStatusCode.OK, mapOf("expired" to isExpired))
        } else {
            call.respond(HttpStatusCode.NotFound, "Token not found.")
        }
    }

    // Delete a token by user ID
    delete("/token/{userId}") {
        val userId = call.parameters["userId"]
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, MISSING_TOKEN)
            return@delete
        }

        val isDeleted = tokenService.deleteToken(userId)
        if (isDeleted) {
            call.respond(HttpStatusCode.OK, "Token deleted successfully.")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Failed to delete token.")
        }
    }
}

const val MISSING_TOKEN = "Missing userId parameter."
