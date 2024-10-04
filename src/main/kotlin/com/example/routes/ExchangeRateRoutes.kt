package com.example.routes

import com.example.service.ExchangeRateService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import java.time.LocalDate

fun Route.exchangeRateRouting(exchangeRateService: ExchangeRateService) {
    val logger = LoggerFactory.getLogger("ExchangeRateRoutes")
    route("/exchangeRate") {
        get("latest") {
            val asOfDate = call.parameters["asOfDate"]?.let { LocalDate.parse(it) }
            if (asOfDate == null) {
                logger.error("'asOfDate' parameter is missing or invalid")
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid 'asOfDate' parameter")
                return@get
            }
            val scope = CoroutineScope(Dispatchers.IO)

            try {
                val deferred = scope.async {
                    exchangeRateService.getLatestExchangeRates(asOfDate)
                }
                logger.debug("Retrieving exchange rates for {}", asOfDate)
                call.respond(HttpStatusCode.OK, deferred.await())
            } catch (e: Exception) {
                logger.error("Failed to retrieve exchange rates for {}: {}", asOfDate, e.message)
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve exchange rates: ${e.message}")
            }
        }
    }
}