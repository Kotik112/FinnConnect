package com.example.dao

import com.example.model.ExchangeRate
import com.example.utility.TestContainer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.sql.DriverManager
import java.sql.Connection
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExchangeRateRepositoryTest {

    private lateinit var connection: Connection
    private lateinit var postgresContainer: TestContainer.KPostgreSQLContainer
    private lateinit var exchangeRateRepository: ExchangeRateRepository

    @BeforeAll
    fun setUp() {
        postgresContainer = TestContainer.startPostgresContainer()
        connection = DriverManager.getConnection(
            postgresContainer.jdbcUrl,
            postgresContainer.username,
            postgresContainer.password
        )
        exchangeRateRepository = ExchangeRateRepository(connection)
    }

    @AfterAll
    fun tearDown() {
        connection.close()
        postgresContainer.stop()
    }

    @Test
    fun `should insert exchange rates`() {
        val date = LocalDate.of(2024,10,1)
        val insertedExchangeRates = listOf(
            ExchangeRate(
                currencyCode = "GBP",
                exchangeRate = BigDecimal("0.8900"),
                effectiveDate = date
            ),
            ExchangeRate(
                currencyCode = "SEK",
                exchangeRate = BigDecimal("13.8000"),
                effectiveDate = date
            )
        )
        val insertedRecords = exchangeRateRepository.insert(insertedExchangeRates)
        Assertions.assertEquals(insertedRecords, 2)
    }

    @Test
    fun `test get latest exchange rates`() {
        val expectedDate = LocalDate.of(2024,10,1)
        val insertedExchangeRates = listOf(
            ExchangeRate(
                currencyCode = "GBP",
                exchangeRate = BigDecimal("0.8900"),
                effectiveDate = expectedDate
            ),
            ExchangeRate(
                currencyCode = "SEK",
                exchangeRate = BigDecimal("13.8000"),
                effectiveDate = expectedDate
            ),
        )
        val inserts = exchangeRateRepository.insert(insertedExchangeRates)

        val result = exchangeRateRepository.getLatestExchangeRates(expectedDate)

        Assertions.assertEquals(inserts, 2)
        Assertions.assertEquals(result, insertedExchangeRates)
    }
}