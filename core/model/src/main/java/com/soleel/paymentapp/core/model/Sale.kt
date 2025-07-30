package com.soleel.paymentapp.core.model

import android.os.Parcelable
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Sale(
    val calculatorTotal: Float,

    val tipTotal: Float? = null,
    val paymentMethodSelected: PaymentMethodEnum? = null,
    val cashChangeSelected: Float? = null,
    val creditInstalmentsSelected: Int? = null,
    val debitChangeSelected: Float? = null
) : Parcelable