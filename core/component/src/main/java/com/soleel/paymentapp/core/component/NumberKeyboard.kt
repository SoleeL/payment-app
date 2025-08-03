package com.soleel.paymentapp.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.soleel.paymentapp.core.ui.visualtransformation.CLPCurrencyVisualTransformation

data class KeyboardInputUiState(
    val value: Int? = null,
    val isConfidential: Boolean = false
)

enum class NumberKeyboardButtonType {
    NUMBER,
    OPERATION
}

data class NumberKeyboardButtonUiState(
    val value: String,
    @DrawableRes val icon: Int?,
    val span: Int = 1,
    val type: NumberKeyboardButtonType,
    val isEnabledEvaluator: () -> Boolean = { true },
    val onClick: () -> Unit
)

@Composable
fun NumberKeyboard(
    inputUiState: KeyboardInputUiState,
    showKeyboardInput: Boolean,
    visualTransformation: VisualTransformation?,
    keyboardButtons: List<NumberKeyboardButtonUiState>,
    columnsGrid: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {

            if (showKeyboardInput) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        if (inputUiState.isConfidential) {
                            repeat(
                                times = inputUiState.value.toString().length,
                                action = {
                                    Text(
                                        text = " * ",
                                        style = MaterialTheme.typography.displayMedium,
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        maxLines = 1
                                    )
                                }
                            )
                        } else {
                            val keyboardInputUiStateValue =
                                if (visualTransformation != null) {
                                    visualTransformation
                                        .filter(AnnotatedString(text = inputUiState.value.toString()))
                                        .text.toString()
                                } else {
                                    inputUiState.value.toString()
                                }

                            Text(
                                text = keyboardInputUiStateValue,
                                style = MaterialTheme.typography.displayMedium,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                )
            }

            BoxWithConstraints(
                content = {
                    val buttonSize = (maxWidth - 2 * 16.dp) / columnsGrid

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columnsGrid),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        content = {
                            items(
                                items = keyboardButtons,
                                span = { GridItemSpan(it.span) },
                                itemContent = { button ->
                                    Button(
                                        onClick = button.onClick,
                                        modifier = if (button.span == 1) {
                                            Modifier.size(buttonSize)
                                        } else {
                                            Modifier
                                                .fillMaxWidth()
                                                .height(buttonSize)
                                        },
                                        enabled = button.isEnabledEvaluator(),
                                        shape = if (button.span == 1) {
                                            CircleShape
                                        } else {
                                            RoundedCornerShape(50)
                                        },
                                        colors = if (button.type == NumberKeyboardButtonType.NUMBER) {
                                            ButtonDefaults.buttonColors()
                                        } else {
                                            ButtonDefaults.buttonColors(
                                                containerColor = Color.DarkGray,
                                                contentColor = Color.White
                                            )
                                        },
                                        content = {
                                            if (button.icon != null) {
                                                Icon(
                                                    painter = painterResource(id = button.icon),
                                                    contentDescription = null,
                                                )
                                            } else {
                                                Text(
                                                    text = button.value,
                                                    style = MaterialTheme.typography.headlineLarge,
                                                )
                                            }
                                        }
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


@Preview(showBackground = true)
@Composable
fun NumberKeyboardPreview() {
    val sampleInputState = KeyboardInputUiState(
        value = 1234,
        isConfidential = true
    )

    val buttonList = listOf(
        NumberKeyboardButtonUiState(
            value = "7",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 1") }
        ),
        NumberKeyboardButtonUiState(
            value = "8",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "9",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "%",
            icon = null,
            type = NumberKeyboardButtonType.OPERATION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),

        NumberKeyboardButtonUiState(
            value = "4",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "5",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "6",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "x",
            icon = null,
            type = NumberKeyboardButtonType.OPERATION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),

        NumberKeyboardButtonUiState(
            value = "1",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "2",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "3",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "/",
            icon = null,
            type = NumberKeyboardButtonType.OPERATION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),

        NumberKeyboardButtonUiState(
            value = "C",
            icon = null,
            type = NumberKeyboardButtonType.OPERATION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "0",
            icon = null,
            type = NumberKeyboardButtonType.NUMBER,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        NumberKeyboardButtonUiState(
            value = "<-",
            icon = null,
            type = NumberKeyboardButtonType.OPERATION,
            isEnabledEvaluator = { sampleInputState.value != null },
            onClick = { println("Pressed DEL") }
        ),
        NumberKeyboardButtonUiState(
            value = "-",
            icon = null,
            type = NumberKeyboardButtonType.OPERATION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),

        NumberKeyboardButtonUiState(
            value = "CONFIRMAR",
            icon = null,
            span = 4,
            type = NumberKeyboardButtonType.OPERATION,
            isEnabledEvaluator = { sampleInputState.value != null },
            onClick = { println("Pressed DEL") }
        )
    )

    NumberKeyboard(
        inputUiState = sampleInputState,
        showKeyboardInput = true,
        visualTransformation = CLPCurrencyVisualTransformation(),
        keyboardButtons = buttonList,
        columnsGrid = 4
    )
}