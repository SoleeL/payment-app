package com.soleel.paymentapp.feature.salesprocess.outcome.registersale

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.Result
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.outcomeprocess.RecordingSaleProcessData
import com.soleel.paymentapp.core.model.outcomeprocess.StoreSaleProcessData
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import com.soleel.paymentapp.domain.sale.IRecordingSaleUseCase
import com.soleel.paymentapp.domain.sale.IStoreSaleUseCase
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
    val sale: Sale = savedStateHandle.get<Sale>("sale") ?: Sale(totalAmount = 0)
    val paymentResult: PaymentResult =
        savedStateHandle.get<PaymentResult>("paymentResult") ?: PaymentResult(isSuccess = false)


    private val _storeSaleProcessUiState: Flow<SaleRegisterProcessUiState<StoreSaleProcessData>> =
        storeSaleUseCase()
            .asResult()
            .map(transform = { getStoreSaleProcessData(it) })

    private fun getStoreSaleProcessData(storeSaleProcessDataResult: Result<StoreSaleProcessData>): SaleRegisterProcessUiState<StoreSaleProcessData> {
        return when (storeSaleProcessDataResult) {
            Result.Loading -> SaleRegisterProcessUiState.Loading
            is Result.Success<StoreSaleProcessData> -> {
                SaleRegisterProcessUiState.Success(storeSaleProcessDataResult.data)
            }
            is Result.Error -> SaleRegisterProcessUiState.Failure(storeSaleProcessDataResult.exception)
        }
    }

    private val storeSaleProcessUiState: StateFlow<SaleRegisterProcessUiState<StoreSaleProcessData>> =
        _storeSaleProcessUiState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = SaleRegisterProcessUiState.Loading
            )

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
        navigateToSuccessfulSale: (uuidSale: String) -> Unit
    ) {
        viewModelScope.launch {
            _registerSaleStepUiState.value = SaleRegisterStepUiState.Storing

            val storeSaleProcessResult: SaleRegisterProcessUiState<StoreSaleProcessData> =
                storeSaleProcessUiState
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
                navigateToPendingSale(storeSaleProcessResult.data.saleUUID.toString())
                return@launch
            }

            _registerSaleStepUiState.value = SaleRegisterStepUiState.Done

            delay(1000)

            navigateToSuccessfulSale(storeSaleProcessResult.data.saleUUID.toString())
        }
    }
}