package com.soleel.paymentapp.feature.salesprocess.payment.contactlessreading

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import com.soleel.paymentapp.domain.reading.IContactlessReadingUseCase
import com.soleel.paymentapp.feature.salesprocess.payment.utils.ReadingErrorType
import com.soleel.paymentapp.feature.salesprocess.payment.utils.ReadingStepUiState
import com.soleel.paymentapp.feature.salesprocess.payment.utils.ReadingUiState
import com.soleel.paymentapp.feature.salesprocess.payment.utils.getErrorType
import com.soleel.paymentapp.feature.salesprocess.payment.utils.mapReadDataToUiState
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


@HiltViewModel
open class ContactlessReadingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val contactlessReadingUseCase: IContactlessReadingUseCase
) : ViewModel() {

    private val _contactlessReadingUiState: Flow<ReadingUiState> = contactlessReadingUseCase()
        .asResult()
        .map(transform = { mapReadDataToUiState(it) })

    private val _contactlessReadingStepUiState: MutableStateFlow<ReadingStepUiState> =
        MutableStateFlow<ReadingStepUiState>(ReadingStepUiState.Active)

    val contactlessReadingUiState: StateFlow<ReadingUiState> = _contactlessReadingUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ReadingUiState.Reading
        )

    fun startContactlessReading(
        withOtherReadingInterface: () -> Unit,
        onVerificationMethod: () -> Unit,
        onFailedPayment: (paymentResult: PaymentResult) -> Unit
    ) {
        viewModelScope.launch {
            _contactlessReadingStepUiState.value = ReadingStepUiState.Active

            val contactlessReadingResult: ReadingUiState =
                contactlessReadingUiState
                    .filter(predicate = { it !is ReadingUiState.Reading })
                    .first()

            if (contactlessReadingResult !is ReadingUiState.Success) {
                val failure = contactlessReadingResult as? ReadingUiState.Failure

                val errorType = failure?.getErrorType()

                delay(1000)

                when (errorType) {
                    ReadingErrorType.InterfaceFallback -> {
                        withOtherReadingInterface()
                    }

                    ReadingErrorType.InvalidCard -> {
                        onFailedPayment(
                            PaymentResult(
                                isSuccess = false,
                                message = failure.errorMessage,
                                failedStep = "READING"
                            )
                        )
                    }

                    ReadingErrorType.PaymentRejected,
                    is ReadingErrorType.Other, null -> {
                        onFailedPayment(
                            PaymentResult(
                                isSuccess = false,
                                message = failure?.errorMessage ?: "Error desconocido",
                                failedStep = "READING"
                            )
                        )
                    }
                }

                return@launch
            }

            _contactlessReadingStepUiState.value = ReadingStepUiState.Finalized

            delay(1000)

            onVerificationMethod()
        }
    }
}