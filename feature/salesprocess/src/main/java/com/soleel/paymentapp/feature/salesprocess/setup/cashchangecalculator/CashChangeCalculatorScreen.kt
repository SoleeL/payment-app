package com.soleel.paymentapp.feature.salesprocess.setup.cashchangecalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar


@LongDevicePreview
@Composable
private fun CashChangeCalculatorScreenLongPreview() {
    val fakeSavedStateHandle = SavedStateHandle(
        mapOf(
            "calculatorTotal" to 7000f,
            "tipTotal" to 1000f
        )
    )

    val cashChangeCalculatorViewModel: CashChangeCalculatorViewModel =
        CashChangeCalculatorViewModel(
            savedStateHandle = fakeSavedStateHandle
        )

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    CashChangeCalculatorScreen(
                        cashChangeCalculatorViewModel = cashChangeCalculatorViewModel,
                        navigateToRegisterSale = {}
                    )
                }
            )
        }
    )
}

@Composable
fun CashChangeCalculatorScreen(
    cashChangeCalculatorViewModel: CashChangeCalculatorViewModel = hiltViewModel(),
    navigateToRegisterSale: (cashChange:Float ) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {

            Spacer(modifier = Modifier.weight(1f))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(48.dp, 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(
                        items = cashChangeCalculatorViewModel.buttonsUiEvent,
                        itemContent = { buttonUiEvent ->
                            val buttonUiState = when (buttonUiEvent) {
                                is ButtonUiEvent.WhenNumberIsDigested -> buttonUiEvent.buttonUiState
                                is ButtonUiEvent.WhenClearIsPressed -> buttonUiEvent.buttonUiState
                                is ButtonUiEvent.WhenDeleteIsPressed -> buttonUiEvent.buttonUiState
                            }

                            Button(
                                onClick = {
                                    cashChangeCalculatorViewModel.onButtonUiEvent(
                                        buttonUiEvent
                                    )
                                },
                                modifier = Modifier.aspectRatio(1f),
                                enabled = buttonUiState.isEnabled,
                                shape = CircleShape,
                                colors = if (buttonUiEvent is ButtonUiEvent.WhenNumberIsDigested) {
                                    ButtonDefaults.buttonColors()
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color.DarkGray,
                                        contentColor = Color.White
                                    )
                                },
                                content = {
                                    Text(
                                        text = buttonUiState.value,
                                        style = MaterialTheme.typography.headlineLarge,
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