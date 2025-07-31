package com.soleel.paymentapp.feature.salesprocess.payment.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.soleel.paymentapp.core.component.SalesSummaryHeader
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.data.preferences.developer.DeveloperPreferencesMock
import com.soleel.paymentapp.domain.payment.RequestConfirmingPaymentUseCaseMock
import com.soleel.paymentapp.domain.payment.RequestValidationPaymentUseCaseMock
import com.soleel.paymentapp.domain.payment.SavePaymentUseCaseMock
import com.soleel.paymentapp.domain.reading.ContactReadingUseCaseMock
import com.soleel.paymentapp.domain.reading.ContactlessReadingUseCaseMock
import com.soleel.paymentapp.feature.salesprocess.payment.ConfirmingPinUiState
import com.soleel.paymentapp.feature.salesprocess.payment.FailedPayment
import com.soleel.paymentapp.feature.salesprocess.payment.PaymentViewModel
import com.soleel.paymentapp.feature.salesprocess.payment.PinpadButtonUiEvent
import kotlinx.coroutines.delay


@LongDevicePreview
@Composable
private fun CashChangeCalculatorScreenLongPreview() {
    val fakeSavedStateHandle = SavedStateHandle(
        mapOf("calculatorTotal" to 7000f)
    )

    val paymentViewModel: PaymentViewModel = PaymentViewModel(
        savedStateHandle = fakeSavedStateHandle,
        contactlessReadingUseCase = ContactlessReadingUseCaseMock(
            DeveloperPreferencesMock()
        ),
        contactReadingUseCase = ContactReadingUseCaseMock(),
        requestValidationPaymentUseCase = RequestValidationPaymentUseCaseMock(),
        requestConfirmationPaymentUseCase = RequestConfirmingPaymentUseCaseMock(),
        savePaymentUseCase = SavePaymentUseCaseMock(),
    )

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    PinpadScreen(
                        paymentViewModel = paymentViewModel,
                        onCancel = { },
                        navigateToRegisterPayment = {},
                        navigateToFailedPayment = {}
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
    navigateToFailedPayment: () -> Unit
//    navigateToOutcomeGraph: (paymentResult: PaymentResult) -> Unit
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

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.Center,
                content = {
                    repeat(paymentViewModel.pinpadUiState.pin.length) {
                        Text(
                            text = " * ",
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            maxLines = 1
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
                                enabled = buttonUiState.isEnabled && paymentViewModel.pinpadUiState.confirmingPinUiState != ConfirmingPinUiState.Confirming,
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

            ConfirmButton(
                confirmingPinUiState = paymentViewModel.pinpadUiState.confirmingPinUiState,
                onConfirm = {
                    paymentViewModel.onConfirmPinpadInput(navigateToRegisterPayment = navigateToRegisterPayment)
                }
            )
        }
    )
}

@Composable
fun ConfirmButton(
    confirmingPinUiState: ConfirmingPinUiState,
    onConfirm: () -> Unit
) {
    var animatedText: String by remember { mutableStateOf("CONFIRMANDO") }

    LaunchedEffect(
        key1 = confirmingPinUiState,
        block = {
            if (confirmingPinUiState == ConfirmingPinUiState.Confirming) {
                val base: String = "CONFIRMANDO"
                while (confirmingPinUiState == ConfirmingPinUiState.Confirming) {
                    repeat(
                        times = 4,
                        action = { i ->
                            animatedText = base + ".".repeat(i)
                            delay(250)
                        }
                    )
                }
            } else {
                animatedText = "CONFIRMAR"
            }
        }
    )

    Button(
        onClick = onConfirm,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .height(80.dp),
        enabled = confirmingPinUiState != ConfirmingPinUiState.Confirming,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        content = {
            Text(
                text = animatedText,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    )
}