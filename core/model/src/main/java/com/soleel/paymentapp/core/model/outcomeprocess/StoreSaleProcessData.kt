package com.soleel.paymentapp.core.model.outcomeprocess

import com.soleel.paymentapp.core.model.Sale
import java.util.UUID

data class StoreSaleProcessData(
    val localSaleId: UUID,
    val sale: Sale,
    val timestamp: Long
)