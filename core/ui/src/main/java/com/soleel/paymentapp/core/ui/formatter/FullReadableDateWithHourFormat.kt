package com.soleel.paymentapp.core.ui.formatter

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class FullReadableDateWithHourFormat {
    companion object {
        @SuppressLint("ConstantLocale")
        private val formatterWithoutYear = DateTimeFormatter.ofPattern(
            "d 'de' MMMM 'a las' HH:mm",
            Locale.getDefault()
        )

        @SuppressLint("ConstantLocale")
        private val formatterWithYear = DateTimeFormatter.ofPattern(
            "d 'de' MMMM 'del' yyyy 'a las' HH:mm",
            Locale.getDefault()
        )

        operator fun invoke(localDateTime: LocalDateTime): String {
            return if (localDateTime.year == LocalDate.now().year) {
                formatterWithoutYear.format(localDateTime)
            } else {
                formatterWithYear.format(localDateTime)
            }
        }
    }
}