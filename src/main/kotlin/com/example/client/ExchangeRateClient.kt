package com.example.client

import com.example.model.ExchangeRateResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.slf4j.LoggerFactory


class ExchangeRateClient(private val apiKey: String) : HttpClientBase() {

    private val logger = LoggerFactory.getLogger(ExchangeRateClient::class.java)
    private val baseUrl = "https://openexchangerates.org/api"

    /**
     * Fetches the latest exchange rates for the specified list of currencies.
     *
     * @param currencies A list of currency codes (e.g., "USD", "EUR") for which exchange rates are requested.
     * @return A map where the keys are currency codes and the values are the corresponding exchange rates.
     */
    suspend fun getExchangeRate(currencies: List<String>) : Map<String, Double> {
        val endPoint = "$baseUrl/latest.json"
        val symbols = currencies.joinToString(",")

        val response : ExchangeRateResponse = client.get(endPoint) {
            url {
                parameters.append("app_id", apiKey)
                parameters.append("symbols", symbols)
            }
        }.body()

        logger.debug("Data fetched with symbols: {} = {}", symbols, response)

        val result = mutableMapOf<String, Double>()
        currencies.forEach {
            result[it] = (response.rates[it] ?: 0) as Double
        }

        return result
    }

    /**
     * Fetches all the latest exchange rates for USD as base currency.
     *
     * @return A map where the keys are currency codes and the values are the corresponding exchange rates.
     */
    suspend fun getAllExchangeRates() : Map<String, Double> {
        val endPoint = "$baseUrl/latest.json"

        val response : ExchangeRateResponse = client.get(endPoint) {
            headers {
                headers.append("Accept", "application/json")
            }
            url {
                parameters.append("app_id", apiKey)
            }
        }.body()
        if (response.rates.isEmpty()) {
            logger.warn("No exchange rates found for USD as base currency.")
            return emptyMap()
        }
        logger.debug("Data fetched for all currencies: {}", response.base to response.rates)

        val result = mutableMapOf<String, Double>()
        response.rates.forEach { currency, rate ->
            result[currency] = rate
        }

        return result
    }
}