package com.example.routes

import com.example.client.MonzoClient
import com.example.model.Either
import com.example.service.OAuthTokenService
import com.example.utils.TimeProvider
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.util.Base64

fun Route.monzoRoutes(timeProvider: TimeProvider, tokenService: OAuthTokenService) {
    val logger = LoggerFactory.getLogger("MonzoRoutes")
    val monzoClient = MonzoClient(tokenService, timeProvider)

    val clientId: String = environment.config.property("monzo.clientId").getString()
    val clientSecret: String = environment.config.property("monzo.clientSecret").getString()
    val accountId: String = environment.config.property("monzo.accountId").getString()
    val userId: String = environment.config.property("monzo.userId").getString()

    val acessTokenNotFound = "Access token not found."

    route(("/auth")) {
        get("/monzo") {
            val state = generateStateToken()
            val redirectUrl = "http://localhost:8080/auth/callback"
            val scopes = "accounts transactions:read"
            val authUrl = "https://auth.monzo.com/?client_id=$clientId&redirect_uri=$redirectUrl&" +
                    "response_type=code&state=$state&scope=$scopes"

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

            val token = monzoClient.exchangeAuthorizationCodeForToken(
                code = code,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = "http://localhost:8080/auth/callback"
            )

            if (token == null) {
                logger.error("Failed to exchange authorization code for token")
                call.respond(HttpStatusCode.BadRequest, "Failed to exchange authorization code for token")
                return@get
            } else {
                logger.info("Monzo authentication successful. Access token: ${token.accessToken}")
                call.respond(HttpStatusCode.BadRequest, "Monzo authentication successful. Access token: ${token.accessToken}")
            }
        }
    }

    get("/whoami") {
        // Retrieve the stored access token from tokenService
        val token = tokenService.getToken(userId)

        if (token == null) {
            logger.error("Access token not found for user: $userId")
            call.respond(HttpStatusCode.Unauthorized, acessTokenNotFound)
            return@get
        }

        // Make the authenticated request to Monzo's `ping/whoami`
        val whoAmIResponse = monzoClient.whoAmI(accessToken = token.accessToken)

        if (whoAmIResponse != null) {
            call.respond(HttpStatusCode.OK, whoAmIResponse)
        } else {
            logger.error("Failed to retrieve `whoami` response for user: $userId")
            call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve `whoami` response.")
        }
    }
    get("/accounts") {
        val token = tokenService.getToken(userId)

        if (token == null) {
            logger.error("Access token not found for user: {}", userId)
            call.respond(HttpStatusCode.Unauthorized, acessTokenNotFound)
            return@get
        }

        val result = monzoClient.getMonzoAccounts(token.accessToken)
        when (result) {
            is Either.Success -> {
                call.respond(HttpStatusCode.OK, result.data)
            }
            is Either.Failure -> {
                val errorResponse = result.error
                logger.error("Failed to retrieve accounts for user: $userId, Error: $errorResponse")
                call.respond(HttpStatusCode.Forbidden, errorResponse)
            }
        }
    }

    get("/balance") {
        val token = tokenService.getToken(userId)

        if (token == null) {
            logger.error("Access token not found for user: {}", userId)
            call.respond(HttpStatusCode.Unauthorized, acessTokenNotFound)
            return@get
        }

        val result = monzoClient.getMonzoBalance(token.accessToken, accountId)
        when (result) {
            is Either.Success -> call.respond(HttpStatusCode.OK, result.data)
            is Either.Failure -> {
                val errorResponse = result.error
                logger.error("Failed to retrieve balance for user: $userId, Error: $errorResponse")
                call.respond(HttpStatusCode.Forbidden, errorResponse)
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