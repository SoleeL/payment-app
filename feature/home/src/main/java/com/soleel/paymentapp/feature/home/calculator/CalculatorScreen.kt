package com.soleel.paymentapp.feature.home.calculator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soleel.paymentapp.core.component.NumberKeyboard
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.core.ui.visualtransformation.CLPCurrencyVisualTransformation


@Composable
fun CalculatorScreen(
    calculatorViewModel: CalculatorViewModel = hiltViewModel(),
    navigateToSalesProcessGraph: (calculatorInput: Int) -> Unit
) {

    calculatorViewModel.setNavigationCallback(
        callback = navigateToSalesProcessGraph
    )

    Box(
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
        content = {
            NumberKeyboard(
                inputUiState = calculatorViewModel.keyboardInputUiState,
                showKeyboardInput = true,
                visualTransformation = CLPCurrencyVisualTransformation(),
                keyboardButtons = calculatorViewModel.calculatorButtonsUi,
                columnsGrid = 3
            )
        }
    )
}

@LongDevicePreview
@Composable
private fun CalculatorScreenLongPreview() {

    val calculatorViewModel: CalculatorViewModel =
        CalculatorViewModel()

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    CalculatorScreen(
                        calculatorViewModel = calculatorViewModel,
                        navigateToSalesProcessGraph = { _ -> }
                    )
                }
            )
        }
    )
}