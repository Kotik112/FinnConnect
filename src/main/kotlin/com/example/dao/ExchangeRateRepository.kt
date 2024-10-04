package com.example.dao

import com.example.model.ExchangeRate
import com.example.service.ExchangeRateService
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate

class ExchangeRateRepository(private val connection: Connection) {
    private val exchangeRateRepoLogger = LoggerFactory.getLogger(ExchangeRateRepository::class.java)

    companion object {
        private const val CREATE_TABLE_EXCHANGE_RATES =
            "CREATE TABLE IF NOT EXISTS curr_exch_rate (curr_cd VARCHAR(3) NOT NULL, exch_rate NUMERIC(10, 4) NOT NULL, eff_dt DATE NOT NULL, " +
                    "PRIMARY KEY (currencyCode, effectiveDate));"
        private const val INSERT_EXCHANGE_RATE =
            "INSERT INTO curr_exch_rate (curr_cd, exch_rate, eff_dt) VALUES (?, ?, ?) " +
                    "ON CONFLICT (curr_cd, eff_dt) DO UPDATE SET exch_rate = EXCLUDED.exch_rate, eff_dt = EXCLUDED.eff_dt"
        private const val SELECT_EXCHANGE_RATE_BY_CODE_AND_DATE =
            "SELECT curr_cd, curr_exch_rate, eff_dt FROM exch_rate WHERE curr_cd = ? AND eff_dt = ?"
        private const val UPDATE_EXCHANGE_RATE =
            "UPDATE curr_exch_rate SET exch_rate = ? WHERE curr_cd = ? AND eff_dt = ?"
        private const val DELETE_EXCHANGE_RATE =
            "DELETE FROM curr_exch_rate WHERE curr_cd = ? AND eff_dt = ?"
        private val GET_LATEST_EXCHANGE_RATE = """
            with cte as (
                select curr_cd, exch_rate, eff_dt,
                row_number() over (partition by curr_cd order by eff_dt desc) as rn
                from curr_exch_rate
                where eff_dt <= ?
            )
                select cte.curr_cd, cte.exch_rate, cte.eff_dt
                from cte
                where rn = 1
        """.trimIndent()
    }

    private val logger = LoggerFactory.getLogger(ExchangeRateRepository::class.java)

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_EXCHANGE_RATES)
        logger.info("Exchange Rate table created or already exists.")
    }

    fun create(exchangeRate: ExchangeRate): Int {
        val statement: PreparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE, PreparedStatement.RETURN_GENERATED_KEYS)
        statement.setString(1, exchangeRate.currencyCode)
        statement.setBigDecimal(2, exchangeRate.exchangeRate)
        statement.setDate(3, Date.valueOf(exchangeRate.effectiveDate))
        return statement.executeUpdate()
    }

    fun read(currencyCode: String, effectiveDate: LocalDate): ExchangeRate? {
        val statement: PreparedStatement = connection.prepareStatement(SELECT_EXCHANGE_RATE_BY_CODE_AND_DATE)
        statement.setString(1, currencyCode)
        statement.setDate(2, Date.valueOf(effectiveDate))
        val resultSet: ResultSet = statement.executeQuery()
        return if (resultSet.next()) {
            ExchangeRate(
                currencyCode = resultSet.getString("curr_cd"),
                exchangeRate = resultSet.getBigDecimal("exch_rate"),
                effectiveDate = resultSet.getDate("eff_dt").toLocalDate()
            )
        } else {
            null
        }
    }

    fun getAll(effectiveDate: LocalDate) : List<ExchangeRate> {
        return emptyList()
    }

    fun getLatestExchangeRate(asOfDate: LocalDate) : List<ExchangeRate> {
        val statement = connection.prepareStatement(GET_LATEST_EXCHANGE_RATE)
        statement.setDate(1, Date.valueOf(asOfDate))
        val resultSet = statement.executeQuery()
        val exchangeRateList = mutableListOf<ExchangeRate>()
        while (resultSet.next()) {
            val exchangeRate = ExchangeRate(
                currencyCode = resultSet.getString("curr_cd"),
                exchangeRate = resultSet.getBigDecimal("exch_rate"),
                effectiveDate = resultSet.getDate("eff_dt").toLocalDate()
            )
            exchangeRateList += exchangeRate
        }
        return exchangeRateList
    }

    fun insert(exchangeRates: List<ExchangeRate>): Int {
        val statement: PreparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE)
        var rowsInserted = 0
        for (exchangeRate in exchangeRates) {
            statement.setString(1, exchangeRate.currencyCode)
            statement.setBigDecimal(2, exchangeRate.exchangeRate)
            statement.setDate(3, Date.valueOf(exchangeRate.effectiveDate))
            rowsInserted += statement.executeUpdate()
        }
        if (rowsInserted != exchangeRates.size) {
            throw RuntimeException("Failed to insert all exchange rates.")
        }
        return rowsInserted
    }

    fun update(exchangeRate: ExchangeRate): Boolean {
        val statement: PreparedStatement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)
        statement.setBigDecimal(1, exchangeRate.exchangeRate)
        statement.setString(2, exchangeRate.currencyCode)
        statement.setDate(3, Date.valueOf(exchangeRate.effectiveDate))
        return statement.executeUpdate() > 0
    }

    fun delete(currencyCode: String, effectiveDate: LocalDate): Boolean {
        val statement: PreparedStatement = connection.prepareStatement(DELETE_EXCHANGE_RATE)
        statement.setString(1, currencyCode)
        statement.setDate(2, java.sql.Date.valueOf(effectiveDate))
        return statement.executeUpdate() > 0
    }
}