package com.soleel.paymentapp.feature.salesprocess.payment.utils

sealed interface ReadingUiState {
    data object Reading : ReadingUiState
    data object Success : ReadingUiState
    data class Failure(val errorCode: String?, val errorMessage: String?) : ReadingUiState
}