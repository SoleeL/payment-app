package com.soleel.paymentapp.core.model.paymentprocess

data class ValidationPaymentProcessData(
    val isValid: Boolean,
    val message: String? = null,
    val authCode: String? = null,
    val sequenceNumber: String? = null
)