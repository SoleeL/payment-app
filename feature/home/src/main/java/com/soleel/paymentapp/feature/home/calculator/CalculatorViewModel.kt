package com.soleel.paymentapp.feature.home.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.collections.List

// README: Recomendado segun traduccion
// UI Model: Representa un objeto de dominio ya adaptado para mostrarse en la UI.
// UI Event: Son las acciones que provienen desde la UI hacia el ViewModel.
//      Se suelen modelar como sealed classes para representar eventos como clics, scroll, cambios,
//      etc.
// UI State: Representa todo el estado actual de la pantalla.
//      Puede contener:
//          Lista de ítems (itemsInCartUi)
//          Estado de botones (isEnabled)
//          Cargas (isLoading)
//          Errores (errorMessage)
//          Campos de entrada (inputText)
// UI Effect: Para cosas que no deben sobrevivir recomposiciones como mostrar un toast, navegar,
//  abrir diálogos.
//      Son acciones de una sola vez, no estado persistente.

data class CalculatorUiModel(
    val name: String = "",
    val nameError: Int? = null,

    val value: Float = 0f,
    val valueError: Int? = null,

    val multiply: Float = 0f,
    val multiplyError: Int? = null,

    val division: Float = 0f,
    val divisionError: Int? = null,

    val subtract: Float = 0f,
    val subtractError: Int? = null,

    val isNextOperationInitialDecimal: Boolean = false,

    val historyOperations: List<CalculatorOperatorButtonUiEvent> = emptyList(),

    val result: Float = 0f
)

data class CalculatorButtonUiState(
    val value: String,
    var isEnabled: Boolean,
    val operator: CalculatorOperatorButtonUiEvent,
    val weight: Float = 1f
)

sealed class CalculatorOperatorButtonUiEvent {
    data object Number : CalculatorOperatorButtonUiEvent()

//    data object Clear : CalculatorOperatorButtonUiEvent()
    data object Percent : CalculatorOperatorButtonUiEvent()

    data object Multiply : CalculatorOperatorButtonUiEvent()
    data object Division : CalculatorOperatorButtonUiEvent()
    data object Subtract : CalculatorOperatorButtonUiEvent()

    data object Decimal : CalculatorOperatorButtonUiEvent()
    data object Delete : CalculatorOperatorButtonUiEvent()
}

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {
    private var _calculatorUiModels: List<CalculatorUiModel> by mutableStateOf(emptyList())
    val calculatorUiModels: List<CalculatorUiModel> get() = _calculatorUiModels

    private var _currentCalculatorUiModel: CalculatorUiModel by mutableStateOf(CalculatorUiModel())
    val currentCalculatorUiModel: CalculatorUiModel get() = _currentCalculatorUiModel

    private var _calculatorButtonsUi: List<List<CalculatorButtonUiState>> by mutableStateOf(
        listOf(
            listOf(
                CalculatorButtonUiState("7", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("8", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("9", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("%", false, CalculatorOperatorButtonUiEvent.Percent)
            ),
            listOf(
                CalculatorButtonUiState("4", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("5", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("6", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("x", false, CalculatorOperatorButtonUiEvent.Multiply)
            ),
            listOf(
                CalculatorButtonUiState("1", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("2", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("3", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("/", false, CalculatorOperatorButtonUiEvent.Division)
            ),
            listOf(
                CalculatorButtonUiState(".", false, CalculatorOperatorButtonUiEvent.Decimal),
                CalculatorButtonUiState("0", true, CalculatorOperatorButtonUiEvent.Number),
                CalculatorButtonUiState("<-", false, CalculatorOperatorButtonUiEvent.Delete),
                CalculatorButtonUiState("-", false, CalculatorOperatorButtonUiEvent.Subtract)
            )
        )
    )

    val calculatorButtonsUi: List<List<CalculatorButtonUiState>> get() = _calculatorButtonsUi

    fun onNameChanged(name: String) {
        _currentCalculatorUiModel = currentCalculatorUiModel.copy(name = name)
    }

    fun onButtonCalculatorEvent(event: CalculatorButtonUiState) {
        when (event.operator) {
            is CalculatorOperatorButtonUiEvent.Number -> numberEvent(event.value.toInt())

//            is CalculatorOperatorButtonUiEvent.Clear -> clearCalculatorItem()
            is CalculatorOperatorButtonUiEvent.Percent -> convertToPercent()

            is CalculatorOperatorButtonUiEvent.Division,
            is CalculatorOperatorButtonUiEvent.Multiply,
            is CalculatorOperatorButtonUiEvent.Subtract -> addEventToHistoryOperations(event.operator)

            is CalculatorOperatorButtonUiEvent.Decimal -> nextOperationDecimal()
            is CalculatorOperatorButtonUiEvent.Delete -> deleteLastDigit()
        }

        updateButtonsState()
    }

    private fun numberEvent(value: Int) {
        when (currentCalculatorUiModel.historyOperations.lastOrNull()) {
            CalculatorOperatorButtonUiEvent.Multiply -> inputMultiply(value)
            CalculatorOperatorButtonUiEvent.Division -> inputDivision(value)
            CalculatorOperatorButtonUiEvent.Subtract -> inputSubtract(value)
            else -> {
                _currentCalculatorUiModel = currentCalculatorUiModel.copy(
                    value = currentCalculatorUiModel.value * 10 + value
                )
            }
        }

        calculateResult()
    }

    private fun inputMultiply(value: Int) {
        _currentCalculatorUiModel = if (currentCalculatorUiModel.isNextOperationInitialDecimal) {
            currentCalculatorUiModel.copy(
                multiply = currentCalculatorUiModel.multiply + value.toFloat() / 10,
                isNextOperationInitialDecimal = false
            )
        } else {
            if (currentCalculatorUiModel.multiply == 0f) {
                currentCalculatorUiModel.copy(
                    multiply = value.toFloat(),
                    isNextOperationInitialDecimal = value == 0
                )
            } else {
                if (currentCalculatorUiModel.multiply % 1 != 0f) {
                    val multiplyIntPart: Int = currentCalculatorUiModel.multiply.toInt()
                    val multiplyDecimalPartToInt: Int = currentCalculatorUiModel.multiply
                        .toString()
                        .substringAfter(".")
                        .toInt()
                    val multiplyDecimalPart: Int = (multiplyDecimalPartToInt * 10) + value
                    currentCalculatorUiModel.copy(
                        multiply = "$multiplyIntPart.$multiplyDecimalPart".toFloat()
                    )
                } else {
                    currentCalculatorUiModel.copy(
                        multiply = (currentCalculatorUiModel.multiply * 10) + value
                    )
                }
            }
        }
    }

    private fun inputDivision(value: Int) {
        _currentCalculatorUiModel = if (currentCalculatorUiModel.isNextOperationInitialDecimal) {
            currentCalculatorUiModel.copy(
                division = currentCalculatorUiModel.division + value.toFloat() / 10,
                isNextOperationInitialDecimal = false
            )
        } else {
            if (currentCalculatorUiModel.division == 0f) {
                currentCalculatorUiModel.copy(
                    division = value.toFloat(),
                    isNextOperationInitialDecimal = value == 0
                )
            } else {
                if (currentCalculatorUiModel.division % 1 != 0f) {
                    val divisionIntPart: Int = currentCalculatorUiModel.division.toInt()
                    val divisionDecimalPartToInt: Int = currentCalculatorUiModel.division
                        .toString()
                        .substringAfter(".")
                        .toInt()
                    val divisionDecimalPart: Int = (divisionDecimalPartToInt * 10) + value
                    currentCalculatorUiModel.copy(
                        division = "$divisionIntPart.$divisionDecimalPart".toFloat()
                    )
                } else {
                    currentCalculatorUiModel.copy(
                        division = (currentCalculatorUiModel.division * 10) + value
                    )
                }
            }
        }
    }

    private fun inputSubtract(value: Int) {
        _currentCalculatorUiModel = if (currentCalculatorUiModel.subtract == 0f) {
            currentCalculatorUiModel.copy(subtract = value.toFloat())
        } else {
            // TODO: Falta implementar multiples divisas
            currentCalculatorUiModel.copy(subtract = (currentCalculatorUiModel.subtract * 10) + value)
        }
    }

    private fun clearCalculatorItem() {
        _currentCalculatorUiModel = CalculatorUiModel()
    }

    private fun calculateResult() {
        var newResult: Float = currentCalculatorUiModel.value

        if (currentCalculatorUiModel.multiply != 0f && // TODO: ARREGLAR
            currentCalculatorUiModel.historyOperations.contains(CalculatorOperatorButtonUiEvent.Multiply)
        ) {
            newResult *= currentCalculatorUiModel.multiply
        }

        if (currentCalculatorUiModel.division != 0f &&
            currentCalculatorUiModel.historyOperations.contains(CalculatorOperatorButtonUiEvent.Division)
        ) {
            newResult /= currentCalculatorUiModel.division
        }

        if (currentCalculatorUiModel.subtract != 0f &&
            currentCalculatorUiModel.historyOperations.contains(CalculatorOperatorButtonUiEvent.Subtract)
        ) {
            newResult -= currentCalculatorUiModel.subtract
        }

        _currentCalculatorUiModel = currentCalculatorUiModel.copy(result = newResult)
    }

    private fun convertToPercent() {
        _currentCalculatorUiModel = when (currentCalculatorUiModel.historyOperations.lastOrNull()) {
            CalculatorOperatorButtonUiEvent.Multiply -> {
                val newMultiply: Float = (currentCalculatorUiModel.multiply / 100)
                    .toBigDecimal()
                    .setScale(2, java.math.RoundingMode.HALF_UP)
                    .toFloat()
                currentCalculatorUiModel.copy(
                    multiply = newMultiply,
                    isNextOperationInitialDecimal = newMultiply == 0f
                )
            }

            CalculatorOperatorButtonUiEvent.Subtract -> {
                var previousResult: Float = currentCalculatorUiModel.value

                if (currentCalculatorUiModel.multiply > 0f) {
                    previousResult *= currentCalculatorUiModel.multiply
                }

                if (currentCalculatorUiModel.division > 0f) {
                    previousResult /= currentCalculatorUiModel.division
                }

                val newSubtract: Float =
                    (previousResult * (currentCalculatorUiModel.subtract / 100)).toInt().toFloat()
                currentCalculatorUiModel.copy(subtract = newSubtract)
            }

            // README: En una calculadora de items el VALUE y la DIVISION no tienen sentido para porcentaje
            else -> {
                currentCalculatorUiModel
            }
        }

        calculateResult()
    }

    private fun addEventToHistoryOperations(event: CalculatorOperatorButtonUiEvent) {
        val updatedHistory = currentCalculatorUiModel.historyOperations
            .filterNot( // Elimina eventos innecesarios si sus valores son 0
                predicate = {
                    (it == CalculatorOperatorButtonUiEvent.Multiply && currentCalculatorUiModel.multiply == 0f) ||
                            (it == CalculatorOperatorButtonUiEvent.Division && currentCalculatorUiModel.division == 0f) ||
                            (it == CalculatorOperatorButtonUiEvent.Subtract && currentCalculatorUiModel.subtract == 0f)
                }
            )
            .filterNot( // Elimina duplicado y agrega al final
                predicate = { it == event }
            ) + event

        _currentCalculatorUiModel = currentCalculatorUiModel.copy(
            historyOperations = updatedHistory,
            isNextOperationInitialDecimal = false
        )
    }

    private fun nextOperationDecimal() {
        _currentCalculatorUiModel =
            currentCalculatorUiModel.copy(isNextOperationInitialDecimal = true)
    }

    private fun deleteLastDigit() {
        _currentCalculatorUiModel = when (currentCalculatorUiModel.historyOperations.lastOrNull()) {
            CalculatorOperatorButtonUiEvent.Multiply -> {
                if (currentCalculatorUiModel.isNextOperationInitialDecimal) {
                    currentCalculatorUiModel.copy(isNextOperationInitialDecimal = false)
                } else {
                    if (currentCalculatorUiModel.multiply == 0f) {
                        val filteredOperations: List<CalculatorOperatorButtonUiEvent> = currentCalculatorUiModel.historyOperations
                            .filterNot( // Elimina eventos innecesarios si sus valores son 0
                                predicate = {
                                    (it == CalculatorOperatorButtonUiEvent.Multiply)
                                }
                            )
                        currentCalculatorUiModel.copy(
                            historyOperations = filteredOperations
                        )
                    } else {
                        val newMultiply: Float = if (currentCalculatorUiModel.multiply % 1 != 0f) {
                            val multiplyIntPart: Int = currentCalculatorUiModel.multiply.toInt()
                            val multiplyDecimalPartToInt: Int = currentCalculatorUiModel
                                .multiply
                                .toString()
                                .substringAfter(".")
                                .toInt()
                            val multiplyNewDecimalPart: Int = multiplyDecimalPartToInt / 10
                            "$multiplyIntPart.$multiplyNewDecimalPart".toFloat()
                        } else {
                            if (currentCalculatorUiModel.multiply > 9f) {
                                (currentCalculatorUiModel.multiply.toInt() / 10).toFloat()
                            } else {
                                0f
                            }
                        }

                        currentCalculatorUiModel.copy(
                            multiply = newMultiply,
                            isNextOperationInitialDecimal = currentCalculatorUiModel.multiply % 1 != 0f &&
                                    newMultiply % 1 == 0f
                        )
                    }
                }
            }

            CalculatorOperatorButtonUiEvent.Division -> {
                if (currentCalculatorUiModel.isNextOperationInitialDecimal) {
                    currentCalculatorUiModel.copy(isNextOperationInitialDecimal = false)
                } else {
                    if (currentCalculatorUiModel.division == 0f) {
                        val filteredOperations: List<CalculatorOperatorButtonUiEvent> = currentCalculatorUiModel.historyOperations
                            .filterNot( // Elimina eventos innecesarios si sus valores son 0
                                predicate = {
                                    (it == CalculatorOperatorButtonUiEvent.Division &&
                                            currentCalculatorUiModel.division == 0f)
                                }
                            )
                        currentCalculatorUiModel.copy(
                            historyOperations = filteredOperations
                        )
                    } else {
                        val newDivision: Float = if (currentCalculatorUiModel.division % 1 != 0f) {
                            val divisionIntPart: Int = currentCalculatorUiModel.division.toInt()
                            val divisionDecimalPartToInt: Int = currentCalculatorUiModel
                                .division
                                .toString()
                                .substringAfter(".")
                                .toInt()
                            val divisionNewDecimalPart: Int = divisionDecimalPartToInt / 10
                            "$divisionIntPart.$divisionNewDecimalPart".toFloat()
                        } else {
                            if (currentCalculatorUiModel.division > 9f) {
                                (currentCalculatorUiModel.division.toInt() / 10).toFloat()
                            } else {
                                0f
                            }
                        }

                        currentCalculatorUiModel.copy(
                            division = newDivision,
                            isNextOperationInitialDecimal = currentCalculatorUiModel.division % 1 != 0f &&
                                    newDivision % 1 == 0f
                        )
                    }
                }
            }

            CalculatorOperatorButtonUiEvent.Subtract -> {
                if (currentCalculatorUiModel.subtract == 0f) {
                    val filteredOperations: List<CalculatorOperatorButtonUiEvent> = currentCalculatorUiModel.historyOperations
                        .filterNot( // Elimina eventos innecesarios si sus valores son 0
                            predicate = {
                                (it == CalculatorOperatorButtonUiEvent.Subtract &&
                                        currentCalculatorUiModel.subtract == 0f)
                            }
                        )
                    currentCalculatorUiModel.copy(
                        historyOperations = filteredOperations
                    )
                } else {
                    val newSubtract: Float = if (currentCalculatorUiModel.subtract > 9f) {
                        (currentCalculatorUiModel.subtract.toInt() / 10).toFloat()
                    } else {
                        0f
                    }

                    currentCalculatorUiModel.copy(subtract = newSubtract)
                }
            }

            else -> {
                currentCalculatorUiModel.copy(
                    value = if (currentCalculatorUiModel.value > 9f) { // Tiene decenas
                        (currentCalculatorUiModel.value.toInt() / 10).toFloat() // Se divide por 10
                    } else {
                        0f // Es un solo digito, se anula a 0
                    }
                )
            }
        }

        calculateResult()
    }

    private fun updateButtonsState() {
        val calculatorButtonsUiUpdated: List<List<CalculatorButtonUiState>> =
            calculatorButtonsUi.map(
                transform = { row ->
                    row.map(
                        transform = { calculatorButton ->
                            when (calculatorButton.operator) {
                                CalculatorOperatorButtonUiEvent.Number -> {
                                    val newIsEnabled: Boolean =
                                        when (currentCalculatorUiModel.historyOperations.lastOrNull()) {
                                            CalculatorOperatorButtonUiEvent.Multiply -> {
                                                if (currentCalculatorUiModel.isNextOperationInitialDecimal) {
                                                    true
                                                } else if (currentCalculatorUiModel.multiply % 1 != 0f) {
                                                    currentCalculatorUiModel.multiply
                                                        .toString().substringAfter(".")
                                                        .toInt()
                                                        .toString()
                                                        .length < 2
                                                } else {
                                                    currentCalculatorUiModel.multiply
                                                        .toInt()
                                                        .toString()
                                                        .length < 2
                                                }
                                            }

                                            CalculatorOperatorButtonUiEvent.Division -> {
                                                if (currentCalculatorUiModel.isNextOperationInitialDecimal) {
                                                    true
                                                } else if (currentCalculatorUiModel.division % 1 != 0f) {
                                                    currentCalculatorUiModel.division
                                                        .toString().substringAfter(".")
                                                        .toInt()
                                                        .toString()
                                                        .length < 2
                                                } else {
                                                    currentCalculatorUiModel.division
                                                        .toInt()
                                                        .toString()
                                                        .length < 2
                                                }
                                            }

                                            CalculatorOperatorButtonUiEvent.Subtract -> currentCalculatorUiModel.subtract
                                                .toInt()
                                                .toString()
                                                .length < 7

                                            else -> currentCalculatorUiModel.value
                                                .toInt()
                                                .toString()
                                                .length < 7
                                        }

                                    calculatorButton.copy(
                                        isEnabled = newIsEnabled
                                    )
                                }

//                                CalculatorOperatorButtonUiEvent.Clear -> {
//                                    val newIsEnabled: Boolean = currentCalculatorUiModel
//                                        .name
//                                        .isNotEmpty() ||
//                                            currentCalculatorUiModel
//                                                .historyOperations
//                                                .isNotEmpty() ||
//                                            currentCalculatorUiModel.result > 0
//                                    calculatorButton.copy(isEnabled = newIsEnabled)
//                                }

                                CalculatorOperatorButtonUiEvent.Percent -> {
                                    val isLastOperationMultiply: Boolean = currentCalculatorUiModel
                                        .historyOperations
                                        .lastOrNull() == CalculatorOperatorButtonUiEvent.Multiply &&
                                            currentCalculatorUiModel.multiply > 0f
                                    val isLastOperationSubtractValid: Boolean =
                                        currentCalculatorUiModel
                                            .historyOperations
                                            .lastOrNull() == CalculatorOperatorButtonUiEvent.Subtract &&
                                                currentCalculatorUiModel.subtract > 0
                                    val newIsEnabled: Boolean = isLastOperationMultiply ||
                                            isLastOperationSubtractValid
                                    calculatorButton.copy(isEnabled = newIsEnabled)
                                }

                                CalculatorOperatorButtonUiEvent.Multiply -> {
                                    val isCurrentOperationMultiply: Boolean =
                                        currentCalculatorUiModel
                                            .historyOperations
                                            .lastOrNull() == CalculatorOperatorButtonUiEvent.Multiply
                                    val newIsEnabled: Boolean =
                                        currentCalculatorUiModel.result > 0 &&
                                                !isCurrentOperationMultiply
                                    calculatorButton.copy(isEnabled = newIsEnabled)
                                }

                                CalculatorOperatorButtonUiEvent.Division -> {
                                    val isLastOperationDivision: Boolean = currentCalculatorUiModel
                                        .historyOperations
                                        .lastOrNull() != CalculatorOperatorButtonUiEvent.Division
                                    val newIsEnabled: Boolean = currentCalculatorUiModel.result > 0
                                            && isLastOperationDivision
                                    calculatorButton.copy(isEnabled = newIsEnabled)
                                }

                                CalculatorOperatorButtonUiEvent.Subtract -> {
                                    val isLastOperationSubtract: Boolean = currentCalculatorUiModel
                                        .historyOperations
                                        .lastOrNull() != CalculatorOperatorButtonUiEvent.Subtract
                                    val newIsEnabled: Boolean = currentCalculatorUiModel.result > 0
                                            && isLastOperationSubtract
                                    calculatorButton.copy(isEnabled = newIsEnabled)
                                }

                                CalculatorOperatorButtonUiEvent.Decimal -> {
                                    val isLastOperationMultiply: Boolean = currentCalculatorUiModel
                                        .historyOperations
                                        .lastOrNull() == CalculatorOperatorButtonUiEvent.Multiply
                                    val hasMultiplyDecimals: Boolean =
                                        currentCalculatorUiModel.multiply % 1 != 0f

                                    val isLastOperationDivision: Boolean = currentCalculatorUiModel
                                        .historyOperations
                                        .lastOrNull() == CalculatorOperatorButtonUiEvent.Division
                                    val hasDivisionDecimals: Boolean =
                                        currentCalculatorUiModel.division % 1 != 0f

                                    val newIsEnabled: Boolean = (isLastOperationMultiply &&
                                            !currentCalculatorUiModel.isNextOperationInitialDecimal &&
                                            !hasMultiplyDecimals) ||
                                            (isLastOperationDivision &&
                                                    !currentCalculatorUiModel.isNextOperationInitialDecimal &&
                                                    !hasDivisionDecimals)

                                    calculatorButton.copy(isEnabled = newIsEnabled)
                                }

                                CalculatorOperatorButtonUiEvent.Delete -> {
                                    calculatorButton.copy(
                                        isEnabled = currentCalculatorUiModel.result > 0 ||
                                                currentCalculatorUiModel.historyOperations.isNotEmpty()
                                    )
                                }
                            }
                        }
                    )
                }
            )

        _calculatorButtonsUi = calculatorButtonsUiUpdated
    }

    fun calculatorToSaleProcess(navigateToSalesProcessGraph: (calculatorTotal: Float) -> Unit) {
        navigateToSalesProcessGraph(currentCalculatorUiModel.result)
    }
}
