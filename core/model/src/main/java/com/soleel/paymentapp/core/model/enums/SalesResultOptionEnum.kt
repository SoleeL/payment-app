package com.soleel.paymentapp.core.model.enums

import androidx.annotation.DrawableRes
import com.soleel.paymentapp.core.ui.R

enum class SalesResultOptionEnum(
    @DrawableRes val icon: Int,
    val displayName: String,
    val displayDescription: String = "",
    val highlight: Boolean = false,
    val isVoucher: Boolean = false,
    val isRetry: Boolean = false
) {
    PRINT(
        R.drawable.ic_print,
        "Imprimir",
        "Imprime el comprobante y daselo al cliente",
        isVoucher = true
    ),
    EMAIL(
        R.drawable.ic_email,
        "Enviar a correo",
        "Envía el comprobante al correo del cliente",
        isVoucher = true
    ),
    QR(
        R.drawable.ic_qr,
        "Escanear QR",
        "Genera un código QR y permite que el cliente lo escanee",
        isVoucher = true
    ),
    RETRY_STORE_SALE(
        R.drawable.ic_sale_retry,
        "Reintentar almacenar y registrar venta",
        highlight = true,
        isRetry = true
    ),
    RETRY_REGISTER_SALE(
        R.drawable.ic_sale_retry,
        "Reintentar registrar venta",
        highlight = true,
        isRetry = true
    ),
    FINISH_SALE(
        R.drawable.ic_sale_finished,
        "Finalizar venta",
        highlight = true
    )
}