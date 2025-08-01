package com.soleel.paymentapp.data.sale.util

import com.soleel.paymentapp.core.common.dateconversion.toLocalDateTimeUTC
import com.soleel.paymentapp.core.database.entity.SaleEntity
import com.soleel.paymentapp.core.model.base.Sale
import java.util.UUID

fun SaleEntity.toModel() = Sale(
    id = UUID.fromString(id),
    paymentId = UUID.fromString(paymentId),
    subtotal = subtotal,
    tip = tip,
    debitCashback = debitCashback,
    cashChangeSelected = cashChangeSelected,
    source = source,
    versionApp = versionApp,
    createdAt = createdAt.toLocalDateTimeUTC(),
    updatedAt = updatedAt.toLocalDateTimeUTC()
)