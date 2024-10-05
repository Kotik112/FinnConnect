package com.example

import com.example.client.ExchangeRateClient
import com.example.dao.ExchangeRateRepository
import com.example.dao.OAuthTokenRepository
import com.example.plugins.*
import com.example.service.ExchangeRateService
import com.example.service.OAuthTokenService
import com.example.utils.TimeProvider
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


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

    configureTemplating()
    configureSerialization()
    configureMonitoring()
    configureDatabases()
    configureHTTP()
    configureSecurity()
    configureRouting(
        exchangeRateService = exchangeRateService,
        exchangeRateClient = exchangeRateClient,
        tokenService = tokenService,
        timeProvider = timeProvider
    )
}

fun scheduleJob(exchangeRateService: ExchangeRateService) {
    val timer = Timer()
    val task = object : TimerTask() {
        override fun run() {
            CoroutineScope(Dispatchers.IO).launch {
                val currencies = listOf("USD", "EUR", "GBP")
                exchangeRateService.getRates(currencies)
                println("Fetching exchange rates! for ${currencies.joinToString(",")}")
            }
            println("Running task!")
        }
    }
    // Schedule the task to run at fixed rate (e.g., every 24 hour)
    timer.scheduleAtFixedRate(task, 0, 24 * 60 * 60 * 1000)
}
