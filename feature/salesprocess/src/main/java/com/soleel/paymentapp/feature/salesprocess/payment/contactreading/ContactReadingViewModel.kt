package com.soleel.paymentapp.feature.salesprocess.payment.contactreading

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import com.soleel.paymentapp.domain.reading.IContactReadingUseCase
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
open class ContactReadingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val contactReadingUseCase: IContactReadingUseCase
) : ViewModel() {

    private val _contactReadingUiState: Flow<ReadingUiState> = contactReadingUseCase()
        .asResult()
        .map(transform = { mapReadDataToUiState(it) })

    val contactReadingUiState: StateFlow<ReadingUiState> = _contactReadingUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = ReadingUiState.Reading
    )

    private val _contactReadingStepUiState: MutableStateFlow<ReadingStepUiState> =
        MutableStateFlow<ReadingStepUiState>(ReadingStepUiState.Active)

    fun startContactReading(
        onFailedPayment: (errorCode: String, errorMessage: String) -> Unit,
        onVerificationMethod: (brand: String, last4: Int) -> Unit
    ) {
        viewModelScope.launch {
            _contactReadingStepUiState.value = ReadingStepUiState.Active

            val contactReadingResult: ReadingUiState = contactReadingUiState
                .filter(predicate = { it !is ReadingUiState.Reading })
                .first()

            if (contactReadingResult !is ReadingUiState.Success) {

                delay(1000)

                val failure = contactReadingResult as? ReadingUiState.Failure

                val errorType = failure?.getErrorType()

                when (errorType) {
                    ReadingErrorType.InterfaceFallback,
                    ReadingErrorType.InvalidCard -> {
                        onFailedPayment(
                            failure.errorCode ?: "UNKNOWN CODE",
                            failure.errorMessage ?: "Error desconocido"
                        )
                    }

                    ReadingErrorType.PaymentRejected,
                    is ReadingErrorType.Other, null -> {
                        onFailedPayment(
                            failure?.errorCode ?: "UNKNOWN CODE",
                            failure?.errorMessage ?: "Error desconocido"
                        )
                    }
                }

                return@launch
            }

            _contactReadingStepUiState.value = ReadingStepUiState.Finalized

            delay(1000)

            onVerificationMethod(
                contactReadingResult.data.cardBrand,
                contactReadingResult.data.cardNumber.takeLast(4).toInt()
            )
        }
    }
}