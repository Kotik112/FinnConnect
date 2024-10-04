package com.example.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TimeProvider(internal val zoneId: ZoneId = ZoneId.of("CET")) {
    fun getCurrentTime(): LocalDateTime {
        return ZonedDateTime.now(zoneId).toLocalDateTime()
    }
}