package com.soleel.paymentapp.feature.salesprocess.payment.utils

import com.soleel.paymentapp.core.model.readingprocess.InterfaceReadData

sealed interface ReadingUiState {
    data object Reading : ReadingUiState
    data class Success(val data: InterfaceReadData) : ReadingUiState
    data class Failure(val errorCode: String?, val errorMessage: String?) : ReadingUiState
}