package com.soleel.paymentapp.core.model.intentsale

import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import kotlinx.serialization.Serializable

@Serializable
data class IntentSaleRequestExternal(
    val commerceId: String,
    val totalAmount: Int,
    val paymentMethod: Int = -1, // -1 solicitar metodo de pago, 0 nada (el preferido??), 0 < x < 4 usar el que corresponda
    val cashChange: Int = -1, // -1 solicitar vuelto efectivo, 0 omitir vuelto, totalAmount < x monto a considerar
    val creditInstalments: Int = -1, // -1 solicitar cuotas, 0 sin cuotas, 0 < x < 13 cantidad a considerar
    val debitChange: Int = -1, // -1 solicitar vuelto debito, 0 omitir vuelto, debe coincidir con los vueltos disponibles
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