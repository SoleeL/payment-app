package com.soleel.paymentapp.feature.salesprocess.payment.pinpad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.Result
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.pinpadprocess.PinBlockData
import com.soleel.paymentapp.domain.pinpad.IPinBlockRequestUseCase
import com.soleel.paymentapp.domain.pinpad.InvalidKSNException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinpadUiState(
    val pin: String = "",
)

sealed interface PinBlockUiState {
    data object Requesting : PinBlockUiState
    data class Obtained(val data: PinBlockData) : PinBlockUiState
    data class Failure(val errorCode: String?, val errorMessage: String?) : PinBlockUiState
}

sealed interface ConfirmingPinStepUiState {
    data object Pending : ConfirmingPinStepUiState
    data object Confirming : ConfirmingPinStepUiState
}

sealed class PinBlockErrorType {
    data object InvalidKSN : PinBlockErrorType()
    data class Other(val message: String?) : PinBlockErrorType()
}

fun PinBlockUiState.Failure.getErrorType(): PinBlockErrorType {
    return when (errorCode) {
        InvalidKSNException::class.simpleName -> PinBlockErrorType.InvalidKSN
        else -> PinBlockErrorType.Other(errorMessage)
    }
}

data class PinpadButtonUiState(
    val value: String,
    var isEnabled: Boolean = true
)

sealed class PinpadButtonUiEvent {
    data class WhenNumberIsDigested(val buttonUiState: PinpadButtonUiState) : PinpadButtonUiEvent()
    data class WhenCancelIsPressed(val buttonUiState: PinpadButtonUiState) : PinpadButtonUiEvent()
    data class WhenDeleteIsPressed(val buttonUiState: PinpadButtonUiState) : PinpadButtonUiEvent()
    data class WhenConfirmIsPressed(
        val navigateToFailedPayment: (errorCode: String, errorMessage: String) -> Unit,
        val navigateToProcessPayment: (pinBlock: String, ksn: String) -> Unit
    ) : PinpadButtonUiEvent()
}

@HiltViewModel
open class PinpadViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val pinBlockRequestUseCase: IPinBlockRequestUseCase
) : ViewModel() {
    private var _pinpadUiState: PinpadUiState by mutableStateOf(PinpadUiState())
    val pinpadUiState: PinpadUiState get() = _pinpadUiState

    private var _pinpadButtonsUiEvent: List<PinpadButtonUiEvent> by mutableStateOf(
        listOf(
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("1")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("2")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("3")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("4")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("5")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("6")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("7")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("8")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("9")),
            PinpadButtonUiEvent.WhenCancelIsPressed(PinpadButtonUiState("X")),
            PinpadButtonUiEvent.WhenNumberIsDigested(PinpadButtonUiState("0")),
            PinpadButtonUiEvent.WhenDeleteIsPressed(PinpadButtonUiState("<-", false))
        )
    )

    val pinpadButtonsUiEvent: List<PinpadButtonUiEvent> get() = _pinpadButtonsUiEvent

    fun onPinpadButtonUiEvent(event: PinpadButtonUiEvent, navigateTo: () -> Unit) {
        when (event) {
            is PinpadButtonUiEvent.WhenNumberIsDigested -> {
                _pinpadUiState = if (pinpadUiState.pin.length < 6) {
                    pinpadUiState.copy(pin = pinpadUiState.pin + event.buttonUiState.value)
                } else {
                    pinpadUiState.copy()
                }
            }

            is PinpadButtonUiEvent.WhenCancelIsPressed -> {
                navigateTo()
            }

            is PinpadButtonUiEvent.WhenDeleteIsPressed -> {
                _pinpadUiState = if (pinpadUiState.pin.isNotEmpty()) {
                    pinpadUiState.copy(pin = pinpadUiState.pin.dropLast(1))
                } else {
                    pinpadUiState.copy()
                }
            }

            is PinpadButtonUiEvent.WhenConfirmIsPressed -> {
                startConfirmPinpad(
                    event.navigateToFailedPayment,
                    event.navigateToProcessPayment
                )
            }
        }
        updatePinpadButtonsState()
    }

    private fun updatePinpadButtonsState() {
        val pinpadButtonsUiEventUpdated: List<PinpadButtonUiEvent> = pinpadButtonsUiEvent.map(
            transform = { pinpadButtonUiEvent ->
                when (pinpadButtonUiEvent) {
                    is PinpadButtonUiEvent.WhenNumberIsDigested -> {
                        PinpadButtonUiEvent.WhenNumberIsDigested(
                            buttonUiState = pinpadButtonUiEvent.buttonUiState.copy(
                                isEnabled = pinpadUiState.pin.length < 6
                            )
                        )
                    }

                    is PinpadButtonUiEvent.WhenDeleteIsPressed -> {
                        PinpadButtonUiEvent.WhenDeleteIsPressed(
                            buttonUiState = pinpadButtonUiEvent.buttonUiState.copy(
                                isEnabled = pinpadUiState.pin.isNotEmpty()
                            )
                        )
                    }

                    is PinpadButtonUiEvent.WhenCancelIsPressed -> pinpadButtonUiEvent
                    is PinpadButtonUiEvent.WhenConfirmIsPressed -> TODO()
                }
            }
        )
        _pinpadButtonsUiEvent = pinpadButtonsUiEventUpdated
    }

    private val _pinBlockUiState: Flow<PinBlockUiState> = pinBlockRequestUseCase()
        .asResult()
        .map(transform = { mapPinBlockDataToUiState(it) })

    private fun mapPinBlockDataToUiState(result: Result<PinBlockData>): PinBlockUiState {
        return when (result) {
            Result.Loading -> PinBlockUiState.Requesting
            is Result.Success -> PinBlockUiState.Obtained(result.data)
            is Result.Error -> PinBlockUiState.Failure(
                errorCode = result.exception::class.simpleName,
                errorMessage = result.exception.localizedMessage ?: "Error desconocido"
            )
        }
    }

    private val pinBlockUiState: StateFlow<PinBlockUiState> = _pinBlockUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = PinBlockUiState.Requesting
    )

    private val _confirmingPinStepUiState: MutableStateFlow<ConfirmingPinStepUiState> =
        MutableStateFlow<ConfirmingPinStepUiState>(ConfirmingPinStepUiState.Pending)

    val confirmingPinStepUiState: StateFlow<ConfirmingPinStepUiState> =
        _confirmingPinStepUiState.asStateFlow()

    private fun startConfirmPinpad(
        onFailedPayment: (errorCode: String, errorMessage: String) -> Unit,
        navigateToProcessPayment: (pinBlock: String, ksn: String) -> Unit
    ) {
        viewModelScope.launch {
            _confirmingPinStepUiState.value = ConfirmingPinStepUiState.Confirming

            val pinBlockResult: PinBlockUiState = pinBlockUiState
                .filter(predicate = { it !is PinBlockUiState.Requesting })
                .first()

            if (pinBlockResult !is PinBlockUiState.Obtained) {

                delay(1000)

                val failure = pinBlockResult as? PinBlockUiState.Failure

                val errorType = failure?.getErrorType()

                when (errorType) {
                    PinBlockErrorType.InvalidKSN -> {
                        onFailedPayment(
                            failure.errorCode ?: "UNKNOWN CODE",
                            failure.errorMessage ?: "Error desconocido"
                        )
                    }

                    is PinBlockErrorType.Other, null -> {
                        onFailedPayment(
                            failure?.errorCode ?: "UNKNOWN CODE",
                            failure?.errorMessage ?: "Error desconocido"
                        )
                    }
                }

                return@launch
            }

            delay(1000)

            navigateToProcessPayment(pinBlockResult.data.pinBlock, pinBlockResult.data.ksn)
        }
    }
}