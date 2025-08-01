package com.soleel.paymentapp.core.model.outcomeprocess

import com.soleel.paymentapp.core.model.base.Sale
import java.util.UUID

data class StoreSaleProcessData(
    val saleUUID: UUID,
    val sale: Sale,
    val timestamp: Long
)