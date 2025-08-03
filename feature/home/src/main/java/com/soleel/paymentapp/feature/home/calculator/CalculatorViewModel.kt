package com.soleel.paymentapp.feature.home.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.soleel.paymentapp.core.component.KeyboardInputUiState
import com.soleel.paymentapp.core.component.NumberKeyboardButtonType
import com.soleel.paymentapp.core.component.NumberKeyboardButtonUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    private var onNavigate: ((Int) -> Unit)? = null

    fun setNavigationCallback(callback: (Int) -> Unit) {
        onNavigate = callback
    }

    private var _keyboardInputUiState: KeyboardInputUiState by mutableStateOf(
        KeyboardInputUiState(
            isConfidential = false
        )
    )

    val keyboardInputUiState: KeyboardInputUiState get() = _keyboardInputUiState

    private var _calculatorButtonsUi: List<NumberKeyboardButtonUiState> by mutableStateOf(
        listOf(
            NumberKeyboardButtonUiState(
                value = "7",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 7
                    )
                }
            ),
            NumberKeyboardButtonUiState(
                value = "8",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 8
                    )
                }
            ),
            NumberKeyboardButtonUiState(
                value = "9",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 9
                    )
                }
            ),

            NumberKeyboardButtonUiState(
                value = "4",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 4
                    )
                }
            ),
            NumberKeyboardButtonUiState(
                value = "5",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 5
                    )
                }
            ),
            NumberKeyboardButtonUiState(
                value = "6",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 6
                    )
                }
            ),

            NumberKeyboardButtonUiState(
                value = "1",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 1
                    )
                }
            ),
            NumberKeyboardButtonUiState(
                value = "2",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 2
                    )
                }
            ),
            NumberKeyboardButtonUiState(
                value = "3",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = (keyboardInputUiState.value ?: 0) * 10 + 3
                    )
                }
            ),

            NumberKeyboardButtonUiState(
                value = "C",
                icon = null,
                type = NumberKeyboardButtonType.OPERATION,
                isEnabledEvaluator = { keyboardInputUiState.value != null },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = null
                    )
                }
            ),
            NumberKeyboardButtonUiState(
                value = "0",
                icon = null,
                type = NumberKeyboardButtonType.NUMBER,
                isEnabledEvaluator = { keyboardInputUiState.value.toString().length < 7 },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value =  if (keyboardInputUiState.value == null) {
                            null
                        } else {
                            keyboardInputUiState.value!! * 10
                        }
                    )
                }
            ),
            NumberKeyboardButtonUiState(
                value = "<-",
                icon = null,
                type = NumberKeyboardButtonType.OPERATION,
                isEnabledEvaluator = { keyboardInputUiState.value != null },
                onClick = {
                    _keyboardInputUiState = keyboardInputUiState.copy(
                        value = if (keyboardInputUiState.value in 0..9) {
                            null
                        } else {
                            (keyboardInputUiState.value ?: 0) / 10
                        }
                    )
                }
            ),

            NumberKeyboardButtonUiState(
                value = "CONFIRMAR",
                icon = null,
                span = 3,
                type = NumberKeyboardButtonType.OPERATION,
                isEnabledEvaluator = { (keyboardInputUiState.value?.let(block = { it > 500 })) == true },
                onClick = { onNavigate?.invoke(keyboardInputUiState.value!!) }
            )
        )
    )

    val calculatorButtonsUi: List<NumberKeyboardButtonUiState> get() = _calculatorButtonsUi

}