package com.soleel.paymentapp.feature.salesprocess.payment.pinpad

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import kotlinx.coroutines.delay


@LongDevicePreview
@Composable
private fun CashChangeCalculatorScreenLongPreview() {


    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    PinpadScreen(
                        onCancel = { },
                        navigateToFailedPayment = { _, _ -> },
                        navigateToProcessPayment = { _, _ -> }
                    )
                }
            )
        }
    )
}

@Composable
fun PinpadScreen(
    pinpadViewModel: PinpadViewModel = hiltViewModel(),
    onCancel: () -> Unit,
    navigateToFailedPayment: (errorCode: String, errorMessage: String) -> Unit,
    navigateToProcessPayment: (pinBlock: String, ksn: String) -> Unit
) {

    val confirmingPinStepUiState by pinpadViewModel.confirmingPinStepUiState.collectAsState()

    BackHandler(
        enabled = true,
        onBack = {
            // TODO: Implementar modal
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pinpadViewModel.pinpadUiState.pin.length) {
                Text(
                    text = " * ",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    maxLines = 1
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(48.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(pinpadViewModel.pinpadButtonsUiEvent) { buttonUiEvent ->
                val buttonUiState = when (buttonUiEvent) {
                    is PinpadButtonUiEvent.WhenNumberIsDigested -> buttonUiEvent.buttonUiState
                    is PinpadButtonUiEvent.WhenCancelIsPressed -> buttonUiEvent.buttonUiState
                    is PinpadButtonUiEvent.WhenDeleteIsPressed -> buttonUiEvent.buttonUiState
                    is PinpadButtonUiEvent.WhenConfirmIsPressed -> TODO()
                }

                Button(
                    onClick = {
                        pinpadViewModel.onPinpadButtonUiEvent(buttonUiEvent, onCancel)
                    },
                    modifier = Modifier.aspectRatio(1f),
                    enabled = buttonUiState.isEnabled && confirmingPinStepUiState != ConfirmingPinStepUiState.Confirming,
                    shape = CircleShape,
                    colors = if (buttonUiEvent is PinpadButtonUiEvent.WhenNumberIsDigested) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        )
                    }
                ) {
                    Text(
                        text = buttonUiState.value,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            }
        }

        ConfirmButton(
            pinLenght = pinpadViewModel.pinpadUiState.pin.length,
            confirmingPinStepUiState = confirmingPinStepUiState,
            onConfirm = {
                pinpadViewModel.onPinpadButtonUiEvent(
                    event = PinpadButtonUiEvent.WhenConfirmIsPressed(
                        navigateToFailedPayment, navigateToProcessPayment
                    ),
                    navigateTo = {}
                )
            }
        )
    }
}

@Composable
fun ConfirmButton(
    pinLenght: Int,
    confirmingPinStepUiState: ConfirmingPinStepUiState,
    onConfirm: () -> Unit,
) {
    var animatedText by remember { mutableStateOf("CONFIRMANDO") }

    LaunchedEffect(confirmingPinStepUiState) {
        if (confirmingPinStepUiState == ConfirmingPinStepUiState.Confirming) {
            val base = "CONFIRMANDO"
            while (confirmingPinStepUiState == ConfirmingPinStepUiState.Confirming) {
                repeat(4) { i ->
                    animatedText = base + ".".repeat(i)
                    delay(250)
                }
            }
        } else {
            animatedText = "CONFIRMAR"
        }
    }

    Button(
        onClick = onConfirm,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .height(80.dp),
        enabled = confirmingPinStepUiState != ConfirmingPinStepUiState.Confirming && pinLenght >= 4,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = animatedText,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}