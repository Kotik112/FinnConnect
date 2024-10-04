package com.example.model

import kotlinx.serialization.Serializable
import com.example.utils.BigDecimalSerializer
import com.example.utils.LocalDateSerializer
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class ExchangeRate(
    val currencyCode: String,
    @Serializable(with = BigDecimalSerializer::class)
    val exchangeRate: BigDecimal,
    @Serializable(with = LocalDateSerializer::class)
    val effectiveDate: LocalDate,
)