package com.soleel.paymentapp.core.model

import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum

data class Sale(
    val calculatorTotal: Float,

    val tipTotal: Float? = null,
    val paymentMethodSelected: PaymentMethodEnum? = null,
    val cashChangeSelected: Float? = null,
    val creditInstalmentsSelected: Int? = null,
    val debitChangeSelected: Float? = null
)