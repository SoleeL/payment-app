package com.soleel.paymentapp.core.model.enums

import androidx.annotation.DrawableRes
import com.soleel.paymentapp.core.ui.R

enum class SalesResultOptionEnum(
    // README: No modificar este atributo, hacerlo corrompe la lectura/almacenamiento de la base la DB
    val id: Int,
    @DrawableRes val icon: Int,
    val displayName: String,
    val displayDescription: String = "",
    val highlight: Boolean = false,
) {
    FINISH_SALE(
        id = 11,
        R.drawable.ic_sale_finished,
        "Finalizar venta",
        highlight = true
    ),
    RETRY_STORE_SALE(
        id = 12,
        R.drawable.ic_sale_retry,
        "Reintentar almacenar y registrar venta",
        highlight = true,
    ),
    RETRY_REGISTER_SALE(
        id = 13,
        R.drawable.ic_sale_retry,
        "Reintentar registrar venta",
        highlight = true,
    ),

    PRINT(
        id = 21,
        R.drawable.ic_print,
        "Imprimir",
        "Imprime el comprobante y daselo al cliente",
    ),
    EMAIL(
        id = 22,
        R.drawable.ic_email,
        "Enviar a correo",
        "Envía el comprobante al correo del cliente",
    ),
    QR(
        id = 23,
        R.drawable.ic_qr,
        "Escanear QR",
        "Genera un código QR y permite que el cliente lo escanee",
    )
}