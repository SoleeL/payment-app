package com.soleel.paymentapp.feature.salesprocess.payment.utils

import com.soleel.paymentapp.domain.reading.InterfaceFallbackException
import com.soleel.paymentapp.domain.reading.InvalidCardException

fun ReadingUiState.Failure.getErrorType(): ReadingErrorType {
    return when (errorCode) {
        InterfaceFallbackException::class.simpleName -> ReadingErrorType.InterfaceFallback
        InvalidCardException::class.simpleName -> ReadingErrorType.InvalidCard
        else -> ReadingErrorType.Other(errorMessage)
    }
}
