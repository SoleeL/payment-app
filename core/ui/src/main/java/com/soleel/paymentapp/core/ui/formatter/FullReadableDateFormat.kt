package com.soleel.paymentapp.core.ui.formatter

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class FullReadableDateFormat {
    companion object {
        @SuppressLint("ConstantLocale")
        private val formatterWithoutYear = DateTimeFormatter.ofPattern(
            "d 'de' MMMM",
            Locale.getDefault()
        )

        @SuppressLint("ConstantLocale")
        private val formatter = DateTimeFormatter.ofPattern(
            "d 'de' MMMM 'del' yyyy",
            Locale.getDefault()
        )

        operator fun invoke(localDate: LocalDate): String {
            if (localDate.year == LocalDate.now().year) {
                return formatterWithoutYear.format(localDate)
            }
            return formatter.format(localDate)
        }
    }
}