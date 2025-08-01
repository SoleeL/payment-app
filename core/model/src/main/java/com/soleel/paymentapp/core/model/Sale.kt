package com.soleel.paymentapp.core.model

import android.os.Parcelable
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Sale(
    val totalAmount: Int,

//    val tipTotal: Int? = null,
    val paymentMethodSelected: PaymentMethodEnum? = null,
    val cashChangeSelected: Int? = null,
    val creditInstalmentsSelected: Int? = null,
    val debitChangeSelected: Int? = null
) : Parcelable