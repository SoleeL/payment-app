package com.soleel.paymentapp.feature.salesprocess.payment.utils

import com.soleel.paymentapp.domain.reading.InterfaceFallbackException
import com.soleel.paymentapp.domain.reading.InvalidCardException
import com.soleel.paymentapp.domain.reading.PaymentRejectedException

fun ReadingUiState.Failure.getErrorType(): ReadingErrorType {
    return when (errorCode) {
        InterfaceFallbackException::class.simpleName -> ReadingErrorType.InterfaceFallback
        InvalidCardException::class.simpleName -> ReadingErrorType.InvalidCard
        PaymentRejectedException::class.simpleName -> ReadingErrorType.PaymentRejected
        else -> ReadingErrorType.Other(errorMessage)
    }
}
