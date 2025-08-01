package com.soleel.paymentapp.feature.salesprocess.payment.utils

sealed interface ReadingStepUiState {
    data object Active : ReadingStepUiState
    data object Finalized : ReadingStepUiState
}