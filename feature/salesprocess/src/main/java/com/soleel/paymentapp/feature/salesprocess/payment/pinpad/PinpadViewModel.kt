package com.soleel.paymentapp.feature.salesprocess.payment.pinpad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinpadUiState(
    val pin: String = "",
    val confirmingPinUiState: ConfirmingPinUiState = ConfirmingPinUiState.Pending
)

sealed interface ConfirmingPinUiState {
    data object Pending : ConfirmingPinUiState
    data object Confirming : ConfirmingPinUiState
}

data class PinpadButtonUiState(
    val value: String,
    var isEnabled: Boolean = true
)

sealed class PinpadButtonUiEvent {
    data class WhenNumberIsDigested(val buttonUiState: PinpadButtonUiState) : PinpadButtonUiEvent()
    data class WhenCancelIsPressed(val buttonUiState: PinpadButtonUiState) : PinpadButtonUiEvent()
    data class WhenDeleteIsPressed(val buttonUiState: PinpadButtonUiState) : PinpadButtonUiEvent()
}

@HiltViewModel
open class PinpadViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
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
                }
            }
        )
        _pinpadButtonsUiEvent = pinpadButtonsUiEventUpdated
    }

    fun onConfirmPinpadInput(navigateToProcessPayment: () -> Unit) {
        viewModelScope.launch(
            block = {
                _pinpadUiState =
                    pinpadUiState.copy(confirmingPinUiState = ConfirmingPinUiState.Confirming)
                delay(1500)
                navigateToProcessPayment()

                // TODO: RECHAZO POR KSNs invalidos
            }
        )
    }
}