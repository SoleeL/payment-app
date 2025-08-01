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
                        navigateToFailedPayment = {},
                        navigateToProcessPayment = {}
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
    navigateToFailedPayment: () -> Unit, // TODO: IMplementar rechazo por KSN para prueba
    navigateToProcessPayment: () -> Unit
) {
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
        verticalArrangement = Arrangement.SpaceBetween,
        content = {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.Center,
                content = {
                    repeat(pinpadViewModel.pinpadUiState.pin.length) {
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
                        items = pinpadViewModel.pinpadButtonsUiEvent,
                        itemContent = { buttonUiEvent ->
                            val buttonUiState = when (buttonUiEvent) {
                                is PinpadButtonUiEvent.WhenNumberIsDigested -> buttonUiEvent.buttonUiState
                                is PinpadButtonUiEvent.WhenCancelIsPressed -> buttonUiEvent.buttonUiState
                                is PinpadButtonUiEvent.WhenDeleteIsPressed -> buttonUiEvent.buttonUiState
                            }

                            Button(
                                onClick = {
                                    pinpadViewModel.onPinpadButtonUiEvent(buttonUiEvent, onCancel)
                                },
                                modifier = Modifier.aspectRatio(1f),
                                enabled = buttonUiState.isEnabled && pinpadViewModel.pinpadUiState.confirmingPinUiState != ConfirmingPinUiState.Confirming,
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
                confirmingPinUiState = pinpadViewModel.pinpadUiState.confirmingPinUiState,
                onConfirm = {
                    pinpadViewModel.onConfirmPinpadInput(navigateToProcessPayment = navigateToProcessPayment)
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