package com.soleel.paymentapp.core.model.readingprocess

data class InterfaceReadData(
    val applicationLabel: String,
    val aid: String,
    val last4: String,
    val expirationDate: String?,
    val isValid: Boolean
)