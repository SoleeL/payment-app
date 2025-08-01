package com.soleel.paymentapp.feature.salesprocess.payment.utils

import com.soleel.paymentapp.core.model.readingprocess.InterfaceReadData
import com.soleel.paymentapp.core.common.result.Result

fun mapReadDataToUiState(result: Result<InterfaceReadData>): ReadingUiState {
    return when (result) {
        Result.Loading -> ReadingUiState.Reading
        is Result.Success -> {
            val data = result.data
            if (data.isValid) {
                ReadingUiState.Success(data)
            } else {
                ReadingUiState.Failure(
                    errorCode = "INVALID_DATA",
                    errorMessage = "Datos inválidos de lectura"
                )
            }
        }

        is Result.Error -> ReadingUiState.Failure(
            errorCode = result.exception::class.simpleName,
            errorMessage = result.exception.localizedMessage ?: "Error desconocido"
        )
    }
}