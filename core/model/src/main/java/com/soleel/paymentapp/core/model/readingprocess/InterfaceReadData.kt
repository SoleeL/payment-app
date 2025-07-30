package com.soleel.paymentapp.core.model.readingprocess

data class InterfaceReadData(
    val cardNumber: String,
    val cardHolderName: String?,
    val expirationDate: String?,
    val isValid: Boolean,
    val additionalInfo: Map<String, Any>? = null
)