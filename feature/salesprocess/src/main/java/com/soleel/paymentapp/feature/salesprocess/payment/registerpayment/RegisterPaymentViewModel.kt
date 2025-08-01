package com.soleel.paymentapp.feature.salesprocess.payment.registerpayment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.Result
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.base.Payment
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.core.model.paymentprocess.ConfirmationPaymentProcessData
import com.soleel.paymentapp.core.model.paymentprocess.ValidationPaymentProcessData
import com.soleel.paymentapp.domain.payment.IRequestConfirmingPaymentUseCase
import com.soleel.paymentapp.domain.payment.IRequestValidationPaymentUseCase
import com.soleel.paymentapp.domain.payment.IStorePaymentUseCase
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
    data class Failure(val exception: Throwable) : PaymentProcessUiState<Nothing>() {
        val errorCode: String? get() = exception::class.simpleName
        val errorMessage: String? get() = exception.localizedMessage
    }
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
    private val storePaymentUseCase: IStorePaymentUseCase
) : ViewModel() {

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

            is Result.Error -> PaymentProcessUiState.Failure(validationPaymentProcessResult.exception)
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

            is Result.Error -> PaymentProcessUiState.Failure(confirmationPaymentProcessResult.exception)
        }
    }

    private val confirmingPaymentProcessUiState: StateFlow<PaymentProcessUiState<ConfirmationPaymentProcessData>> =
        _confirmingPaymentProcessUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = PaymentProcessUiState.Loading
            )

    private fun storingPaymentProcessUiState(
        method: PaymentMethodEnum,
        amount: Int,
        instalments: Int?,
        applicationLabel: String,
        aid: String,
        last4: String,
        sequenceNumber: String,
        authCode: String
    ): StateFlow<PaymentProcessUiState<Payment>> {
        return storePaymentUseCase(
            method,
            amount,
            instalments,
            applicationLabel,
            aid,
            last4,
            sequenceNumber,
            authCode
        )
            .asResult()
            .map { getPaymentProcessData(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = PaymentProcessUiState.Loading
            )
    }

    private fun getPaymentProcessData(storePaymentProcessResult: Result<Payment>): PaymentProcessUiState<Payment> {
        return when (storePaymentProcessResult) {
            Result.Loading -> PaymentProcessUiState.Loading
            is Result.Success<Payment> -> {
                PaymentProcessUiState.Success(storePaymentProcessResult.data)
            }

            is Result.Error -> PaymentProcessUiState.Failure(storePaymentProcessResult.exception)
        }
    }

    private val _paymentStepUiState: MutableStateFlow<PaymentStepUiState> =
        MutableStateFlow<PaymentStepUiState>(PaymentStepUiState.Validating)
    val paymentStepUiState: StateFlow<PaymentStepUiState> = _paymentStepUiState

    fun startPaymentProcess(
        navigateToFailedPayment: (errorCode: String, errorMessage: String) -> Unit,
        navigateToRegisterSale: (uuidPayment: String) -> Unit,
        method: PaymentMethodEnum,
        amount: Int,
        instalments: Int?,
        applicationLabel: String,
        aid: String,
        last4: String
    ) {
        viewModelScope.launch {
            _paymentStepUiState.value = PaymentStepUiState.Validating

            val validatingPaymentProcessResult: PaymentProcessUiState<ValidationPaymentProcessData> =
                validatingPaymentProcessUiState
                    // README: ESTO FILTRA, TAL QUE LOS ESTADOS QUE PUEDEN SALIR SON Success O Failure
                    .filter(predicate = { it !is PaymentProcessUiState.Loading })
                    .first()

            if (validatingPaymentProcessResult !is PaymentProcessUiState.Success) {
                val failure = validatingPaymentProcessResult as PaymentProcessUiState.Failure
                navigateToFailedPayment(
                    failure.errorCode ?: "UNKNOWN CODE",
                    failure.errorMessage ?: "Error desconocido"
                )
                return@launch
            }

            _paymentStepUiState.value = PaymentStepUiState.Confirming

            val confirmingPaymentProcessResult: PaymentProcessUiState<ConfirmationPaymentProcessData> =
                confirmingPaymentProcessUiState
                    .filter(predicate = { it !is PaymentProcessUiState.Loading })
                    .first()

            if (confirmingPaymentProcessResult !is PaymentProcessUiState.Success) {
                val failure = confirmingPaymentProcessResult as PaymentProcessUiState.Failure

                navigateToFailedPayment(
                    failure.errorCode ?: "UNKNOWN CODE",
                    failure.errorMessage ?: "Error desconocido"
                )

                return@launch
            }

            _paymentStepUiState.value = PaymentStepUiState.Saving

            val storingPaymentProcessResult: PaymentProcessUiState<Payment> =
                storingPaymentProcessUiState(
                    method = method,
                    amount = amount,
                    instalments = instalments,
                    applicationLabel = applicationLabel,
                    aid = aid,
                    last4 = last4,
                    sequenceNumber = validatingPaymentProcessResult.data.sequenceNumber!!, // README: HIPOTETICO
                    authCode = validatingPaymentProcessResult.data.authCode!! // README: HIPOTETICO
                )
                    .filter(predicate = { it !is PaymentProcessUiState.Loading })
                    .first()

            if (storingPaymentProcessResult !is PaymentProcessUiState.Success) {
                val failure = storingPaymentProcessResult as PaymentProcessUiState.Failure
                navigateToFailedPayment(
                    failure.errorCode ?: "UNKNOWN CODE",
                    failure.errorMessage ?: "Error desconocido"
                )
                return@launch
            }

            _paymentStepUiState.value = PaymentStepUiState.Done

            delay(1000)

            navigateToRegisterSale(storingPaymentProcessResult.data.id.toString())
        }
    }
}