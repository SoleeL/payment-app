package com.soleel.paymentapp.core.common.dateconversion

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun Long.toLocalDateTimeUTC(): LocalDateTime =
    Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()