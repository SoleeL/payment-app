package com.soleel.paymentapp.feature.salesprocess.payment.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.soleel.paymentapp.core.component.SalesSummaryHeader
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.salesprocess.payment.PinpadButtonUiEvent
import com.soleel.paymentapp.feature.salesprocess.payment.PaymentViewModel


@LongDevicePreview
@Composable
private fun CashChangeCalculatorScreenLongPreview() {
    val fakeSavedStateHandle = SavedStateHandle(
        mapOf("calculatorTotal" to 7000f)
    )

    val paymentViewModel: PaymentViewModel =
        PaymentViewModel(savedStateHandle = fakeSavedStateHandle)

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    PinpadScreen(
                        paymentViewModel = paymentViewModel,
                        onCancel = { },
                        navigateToRegisterPayment = {},
                        navigateToFailedSale = {}
                    )
                }
            )
        }
    )
}

@Composable
fun PinpadScreen(
    paymentViewModel: PaymentViewModel,
    onCancel: () -> Unit,
    navigateToRegisterPayment: () -> Unit,
    navigateToFailedSale: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            SalesSummaryHeader(
                calculatorTotal = paymentViewModel.sale.calculatorTotal,

                tipTotal = paymentViewModel.sale.tipTotal,
                paymentMethodSelected = paymentViewModel.sale.paymentMethodSelected,
                creditInstalmentsSelected = paymentViewModel.sale.creditInstalmentsSelected,
                debitChangeSelected = paymentViewModel.sale.debitChangeSelected
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.Center,
                content = {
                    repeat(paymentViewModel.pinpadUiState.pin.length) {
                        Text(
                            text = "* ",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(48.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(
                        items = paymentViewModel.pinpadButtonsUiEvent,
                        itemContent = { buttonUiEvent ->
                            val buttonUiState = when (buttonUiEvent) {
                                is PinpadButtonUiEvent.WhenNumberIsDigested -> buttonUiEvent.buttonUiState
                                is PinpadButtonUiEvent.WhenCancelIsPressed -> buttonUiEvent.buttonUiState
                                is PinpadButtonUiEvent.WhenDeleteIsPressed -> buttonUiEvent.buttonUiState
                            }

                            Button(
                                onClick = {
                                    paymentViewModel.onPinpadButtonUiEvent(buttonUiEvent, onCancel)
                                },
                                modifier = Modifier.aspectRatio(1f),
                                enabled = buttonUiState.isEnabled,
                                shape = CircleShape,
                                colors = if (buttonUiEvent is PinpadButtonUiEvent.WhenNumberIsDigested) {
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

            Button(
                onClick = {
                    paymentViewModel.onConfirmPinpadInput(
                        navigateToRegisterPayment = navigateToRegisterPayment,
                        navigateToFailedSale = navigateToFailedSale
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
                    .height(80.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                content = {
                    Text(
                        text = "CONFIRMAR",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        }
    )
}