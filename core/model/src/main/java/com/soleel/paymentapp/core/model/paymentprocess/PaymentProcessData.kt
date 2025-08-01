package com.soleel.paymentapp.core.model.paymentprocess

import java.time.LocalDateTime

data class PaymentProcessData(
    val id: String,
    val amount: Double,
    val currency: String = "CLP",
    val savedAt: LocalDateTime,
    val reference: String? = null
)