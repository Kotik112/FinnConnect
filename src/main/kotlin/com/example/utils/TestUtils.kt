package com.example.utils

import com.example.model.ExchangeRate
import java.math.RoundingMode
import kotlin.test.assertTrue

fun List<ExchangeRate>.shouldBeIgnoringPrecision(expected: List<ExchangeRate>, scale: Int = 2) {
    assertTrue(this.size == expected.size, "Lists do not have the same size")

    this.zip(expected).forEach { (actual, expected) ->
        assertTrue(
            actual.currencyCode == expected.currencyCode,
            "Currency codes do not match: expected ${expected.currencyCode}, but was ${actual.currencyCode}"
        )

        val actualRounded = actual.exchangeRate.setScale(scale, RoundingMode.HALF_UP)
        val expectedRounded = expected.exchangeRate.setScale(scale, RoundingMode.HALF_UP)

        assertTrue(
            actualRounded == expectedRounded,
            "Exchange rates do not match for ${actual.currencyCode}: expected $expectedRounded, but was $actualRounded"
        )

        assertTrue(
            actual.effectiveDate == expected.effectiveDate,
            "Effective dates do not match: expected ${expected.effectiveDate}, but was ${actual.effectiveDate}"
        )
    }
}