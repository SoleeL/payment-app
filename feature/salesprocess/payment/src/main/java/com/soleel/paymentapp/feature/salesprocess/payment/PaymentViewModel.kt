package com.soleel.paymentapp.feature.salesprocess.payment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.model.Sale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReadingUiState {
    data object Reading : ReadingUiState
    data object Success : ReadingUiState
    data object Failure : ReadingUiState
}

sealed interface ConfirmingPinUiState {
    data object Pending : ConfirmingPinUiState
    data object Confirming : ConfirmingPinUiState
}

data class PinpadUiState(
    val pin: String = "",
    val confirmingPinUiState: ConfirmingPinUiState = ConfirmingPinUiState.Pending
)

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
open class PaymentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val sale: Sale = savedStateHandle.get<Sale>("sale") ?: Sale(calculatorTotal = 0f)

    // Contactless
    private val _contactlessReadingUiState: Flow<ReadingUiState> = getContactlessReadingUiState()

    private fun getContactlessReadingUiState(): Flow<ReadingUiState> = flow(
        block = {
            emit(ReadingUiState.Reading)
            delay(5_000)
            if (!false) { // TODO: Pendiente implementacion con shared preference para pruebas
                emit(ReadingUiState.Success)
            } else {
                emit(ReadingUiState.Failure)
            }
        }
    )

    val contactlessReadingUiState: StateFlow<ReadingUiState> = _contactlessReadingUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ReadingUiState.Reading
        )

    // Contact
    private val _contactReadingUiState: Flow<ReadingUiState> = getContactReadingUiState()

    private fun getContactReadingUiState(): Flow<ReadingUiState> = flow(
        block = {
            emit(ReadingUiState.Reading)
            delay(5_000)

            if (!false) { // TODO: Pendiente implementacion con shared preference para pruebas
                emit(ReadingUiState.Success)
            } else {
                emit(ReadingUiState.Failure)
            }
        }
    )

    val contactReadingUiState: StateFlow<ReadingUiState> = _contactReadingUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = ReadingUiState.Reading
    )

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
                _pinpadUiState = if (pinpadUiState.pin.length < 12) {
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
                    is PinpadButtonUiEvent.WhenNumberIsDigested -> pinpadButtonUiEvent
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

    fun onConfirmPinpadInput(
        navigateToRegisterPayment: () -> Unit,
        navigateToFailedSale: () -> Unit
    ) {
        _pinpadUiState = pinpadUiState.copy(confirmingPinUiState = ConfirmingPinUiState.Confirming)

        viewModelScope.launch(
            block = {

                delay(500L) // Simula procesamiento

                val validPins = listOf("1234", "0000", "9999") // Lista de PINs válidos

                if (pinpadUiState.pin in validPins) {
                    navigateToRegisterPayment()
                } else {
                    navigateToFailedSale()
                }
            }
        )
    }
}