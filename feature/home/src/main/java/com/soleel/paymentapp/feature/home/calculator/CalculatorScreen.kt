package com.soleel.paymentapp.feature.home.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.ShortDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.core.ui.visualtransformation.CLPCurrencyVisualTransformation

@LongDevicePreview
@Composable
fun CalculatorScreenLongPreview() {
    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    CalculatorScreen(navigateToSalesProcessGraph = { })
                }
            )
        }
    )
}

@ShortDevicePreview
@Composable
fun CalculatorScreenShortPreview() {
    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    CalculatorScreen(navigateToSalesProcessGraph = { })
                }
            )
        }
    )
}

@Composable
fun CalculatorScreen(
    calculatorViewModel: CalculatorViewModel = hiltViewModel(),
    navigateToSalesProcessGraph: (amount: Int) -> Unit
) {
    val currentItemUi: CalculatorUiModel = calculatorViewModel.currentCalculatorUiModel

    val currencyVisualTransformation by remember(
        calculation = { mutableStateOf(CLPCurrencyVisualTransformation()) }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        content = {
            ItemInCalculator(itemInCalculator = currentItemUi)

            Spacer(modifier = Modifier.weight(1f))

            calculatorViewModel.calculatorButtonsUi.forEach(
                action = { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            row.forEach(
                                action = { calculatorButton ->
                                    CalculatorButton(
                                        modifier = Modifier.weight(calculatorButton.weight),
                                        value = calculatorButton.value,
                                        isEnabled = calculatorButton.isEnabled,
                                        isNumber = calculatorButton.operator == CalculatorOperatorButtonUiEvent.Number,
                                        onClick = {
                                            calculatorViewModel.onButtonCalculatorEvent(
                                                calculatorButton
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Button(
                        onClick = {
                            calculatorViewModel.calculatorToSaleProcess(
                                navigateToSalesProcessGraph = navigateToSalesProcessGraph
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(4.dp),
                        enabled = currentItemUi.result > 0,
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        ),
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                content = {
                                    val itemInCartTotalAmountCLP: String =
                                        currencyVisualTransformation
                                            .filter(
                                                AnnotatedString(
                                                    text = currentItemUi.result.toInt().toString()
                                                )
                                            )
                                            .text.toString()
                                    Text(
                                        text = "PAGAR",
                                        style = MaterialTheme.typography.headlineSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = itemInCartTotalAmountCLP,
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun ItemInCalculator(
    itemInCalculator: CalculatorUiModel,
) {
    val currencyVisualTransformation by remember(
        calculation = { mutableStateOf(CLPCurrencyVisualTransformation()) }
    )

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                content = {
                    Column(
                        modifier = if (itemInCalculator.historyOperations.isNotEmpty()) {
                            Modifier
                                .background(color = Color.Transparent)
                                .padding(end = 4.dp)
                        } else {
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 6.dp)
                        },
                        content = {
                            val valueAmount: Int = itemInCalculator.value.toInt()
                            val valueAmountCLP: String = currencyVisualTransformation
                                .filter(AnnotatedString(text = valueAmount.toString()))
                                .text.toString()

                            Text(
                                text = valueAmountCLP,
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (itemInCalculator.historyOperations.isNotEmpty()) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onPrimary
                                }
                            )
                        }
                    )
                    if (itemInCalculator.historyOperations.contains(
                            CalculatorOperatorButtonUiEvent.Multiply
                        )
                    ) {
                        Column(
                            modifier = if (itemInCalculator.historyOperations.lastOrNull() != CalculatorOperatorButtonUiEvent.Multiply) {
                                Modifier
                                    .background(color = Color.Transparent)
                                    .padding(start = 2.dp, end = 4.dp)
                            } else {
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 6.dp)
                            },
                            content = {
                                val multiply: String = if (
                                    itemInCalculator
                                        .historyOperations
                                        .lastOrNull() == CalculatorOperatorButtonUiEvent.Multiply &&
                                    itemInCalculator.isNextOperationInitialDecimal
                                ) {
                                    itemInCalculator.multiply.toInt().toString() + "."
                                } else if (itemInCalculator.multiply == 0f) {
                                    ""
                                } else if (itemInCalculator.multiply % 1 != 0f) {
                                    itemInCalculator.multiply.toString()
                                } else {
                                    itemInCalculator.multiply.toInt().toString()
                                }
                                Text(
                                    text = "x $multiply", // TODO: Cambiar esto por una transformacion visual
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = if (itemInCalculator.historyOperations.lastOrNull() != CalculatorOperatorButtonUiEvent.Multiply) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary
                                    }
                                )
                            }
                        )
                    }
                    if (itemInCalculator.historyOperations.contains(
                            CalculatorOperatorButtonUiEvent.Division
                        )
                    ) {
                        Column(
                            modifier = if (itemInCalculator.historyOperations.lastOrNull() != CalculatorOperatorButtonUiEvent.Division) {
                                Modifier
                                    .background(color = Color.Transparent)
                                    .padding(start = 2.dp, end = 4.dp)
                            } else {
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 6.dp)
                            },
                            content = {
                                val division: String = if (
                                    itemInCalculator
                                        .historyOperations
                                        .lastOrNull() == CalculatorOperatorButtonUiEvent.Division &&
                                    itemInCalculator.isNextOperationInitialDecimal
                                ) {
                                    itemInCalculator.division.toInt().toString() + "."
                                } else if (itemInCalculator.division == 0f) {
                                    ""
                                } else if (itemInCalculator.division % 1 != 0f) {
                                    itemInCalculator.division.toString()
                                } else {
                                    itemInCalculator.division.toInt().toString()
                                }
                                Text(
                                    text = "/ $division",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = if (itemInCalculator.historyOperations.lastOrNull() != CalculatorOperatorButtonUiEvent.Division) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary
                                    }
                                )
                            }
                        )
                    }
                    if (itemInCalculator.historyOperations.contains(
                            CalculatorOperatorButtonUiEvent.Subtract
                        )
                    ) {
                        Column(
                            modifier = if (itemInCalculator.historyOperations.lastOrNull() != CalculatorOperatorButtonUiEvent.Subtract) {
                                Modifier
                                    .background(color = Color.Transparent)
                                    .padding(start = 2.dp)
                            } else {
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 6.dp)
                            },
                            content = {
                                val subtractAmountCLP: String =
                                    if (itemInCalculator.subtract == 0f) {
                                        ""
                                    } else {
                                        itemInCalculator.subtract.toInt().toString()
                                        val subtractAmount: Int =
                                            itemInCalculator.subtract.toInt()
                                        currencyVisualTransformation
                                            .filter(AnnotatedString(text = subtractAmount.toString()))
                                            .text.toString()
                                    }

                                Text(
                                    text = "- $subtractAmountCLP",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = if (itemInCalculator.historyOperations.lastOrNull() != CalculatorOperatorButtonUiEvent.Subtract) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary
                                    }
                                )
                            }
                        )
                    }
                }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
                content = {
                    val resultAmount: Int = itemInCalculator.result.toInt()
                    val resultAmountCLP: String = currencyVisualTransformation
                        .filter(
                            AnnotatedString(text = resultAmount.toString())
                        )
                        .text.toString()

                    Text(
                        text = "=",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = if (itemInCalculator.result < 0f) "- $resultAmountCLP" else resultAmountCLP,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            )
        }
    )
}

@ShortDevicePreview
@Composable
fun ItemInCalculatorPreview() {
    val currentItemInCalculator: CalculatorUiModel = CalculatorUiModel(
        name = "item 2",
        value = 25000f,
        multiply = 3f,
        division = 1f,
        subtract = 0f,
        result = 75000f
    )

    Column(
        content = {
            ItemInCalculator(
                itemInCalculator = currentItemInCalculator
            )
        }
    )
}

@Composable
fun CalculatorButton(
    modifier: Modifier,
    value: String,
    isEnabled: Boolean,
    isNumber: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(20),
        colors = if (isNumber) {
            ButtonDefaults.buttonColors()
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            )
        },
        content = {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    )
}

@Composable
fun AlertDialogCalculator(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancelar")
            }
        }
    )
}