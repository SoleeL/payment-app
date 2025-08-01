package com.soleel.paymentapp.data.payment.util

import com.soleel.paymentapp.core.common.dateconversion.toEpochMillisUTC
import com.soleel.paymentapp.core.database.entity.PaymentEntity
import com.soleel.paymentapp.core.model.base.Payment

fun Payment.toEntity() = PaymentEntity(
    id = id.toString(),
    method = method.ordinal,
    amount = amount,
    instalments = instalments,
    applicationLabel = applicationLabel,
    aid = aid,
    last4 = last4,
    sequenceNumber = sequenceNumber,
    authCode = authCode,
    createdAt = createdAt.toEpochMillisUTC(),
    updatedAt = updatedAt.toEpochMillisUTC()
)