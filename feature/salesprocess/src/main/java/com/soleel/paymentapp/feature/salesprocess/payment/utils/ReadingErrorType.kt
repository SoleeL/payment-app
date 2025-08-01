package com.soleel.paymentapp.feature.salesprocess.payment.utils

sealed class ReadingErrorType {
    data object InterfaceFallback : ReadingErrorType()
    data object InvalidCard : ReadingErrorType()
    data object PaymentRejected : ReadingErrorType()
    data class Other(val message: String?) : ReadingErrorType()
}
