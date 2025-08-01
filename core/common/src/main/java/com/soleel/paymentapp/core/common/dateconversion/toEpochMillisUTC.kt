package com.soleel.paymentapp.core.common.dateconversion

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toEpochMillisUTC(): Long =
    this.atZone(ZoneId.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()