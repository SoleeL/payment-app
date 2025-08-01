package com.soleel.paymentapp.data.sale.util

import com.soleel.paymentapp.core.common.dateconversion.toEpochMillisUTC
import com.soleel.paymentapp.core.database.entity.SaleEntity
import com.soleel.paymentapp.core.model.base.Sale

fun Sale.toEntity() = SaleEntity(
    id = id.toString(),
    paymentId = paymentId.toString(),
    subtotal = subtotal,
//    tip = tip,
    debitCashback = debitCashback,
    cashChangeSelected = cashChangeSelected,
    source = source,
    versionApp = versionApp,
    createdAt = createdAt.toEpochMillisUTC(),
    updatedAt = updatedAt.toEpochMillisUTC()
)