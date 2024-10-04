package com.example.service

import com.example.client.ExchangeRateClient
import com.example.dao.ExchangeRateRepository
import com.example.model.ExchangeRate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.kotlin.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate


class ExchangeRateServiceTest {
    val exchangeRateClient = mock<ExchangeRateClient>()
    val repository = mock<ExchangeRateRepository>()
    val service = ExchangeRateService(exchangeRateClient, repository)

    @Test
    fun `should return an empty list when the input currency list is empty`() {
        runBlocking {

            val emptyCurrencyList = emptyList<String>()

            whenever(exchangeRateClient.getExchangeRate(emptyCurrencyList)).thenReturn(emptyMap())
            whenever(repository.insert(any())).thenReturn(0)

            // Act
            val result = service.getRates(emptyCurrencyList)

            // Assert
            assertTrue(result.isEmpty())
            verify(exchangeRateClient).getExchangeRate(emptyCurrencyList)
            verify(repository).insert(emptyList())
        }
    }

    @Test
    fun `should correctly fetch and return exchange rates for a list of valid currency codes`() {
        runBlocking {
            // Arrange
            val currencyList = listOf("USD", "EUR", "GBP")
            val exchangeRateMap = mapOf(
                "USD" to 1.0,
                "EUR" to 0.86,
                "GBP" to 0.75
            )
            val expectedExchangeRates = listOf(
                ExchangeRate("USD", BigDecimal("1.0").setScale(2, RoundingMode.HALF_UP), LocalDate.now()),
                ExchangeRate("EUR", BigDecimal("0.86").setScale(2, RoundingMode.HALF_UP), LocalDate.now()),
                ExchangeRate("GBP", BigDecimal("0.75").setScale(2, RoundingMode.HALF_UP), LocalDate.now())
            )

            whenever(exchangeRateClient.getExchangeRate(currencyList)).thenReturn(exchangeRateMap)
            whenever(repository.insert(expectedExchangeRates)).thenReturn(expectedExchangeRates.size)

            // Act
            val result = service.getRates(currencyList)

            // Assert
            assertTrue(result.containsAll(expectedExchangeRates))
            verify(exchangeRateClient).getExchangeRate(currencyList)
            verify(repository).insert(expectedExchangeRates)
        }
    }


}