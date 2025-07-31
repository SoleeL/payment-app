package com.soleel.paymentapp.core.model.intentsale

import kotlinx.serialization.Serializable

@Serializable
enum class IntentSaleStatusEnum {
    SUCCESS,
    CANCELLED,
    ERROR
}