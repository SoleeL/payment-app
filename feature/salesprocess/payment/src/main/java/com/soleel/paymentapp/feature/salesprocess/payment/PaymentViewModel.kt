package com.soleel.paymentapp.feature.salesprocess.payment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.common.result.Result
import com.soleel.paymentapp.core.common.result.asResult
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.paymentprocess.ConfirmationPaymentProcessData
import com.soleel.paymentapp.core.model.paymentprocess.PaymentProcessData
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

sealed class PaymentProcessUiState<out T> {
    data object Loading : PaymentProcessUiState<Nothing>()
    data class Success<T>(val data: T) : PaymentProcessUiState<T>()
    data class Failure(val errorMessage: String? = null) : PaymentProcessUiState<Nothing>()
}

sealed interface PaymentStepUiState {
    data object Idle : PaymentStepUiState
    data object Validating : PaymentStepUiState
    data object Confirming : PaymentStepUiState
    data object Saving : PaymentStepUiState
    data object Done : PaymentStepUiState
    data class Error(val message: String?, val step: Step) : PaymentStepUiState

    enum class Step {
        VALIDATING, CONFIRMING, SAVING
    }
}

@HiltViewModel
open class PaymentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val requestValidationPaymentUseCase: IRequestValidationPaymentUseCase,
    private val requestConfirmationPaymentUseCase: IRequestConfirmingPaymentUseCase,
    private val savePaymentUseCase: ISavePaymentUseCase,
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

    fun onConfirmPinpadInput(
        navigateToRegisterPayment: () -> Unit,
        navigateToFailedSale: () -> Unit
    ) {
        _pinpadUiState = pinpadUiState.copy(confirmingPinUiState = ConfirmingPinUiState.Confirming)

        viewModelScope.launch(
            block = {

                delay(1500) // Simula procesamiento

                val validPins = listOf("1234", "0000", "9999") // Lista de PINs válidos

                if (pinpadUiState.pin in validPins) {
                    navigateToRegisterPayment()
                } else {
                    navigateToFailedSale()
                }
            }
        )
    }

    private val _validatingPaymentProcessUiState: Flow<PaymentProcessUiState<ValidationPaymentProcessData>> =
        requestValidationPaymentUseCase()
            .asResult()
            .map { getValidationPaymentProcessData(it) }

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
        MutableStateFlow<PaymentStepUiState>(PaymentStepUiState.Idle)
    val paymentStepUiState: StateFlow<PaymentStepUiState> = _paymentStepUiState

    fun startPaymentProcess() {
        viewModelScope.launch {
            _paymentStepUiState.value = PaymentStepUiState.Validating

            val validatingPaymentProcessResult: PaymentProcessUiState<ValidationPaymentProcessData> =
                validatingPaymentProcessUiState
                    .filter(predicate = { it !is PaymentProcessUiState.Loading })
                    .first()

            if (validatingPaymentProcessResult !is PaymentProcessUiState.Success) {
                val errorMessage: String? =
                    (validatingPaymentProcessResult as? PaymentProcessUiState.Failure)?.errorMessage
                _paymentStepUiState.value = PaymentStepUiState.Error(
                    errorMessage,
                    PaymentStepUiState.Step.VALIDATING
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
                _paymentStepUiState.value = PaymentStepUiState.Error(
                    errorMessage,
                    PaymentStepUiState.Step.CONFIRMING
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
                _paymentStepUiState.value = PaymentStepUiState.Error(
                    errorMessage,
                    PaymentStepUiState.Step.SAVING
                )
                return@launch
            }

            _paymentStepUiState.value = PaymentStepUiState.Done
        }
    }
}