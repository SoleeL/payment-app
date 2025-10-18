package com.soleel.paymentapp.core.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.soleel.paymentapp.core.ui.visualtransformation.CLPCurrencyVisualTransformation


enum class SelectionItemType {
    OPTION,
    ACTION
}

data class SelectionItemUiState(
    val value: String,
    val type: SelectionItemType,
    val isEnabledEvaluator: () -> Boolean = { true },
    val onClick: () -> Unit
)

@Composable
fun ListSelection(
    listSelectionItems: List<SelectionItemUiState>,
    visualTransformation: VisualTransformation? = null
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            items(
                items = listSelectionItems,
                itemContent = { item ->
                    val buttonColors = if (item.type == SelectionItemType.ACTION) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = item.onClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = item.isEnabledEvaluator(),
                        colors = buttonColors,
                        shape = RoundedCornerShape(12.dp),
                        content = {
                            val itemValueFormated: String = if (visualTransformation != null &&
                                item.type == SelectionItemType.OPTION
                            ) {
                                visualTransformation
                                    .filter(AnnotatedString(text = item.value))
                                    .text.toString()
                            } else {
                                item.value
                            }
                            Text(
                                text = itemValueFormated,
                                style = MaterialTheme.typography.headlineLarge
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
fun ListSelectionPreview() {

    val selectionList: List<SelectionItemUiState> = listOf(
        SelectionItemUiState(
            value = "5000",
            type = SelectionItemType.OPTION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 1") }
        ),
        SelectionItemUiState(
            value = "10000",
            type = SelectionItemType.OPTION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        SelectionItemUiState(
            value = "15000",
            type = SelectionItemType.OPTION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),
        SelectionItemUiState(
            value = "20000",
            type = SelectionItemType.OPTION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        ),

        SelectionItemUiState(
            value = "Sin vuelto",
            type = SelectionItemType.ACTION,
            isEnabledEvaluator = { true },
            onClick = { println("Pressed 2") }
        )
    )

    ListSelection(
        listSelectionItems = selectionList,
        visualTransformation = CLPCurrencyVisualTransformation()
    )
}