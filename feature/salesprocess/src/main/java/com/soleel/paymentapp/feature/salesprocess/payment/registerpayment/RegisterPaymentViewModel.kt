package com.soleel.paymentapp.feature.salesprocess.payment.registerpayment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.Result
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.paymentprocess.ConfirmationPaymentProcessData
import com.soleel.paymentapp.core.model.paymentprocess.PaymentProcessData
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import com.soleel.paymentapp.core.model.paymentprocess.ValidationPaymentProcessData
import com.soleel.paymentapp.domain.payment.IRequestConfirmingPaymentUseCase
import com.soleel.paymentapp.domain.payment.IRequestValidationPaymentUseCase
import com.soleel.paymentapp.domain.payment.ISavePaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentProcessUiState<out T> {
    data object Loading : PaymentProcessUiState<Nothing>()
    data class Success<T>(val data: T) : PaymentProcessUiState<T>()
    data class Failure(val errorMessage: String? = null) : PaymentProcessUiState<Nothing>()
}

sealed interface PaymentStepUiState {
    data object Validating : PaymentStepUiState
    data object Confirming : PaymentStepUiState
    data object Saving : PaymentStepUiState
    data object Done : PaymentStepUiState
}

@HiltViewModel
open class RegisterPaymentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,

    private val requestValidationPaymentUseCase: IRequestValidationPaymentUseCase,
    private val requestConfirmationPaymentUseCase: IRequestConfirmingPaymentUseCase,
    private val savePaymentUseCase: ISavePaymentUseCase
) : ViewModel() {
    val sale: Sale = savedStateHandle.get<Sale>("sale") ?: Sale(calculatorTotal = 0f)

    private val _validatingPaymentProcessUiState: Flow<PaymentProcessUiState<ValidationPaymentProcessData>> =
        requestValidationPaymentUseCase()
            .asResult()
            .map(transform = { getValidationPaymentProcessData(it) })

    private fun getValidationPaymentProcessData(validationPaymentProcessResult: Result<ValidationPaymentProcessData>): PaymentProcessUiState<ValidationPaymentProcessData> {
        return when (validationPaymentProcessResult) {
            Result.Loading -> PaymentProcessUiState.Loading
            is Result.Success<ValidationPaymentProcessData> -> {
                PaymentProcessUiState.Success(validationPaymentProcessResult.data)
            }

            is Result.Error -> PaymentProcessUiState.Failure(validationPaymentProcessResult.exception.message)
        }
    }

    private val validatingPaymentProcessUiState: StateFlow<PaymentProcessUiState<ValidationPaymentProcessData>> =
        _validatingPaymentProcessUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = PaymentProcessUiState.Loading
            )

    private val _confirmingPaymentProcessUiState: Flow<PaymentProcessUiState<ConfirmationPaymentProcessData>> =
        requestConfirmationPaymentUseCase()
            .asResult()
            .map(transform = { this.getConfirmationPaymentProcessData(it) })

    private fun getConfirmationPaymentProcessData(confirmationPaymentProcessResult: Result<ConfirmationPaymentProcessData>): PaymentProcessUiState<ConfirmationPaymentProcessData> {
        return when (confirmationPaymentProcessResult) {
            Result.Loading -> PaymentProcessUiState.Loading
            is Result.Success<ConfirmationPaymentProcessData> -> {
                PaymentProcessUiState.Success(confirmationPaymentProcessResult.data)
            }

            is Result.Error -> PaymentProcessUiState.Failure(confirmationPaymentProcessResult.exception.message)
        }
    }

    private val confirmingPaymentProcessUiState: StateFlow<PaymentProcessUiState<ConfirmationPaymentProcessData>> =
        _confirmingPaymentProcessUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = PaymentProcessUiState.Loading
            )

    private val _savingPaymentProcessUiState: Flow<PaymentProcessUiState<PaymentProcessData>> =
        savePaymentUseCase()
            .asResult()
            .map(transform = { this.getPaymentProcessData(it) })

    private fun getPaymentProcessData(savePaymentProcessResult: Result<PaymentProcessData>): PaymentProcessUiState<PaymentProcessData> {
        return when (savePaymentProcessResult) {
            Result.Loading -> PaymentProcessUiState.Loading
            is Result.Success<PaymentProcessData> -> {
                PaymentProcessUiState.Success(savePaymentProcessResult.data)
            }

            is Result.Error -> PaymentProcessUiState.Failure(savePaymentProcessResult.exception.message)
        }
    }

    private val savingPaymentProcessUiState: StateFlow<PaymentProcessUiState<PaymentProcessData>> =
        _savingPaymentProcessUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = PaymentProcessUiState.Loading
            )

    private val _paymentStepUiState: MutableStateFlow<PaymentStepUiState> =
        MutableStateFlow<PaymentStepUiState>(PaymentStepUiState.Validating)
    val paymentStepUiState: StateFlow<PaymentStepUiState> = _paymentStepUiState

    fun startPaymentProcess(onPaymentResult: (sale: Sale, paymentResult: PaymentResult) -> Unit) {
        viewModelScope.launch {
            _paymentStepUiState.value = PaymentStepUiState.Validating

            val validatingPaymentProcessResult: PaymentProcessUiState<ValidationPaymentProcessData> =
                validatingPaymentProcessUiState
                    // README: ESTO FILTRA, TAL QUE LOS ESTADOS QUE PUEDEN SALIR SON Success O Failure
                    .filter(predicate = { it !is PaymentProcessUiState.Loading })
                    .first()

            if (validatingPaymentProcessResult !is PaymentProcessUiState.Success) {
                val errorMessage: String? =
                    (validatingPaymentProcessResult as? PaymentProcessUiState.Failure)?.errorMessage

//                val validPins = listOf("1234", "0000", "9999") // Lista de PINs válidos
//                    ReadingErrorType.PaymentRejected -> { TODO: IMPLEMENTAR RETORNO DE FALLAS
//                        onPaymentResult(
//                            PaymentResult(
//                                isSuccess = false,
//                                message = failure.errorMessage,
//                                failedStep = "READING"
//                            )
//                        )
//                    }

                onPaymentResult(
                    sale,
                    PaymentResult(
                        isSuccess = false,
                        message = errorMessage,
                        failedStep = "VALIDATING"
                    )
                )

                return@launch
            }

            _paymentStepUiState.value = PaymentStepUiState.Confirming

            val confirmingPaymentProcessResult: PaymentProcessUiState<ConfirmationPaymentProcessData> =
                confirmingPaymentProcessUiState
                    .filter(predicate = { it !is PaymentProcessUiState.Loading })
                    .first()

            if (confirmingPaymentProcessResult !is PaymentProcessUiState.Success) {
                val errorMessage: String? =
                    (confirmingPaymentProcessResult as? PaymentProcessUiState.Failure)?.errorMessage

                onPaymentResult(
                    sale,
                    PaymentResult(
                        isSuccess = false,
                        message = errorMessage,
                        failedStep = "CONFIRMING"
                    )
                )

                return@launch
            }

            _paymentStepUiState.value = PaymentStepUiState.Saving

            val savingPaymentProcessResult: PaymentProcessUiState<PaymentProcessData> =
                savingPaymentProcessUiState
                    .filter(predicate = { it !is PaymentProcessUiState.Loading })
                    .first()

            if (savingPaymentProcessResult !is PaymentProcessUiState.Success) {
                val errorMessage: String? =
                    (savingPaymentProcessResult as? PaymentProcessUiState.Failure)?.errorMessage
                onPaymentResult(
                    sale,
                    PaymentResult(
                        isSuccess = false,
                        message = errorMessage,
                        failedStep = "SAVING"
                    )
                )
                return@launch
            }

            val paymentData: PaymentProcessData = savingPaymentProcessResult.data

            _paymentStepUiState.value = PaymentStepUiState.Done

            delay(1000)

            onPaymentResult(
                sale,
                PaymentResult(
                    isSuccess = true,
                    paymentId = paymentData.id
                )
            )
        }
    }
}