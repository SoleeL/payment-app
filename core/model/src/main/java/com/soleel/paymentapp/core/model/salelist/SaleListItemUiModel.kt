package com.soleel.paymentapp.core.model.salelist

import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import java.time.LocalDateTime
import java.util.UUID

data class SaleListItemUiModel(
    val saleId: UUID,
    val amount: Int,
    val createdAt: LocalDateTime,
    val paymentMethod: PaymentMethodEnum,
    val sequenceNumber: String
)