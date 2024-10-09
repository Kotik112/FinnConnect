package com.example.routes

import com.example.client.ExchangeRateClient
import com.example.model.ExchangeRate
import com.example.service.ExchangeRateService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDate

fun Route.mainRoute(exchangeRateClient: ExchangeRateClient, exchangeRateService: ExchangeRateService) {
    val logger = LoggerFactory.getLogger("MainRoutes")

    route("/") {
        get {
            call.respond(HttpStatusCode.OK, "Hello, World!")
        }

        // Temporary endpoint for fetching exchange rates from openexchangerates.org
        get("runClient") {
            logger.debug("Fetched exchange rates from openexchangerates.org")
            val exchangeRateMap = exchangeRateClient.getAllExchangeRates()

            val exchangeRateList = exchangeRateMap.map {(currency, rate) ->
                ExchangeRate(
                    currencyCode = currency,
                    exchangeRate = BigDecimal(rate),
                    effectiveDate = LocalDate.now()
                )
            }
            exchangeRateService.insertExchangeRates(exchangeRateList)
            logger.debug("Data fetched and inserted into the database: {}", exchangeRateList)
            call.respond(HttpStatusCode.OK, exchangeRateList)
        }
    }
}