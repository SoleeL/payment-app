package com.soleel.paymentapp.core.model.paymentprocess

import java.time.LocalDateTime

data class ConfirmationPaymentProcessData(
    val isConfirmed: Boolean,
    val confirmationCode: String? = null,
    val timestamp: LocalDateTime? = null,
    val message: String? = null
)