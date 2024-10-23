package com.example

import com.example.client.ExchangeRateClient
import com.example.dao.ExchangeRateRepository
import com.example.dao.OAuthTokenRepository
import com.example.dao.UserRepository
import com.example.plugins.*
import com.example.service.ExchangeRateService
import com.example.service.OAuthTokenService
import com.example.utils.TimeProvider
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

}

fun Application.module() {
    val apiKey: String = environment.config.property("openexchangerates.apiKey").getString()

    val connection = connectToPostgres(false)

    val timeProvider = TimeProvider()
    val exchangeRateRepository = ExchangeRateRepository(connection)
    val exchangeRateClient = ExchangeRateClient(apiKey = apiKey)
    val exchangeRateService = ExchangeRateService(
        exchangeRateClient = exchangeRateClient,
        repository = exchangeRateRepository
    )
    val tokenRepository = OAuthTokenRepository(connection)
    val tokenService = OAuthTokenService(tokenRepository = tokenRepository, timeProvider = timeProvider)

    val userRepository = UserRepository(connection)

    configureTemplating()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting(
        exchangeRateService = exchangeRateService,
        exchangeRateClient = exchangeRateClient,
        tokenService = tokenService,
        timeProvider = timeProvider
    )
}
