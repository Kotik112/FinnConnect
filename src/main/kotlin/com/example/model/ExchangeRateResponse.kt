package com.example.model

import com.example.utils.LocalDateTimeSerializer
import com.example.utils.TimeProvider
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/**
 * Represents the response from the Exchange Rate API (OpenExchangeRates)
 *
 * @param disclaimer A legal disclaimer about the API usage.
 * @param license License information for the API.
 * @param timestamp Unix timestamp of when the rates were fetched.
 * @param base The base currency (e.g., "USD").
 * @param rates A map of target currencies to their exchange rates.
 *
 * Example usage:
 * ```kotlin
 *  val exchangeRate = ExchangeRateResponse(
 *      disclaimer = "License information",
 *      license = "MIT License",
 *      rates = mapOf("EUR" to 0.85, "GBP" to 0.78)
 *  )
 *  ```
 */
@Serializable
data class ExchangeRateResponse(
    val disclaimer: String,       // Legal disclaimer about the API usage
    val license: String,          // License information
    @Serializable(with = LocalDateTimeSerializer::class)
    val timestamp: LocalDateTime,          // Unix timestamp of when the rates were fetched
    val base: String,             // The base currency (e.g., "USD")
    val rates: Map<String, Double> // A map of target currencies to their exchange rates
) {
    /**
     * Constructs an instance of [ExchangeRateResponse] with the provided disclaimer, license, and rates.
     *
     * @param disclaimer A legal disclaimer about the API usage.
     * @param license License information for the API.
     * @param rate A map of target currencies to their exchange rates.
     */
    constructor(disclaimer: String, license: String, rate: Map<String, Double>) : this(
        disclaimer = disclaimer,
        license = license,
        timestamp = TimeProvider().getCurrentTime(),
        base = "USD",
        rates = mutableMapOf<String, Double>(),
    )
}