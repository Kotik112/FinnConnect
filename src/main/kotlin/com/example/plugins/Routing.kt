package com.example.plugins

import com.example.client.ExchangeRateClient
import com.example.routes.exchangeRateRouting
import com.example.routes.healthRouting
import com.example.routes.mainRoute
import com.example.routes.monzoRoutes
import com.example.routes.tokenRoutes
import com.example.routes.userRoutes
import com.example.service.ExchangeRateService
import com.example.service.OAuthTokenService
import com.example.service.UserService
import com.example.utils.TimeProvider
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    exchangeRateService: ExchangeRateService,
    exchangeRateClient: ExchangeRateClient,
    tokenService: OAuthTokenService,
    timeProvider: TimeProvider,
    userService: UserService
) {
    routing {
        singlePageApplication {
            useResources = true
            applicationRoute = "/"
            filesPath = "static"
            defaultPage = "index.html"
        }
        healthRouting()
        mainRoute(exchangeRateClient = exchangeRateClient, exchangeRateService)
        exchangeRateRouting(exchangeRateService = exchangeRateService)
        tokenRoutes(tokenService = tokenService)
        monzoRoutes(
            timeProvider = timeProvider,
            tokenService = tokenService
        )
        userRoutes(userService = userService)
    }
}
