package com.soleel.paymentapp.core.model.outcomeprocess

import java.util.UUID

data class RegisterSaleResult(
    val localSaleId: UUID,
    val remoteSaleId: String,
    val isSuccess: Boolean,
    val message: String? = null
)
