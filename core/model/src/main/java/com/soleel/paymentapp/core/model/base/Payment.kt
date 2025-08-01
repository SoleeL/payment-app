package com.soleel.paymentapp.core.model.base

import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import java.time.LocalDateTime
import java.util.UUID


data class Payment(
    val id: UUID,

    val method: PaymentMethodEnum,
    val amount: Int,
    val instalments: Int?,

    val applicationLabel: String,
    val aid: String,
    val last4: String,

    val sequenceNumber: String,
    val authCode: String,

    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime

//    val codeCurrency: String,
//    val acquirer: String
    // val avoid: String
)