
package com.soleel.paymentapp.data.payment.util

import com.soleel.paymentapp.core.common.dateconversion.toLocalDateTimeUTC
import com.soleel.paymentapp.core.database.entity.PaymentEntity
import com.soleel.paymentapp.core.model.base.Payment
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import java.util.UUID

fun PaymentEntity.toModel() = Payment(
    id = UUID.fromString(id),
    method = PaymentMethodEnum.entries[method],
    amount = amount,
    instalments = instalments,
    applicationLabel = applicationLabel,
    aid = aid,
    last4 = last4,
    sequenceNumber = sequenceNumber,
    authCode = authCode,
    createdAt = createdAt.toLocalDateTimeUTC(),
    updatedAt = updatedAt.toLocalDateTimeUTC()
)