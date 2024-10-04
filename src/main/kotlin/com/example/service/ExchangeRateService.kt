package com.example.service

import com.example.client.ExchangeRateClient
import com.example.dao.ExchangeRateRepository
import com.example.model.ExchangeRate
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class ExchangeRateService(private val exchangeRateClient: ExchangeRateClient, private val repository: ExchangeRateRepository) {
    private val logger = LoggerFactory.getLogger(ExchangeRateService::class.java)

    /**
     * Fetches the exchange rates for the specified list of currencies and stores them in the repository.
     *
     * @param currencies A list of currency codes for which the exchange rates are to be fetched.
     * @return A list of [ExchangeRate] objects containing the exchange rate information for each currency.
     */
    suspend fun getRates(currencies: List<String>) : List<ExchangeRate> {
        val currencyExchangeMap = exchangeRateClient.getExchangeRate(currencies = currencies)
        val exchangeRateList = mutableListOf<ExchangeRate>()
        currencyExchangeMap.forEach { (key, value) ->
            val exchangeRate = ExchangeRate(
                currencyCode = key,
                exchangeRate = BigDecimal(value).setScale(2, RoundingMode.HALF_UP),
                effectiveDate = LocalDate.now()
            )
            exchangeRateList.add(exchangeRate)
            logger.debug("$key to $value, added to result.")
        }
        repository.insert(exchangeRateList)
        return exchangeRateList
    }

    /**
     * Retrieves the latest exchange rates available as of the specified date.
     *
     * @param asOfDate The date for which the closest available exchange rates are to be fetched.
     * @return A list of [ExchangeRate] objects representing the exchange rates closest to the specified date.
     */
    fun getLatestExchangeRates(asOfDate: LocalDate): List<ExchangeRate> {
        logger.debug("Fetching exchange rates for date closest to {}", asOfDate)
        return repository.getLatestExchangeRate(asOfDate)
    }

    /**
     * Inserts the given exchange rates into the repository.
     *
     * @param exchangeRates A list of [ExchangeRate] objects to be inserted.
     * @return The number of rows inserted.
     */
    fun insertExchangeRates(exchangeRates: List<ExchangeRate>): Int {
        return repository.insert(exchangeRates)
    }
}