package com.soleel.paymentapp.feature.salesprocess.setup.cashchangecalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class CashChangeCalculatorUiState(
    val cashChange: Float? = null
)

data class ButtonUiState(
    val value: String,
    var isEnabled: Boolean = true
)

sealed class ButtonUiEvent {
    data class WhenNumberIsDigested(val buttonUiState: ButtonUiState) : ButtonUiEvent()
    data class WhenClearIsPressed(val buttonUiState: ButtonUiState) : ButtonUiEvent()
    data class WhenDeleteIsPressed(val buttonUiState: ButtonUiState) : ButtonUiEvent()
}

@HiltViewModel
open class CashChangeCalculatorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val totalAmount: Int = savedStateHandle.get<Int>("totalAmount") ?: 0
    private val tipTotal: Float = savedStateHandle.get<Float>("tipTotal") ?: 0f

    private var _buttonsUiEvent: List<ButtonUiEvent> by mutableStateOf(
        listOf(
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("7")),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("8")),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("9")),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("4")),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("5")),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("6")),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("1")),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("2")),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("3")),
            ButtonUiEvent.WhenClearIsPressed(ButtonUiState("C", false)),
            ButtonUiEvent.WhenNumberIsDigested(ButtonUiState("0")),
            ButtonUiEvent.WhenDeleteIsPressed(ButtonUiState("<-", false))
        )
    )

    val buttonsUiEvent: List<ButtonUiEvent> get() = _buttonsUiEvent

    fun onButtonUiEvent(event: ButtonUiEvent) {
        when (event) {
            is ButtonUiEvent.WhenNumberIsDigested -> TODO()
            is ButtonUiEvent.WhenClearIsPressed -> TODO()
            is ButtonUiEvent.WhenDeleteIsPressed -> TODO()
        }
        updateButtonsState()
    }

    private fun updateButtonsState() {
        TODO("Not yet implemented")
    }
}