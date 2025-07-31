package com.soleel.paymentapp.core.model.intentsale

import kotlinx.serialization.Serializable


@Serializable
data class IntentSaleResultExternal(
    val saleId: String = "",
    val status: Int,
    val message: String = "",
    val errorCode: String = "",
)

@Serializable
data class IntentSaleResultInternal(
    val saleId: String? = null,
    val status: IntentSaleStatusEnum,
    val message: String? = null,
    val errorCode: String? = null,
)

fun IntentSaleResultInternal.toExternal(): IntentSaleResultExternal {
    return IntentSaleResultExternal(
        saleId = saleId ?: "",
        status = status.ordinal,
        message = message ?: "",
        errorCode = errorCode ?: ""
    )
}