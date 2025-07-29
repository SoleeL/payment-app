package com.soleel.paymentapp.core.model

import com.soleel.paymentapp.core.ui.R

enum class PaymentMethodEnum(
    val id: Int,
    val value: String,
    val icon: Int
) {

    CASH(id = 1, value = "Efectivo", icon = R.drawable.ic_payment_method_cash),
    CREDIT(id = 2, value = "Credito", icon = R.drawable.ic_payment_method_credit),
    DEBIT(id = 3, value = "Debito", icon =  R.drawable.ic_payment_method_debit);

    companion object {
        fun fromId(id: Int): PaymentMethodEnum {
            val paymentMethodEnum: PaymentMethodEnum? = entries.find(predicate = { it.id == id })
            return paymentMethodEnum ?: CASH
        }
    }
}