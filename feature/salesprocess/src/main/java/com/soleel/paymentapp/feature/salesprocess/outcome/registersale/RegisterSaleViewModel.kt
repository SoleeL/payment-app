package com.soleel.paymentapp.feature.salesprocess.outcome.registersale

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.Result
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.base.Payment
import com.soleel.paymentapp.core.model.base.Sale
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.core.model.outcomeprocess.RecordingSaleProcessData
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import com.soleel.paymentapp.domain.sale.IRecordingSaleUseCase
import com.soleel.paymentapp.domain.sale.IStoreSaleUseCase
import com.soleel.paymentapp.feature.salesprocess.payment.registerpayment.PaymentProcessUiState
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
import java.util.UUID
import javax.inject.Inject

sealed class SaleRegisterProcessUiState<out T> {
    data object Loading : SaleRegisterProcessUiState<Nothing>()
    data class Success<T>(val data: T) : SaleRegisterProcessUiState<T>()
    data class Failure(val exception: Throwable) : SaleRegisterProcessUiState<Nothing>() {
        val errorCode: String? get() = exception::class.simpleName
        val errorMessage: String? get() = exception.localizedMessage
    }
}

sealed interface SaleRegisterStepUiState {
    data object Storing : SaleRegisterStepUiState
    data object Recording : SaleRegisterStepUiState
    data object Done : SaleRegisterStepUiState
}

@HiltViewModel
open class RegisterSaleViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,

    private val storeSaleUseCase: IStoreSaleUseCase,
    private val recordingSaleUseCase: IRecordingSaleUseCase,
) : ViewModel() {
    val paymentResult: PaymentResult =
        savedStateHandle.get<PaymentResult>("paymentResult") ?: PaymentResult(isSuccess = false)

    private fun storeSaleProcessUiState(
        paymentUUID: UUID,
        subtotal: Int,
        cashChangeSelected: Int?,
        debitCashback: Int?,
        source: String?,
    ): StateFlow<SaleRegisterProcessUiState<Sale>> {
        return storeSaleUseCase(
            paymentUUID,
            subtotal,
            cashChangeSelected,
            debitCashback,
            source
        )
            .asResult()
            .map { getStoreSaleProcessData(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = SaleRegisterProcessUiState.Loading
            )
    }

    private fun getStoreSaleProcessData(storeSaleProcessDataResult: Result<Sale>): SaleRegisterProcessUiState<Sale> {
        return when (storeSaleProcessDataResult) {
            Result.Loading -> SaleRegisterProcessUiState.Loading
            is Result.Success<Sale> -> {
                SaleRegisterProcessUiState.Success(storeSaleProcessDataResult.data)
            }

            is Result.Error -> SaleRegisterProcessUiState.Failure(storeSaleProcessDataResult.exception)
        }
    }

    private val _recordingSaleProcessUiState: Flow<SaleRegisterProcessUiState<RecordingSaleProcessData>> =
        recordingSaleUseCase()
            .asResult()
            .map(transform = { getRecordingSaleProcessData(it) })

    private fun getRecordingSaleProcessData(recordingSaleProcessDataResult: Result<RecordingSaleProcessData>): SaleRegisterProcessUiState<RecordingSaleProcessData> {
        return when (recordingSaleProcessDataResult) {
            Result.Loading -> SaleRegisterProcessUiState.Loading
            is Result.Success<RecordingSaleProcessData> -> {
                SaleRegisterProcessUiState.Success(recordingSaleProcessDataResult.data)
            }

            is Result.Error -> SaleRegisterProcessUiState.Failure(recordingSaleProcessDataResult.exception)
        }
    }

    private val recordingSaleProcessUiState: StateFlow<SaleRegisterProcessUiState<RecordingSaleProcessData>> =
        _recordingSaleProcessUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = SaleRegisterProcessUiState.Loading
            )

    private val _registerSaleStepUiState: MutableStateFlow<SaleRegisterStepUiState> =
        MutableStateFlow<SaleRegisterStepUiState>(SaleRegisterStepUiState.Storing)
    val registerSaleStepUiState: StateFlow<SaleRegisterStepUiState> = _registerSaleStepUiState

    fun startPaymentProcess(
        navigateToFailedSale: (errorCode: String, errorMessage: String) -> Unit,
        navigateToPendingSale: (uuidSale: String) -> Unit,
        navigateToSuccessfulSale: (uuidSale: String) -> Unit,
        paymentUUID: String,
        subtotal: Int,
        cashChangeSelected: Int?,
        debitCashback: Int?,
        source: String?
    ) {
        viewModelScope.launch {
            _registerSaleStepUiState.value = SaleRegisterStepUiState.Storing

            val storeSaleProcessResult: SaleRegisterProcessUiState<Sale> =
                storeSaleProcessUiState(
                    paymentUUID = UUID.fromString(paymentUUID),
                    subtotal = subtotal,
                    cashChangeSelected = cashChangeSelected,
                    debitCashback = debitCashback,
                    source = source
                )
                    .filter(predicate = { it !is SaleRegisterProcessUiState.Loading })
                    .first()

            if (storeSaleProcessResult !is SaleRegisterProcessUiState.Success) {
                val failure = storeSaleProcessResult as SaleRegisterProcessUiState.Failure
                navigateToFailedSale(
                    failure.errorCode ?: "UNKNOWN CODE",
                    failure.errorMessage ?: "Error desconocido"
                )
                return@launch
            }

            _registerSaleStepUiState.value = SaleRegisterStepUiState.Recording

            val recordingSaleProcessResult: SaleRegisterProcessUiState<RecordingSaleProcessData> =
                recordingSaleProcessUiState
                    .filter(predicate = { it !is SaleRegisterProcessUiState.Loading })
                    .first()

            if (recordingSaleProcessResult !is SaleRegisterProcessUiState.Success) {
                navigateToPendingSale(storeSaleProcessResult.data.id.toString())
                return@launch
            }

            _registerSaleStepUiState.value = SaleRegisterStepUiState.Done

            delay(1000)

            navigateToSuccessfulSale(storeSaleProcessResult.data.id.toString())
        }
    }
}