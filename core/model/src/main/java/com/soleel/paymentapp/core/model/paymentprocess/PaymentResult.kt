package com.soleel.paymentapp.core.model.paymentprocess

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PaymentResult(
    val isSuccess: Boolean,
    val message: String? = null,
    val failedStep: String? = null,
    val paymentId: String? = null
) : Parcelable