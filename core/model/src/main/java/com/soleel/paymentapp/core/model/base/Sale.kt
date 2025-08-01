package com.soleel.paymentapp.core.model.base

import java.time.LocalDateTime
import java.util.UUID


data class Sale(
    val id: UUID,
    val paymentId: UUID,

    // README: El monto total no es lo mismo que el total a pagar
    val subtotal: Int, // Calculadora o externo
//    val tip: Int, // Sumar
    val debitCashback: Int?, // Sumar

    val cashChangeSelected: Int? = null,

    val source: String,
    val versionApp: String,

    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    //    val synchronization: SynchronizationEnum // IMPORTANT: para reintento de registro a servicio

    // README: metadata relativa al terminal... no es necesario en local, ya que el terminal ya la
// tiene, por otra parte, al registrar la venta en el servicio se debe adjuntar esta data de serial
// number comercio y otros para que el servicio si registre eso, para luego lograr la sincronizacion
)