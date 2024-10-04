package com.example.routes

import com.example.client.MonzoClient
import com.example.utils.TimeProvider
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.util.Base64

fun Route.monzoRoutes(clientId: String, clientSecret: String, timeProvider: TimeProvider) {
    val logger = LoggerFactory.getLogger("MonzoRoutes")
    val monzoClient = MonzoClient(timeProvider)

    route(("/auth")) {
        get("/monzo") {
            val state = generateStateToken()
            val redirectUrl = "http://localhost:8080/auth/callback"
            val authUrl = "https://auth.monzo.com/?client_id=$clientId&redirect_uri=$redirectUrl&response_type=code&state=$state"

            logger.debug("Redirecting to Monzo authentication: $authUrl")
            call.respondRedirect(authUrl, permanent = false)
        }

        get("/callback") {
            val code = call.parameters["code"]
            val state = call.parameters["state"]

            if (code == null || state == null) {
                logger.error("Missing or invalid 'code' or 'state' parameters in Monzo callback")
                call.respondText("Missing or invalid 'code' or 'state' parameters in Monzo callback")
                return@get
            }

            logger.debug("Received Monzo callback with code: $code and state: $state")

            // Exchange the authorization code for an access token
            val token = monzoClient.exchangeAuthorizationCodeForToken(
                code = code,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = "http://localhost:8080/auth/callback"
            )
            if (token == null) {
                logger.error("Failed to exchange authorization code for token")
                call.respondText("Failed to exchange authorization code for token")
                return@get
            } else {
                logger.info("Monzo authentication successful. Access token: $token")
                call.respondText("Monzo authentication successful. Access token: $token")
            }
        }
    }
}


private fun generateStateToken(): String {
    val random = SecureRandom()
    val bytes = ByteArray(32)
    random.nextBytes(bytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}