package com.soleel.paymentapp.core.model.intentsale

import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import kotlinx.serialization.Serializable

@Serializable
data class IntentSaleRequestExternal(
    val commerceId: String,
    val totalAmount: Int,
    val paymentMethod: Int = -1,
    val cashChange: Int = -1,
    val creditInstalments: Int = -1,
    val debitChange: Int = -1,
)

@Serializable
data class IntentSaleRequestInternal(
    val commerceId: String,
    val totalAmount: Double,
    val paymentMethod: PaymentMethodEnum?,
    val cashChange: Int?,
    val creditInstalments: Int?,
    val debitChange: Int?,
)