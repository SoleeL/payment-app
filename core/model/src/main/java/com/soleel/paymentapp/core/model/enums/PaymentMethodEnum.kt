package com.soleel.paymentapp.core.model.enums

import androidx.annotation.DrawableRes
import com.soleel.paymentapp.core.ui.R

enum class PaymentMethodEnum(
    // README: No modificar este atributo, hacerlo corrompe la lectura/almacenamiento de la base la DB
    val id: Int,
    @DrawableRes val icon: Int,
    val displayName: String,
    val displayDescription: String,
) {

    CASH(
        id = 1,
        icon = R.drawable.ic_payment_method_cash,
        displayName = "Efectivo",
        displayDescription = "Pago realizado en moneda fisica"
    ),
    CREDIT(
        id = 2,
        icon = R.drawable.ic_payment_method_credit,
        displayName = "Credito",
        displayDescription = "Pago con tarjeta que permite cuotas"
    ),
    DEBIT(
        id = 3,
        icon = R.drawable.ic_payment_method_debit,
        displayName = "Debito",
        displayDescription = "Pago con tarjeta que permite vuelto"
    ),
    QR(
        id = 4,
        icon = R.drawable.ic_qr,
        displayName = "Codigo QR",
        displayDescription = "Pago por scaneo de QR en app bancaria"
    ),
    BIOMETRIC_AUTHENTICATION(
        id = 5,
        icon = R.drawable.ic_biometric,
        displayName = "Autenticacion biometrica",
        displayDescription = "Pago por deteccion facial"
    );

    companion object {
        fun fromId(id: Int?): PaymentMethodEnum? {
            return entries.find(predicate = { it.id == id })
        }
    }
}