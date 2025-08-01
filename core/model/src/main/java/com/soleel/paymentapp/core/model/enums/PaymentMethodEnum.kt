package com.soleel.paymentapp.core.model.enums

import androidx.annotation.DrawableRes
import com.soleel.paymentapp.core.ui.R

enum class PaymentMethodEnum(
    val id: Int,
    val value: String,
    @DrawableRes val icon: Int
) {

    CASH(id = 1, value = "Efectivo", icon = R.drawable.ic_payment_method_cash),
    CREDIT(id = 2, value = "Credito", icon = R.drawable.ic_payment_method_credit),
    DEBIT(id = 3, value = "Debito", icon =  R.drawable.ic_payment_method_debit);

    companion object {
        fun fromId(id: Int?): PaymentMethodEnum? {
            return entries.find(predicate = { it.id == id })
        }
    }
}