package com.soleel.paymentapp.core.model.outcomeprocess

data class RecordingSaleProcessData(
    val remoteSaleId: String,
    val confirmationCode: String?,
    val syncedAt: Long
)