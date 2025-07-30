package com.soleel.paymentapp.feature.salesprocess.payment.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soleel.paymentapp.core.component.SalesSummaryHeader
import com.soleel.paymentapp.core.ui.R
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.domain.payment.RequestConfirmingPaymentUseCaseMock
import com.soleel.paymentapp.domain.payment.RequestValidationPaymentUseCaseMock
import com.soleel.paymentapp.domain.payment.SavePaymentUseCaseMock
import com.soleel.paymentapp.feature.salesprocess.payment.PaymentViewModel
import com.soleel.paymentapp.feature.salesprocess.payment.ReadingUiState
import com.soleel.paymentapp.feature.salesprocess.payment.component.FailurePrompt
import com.soleel.paymentapp.feature.salesprocess.payment.component.SuccessPrompt
import kotlinx.coroutines.delay


@LongDevicePreview
@Composable
private fun ContactlessReadingScreenLongPreview() {
    val fakeSavedStateHandle = SavedStateHandle(
        mapOf("calculatorTotal" to 7000f)
    )

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    ContactlessReadingScreen(
                        paymentViewModel = PaymentViewModel(
                            savedStateHandle = fakeSavedStateHandle,
                            requestValidationPaymentUseCase = RequestValidationPaymentUseCaseMock(),
                            requestConfirmationPaymentUseCase = RequestConfirmingPaymentUseCaseMock(),
                            savePaymentUseCase = SavePaymentUseCaseMock(),
                        ),
                        navigateToContactReading = {},
                        navigateToVerificationMethod = {},
                        navigateToOutcomeGraph = {}
                    )
                }
            )
        }
    )
}

@Composable
fun ContactlessReadingScreen(
    paymentViewModel: PaymentViewModel,
    navigateToContactReading: () -> Unit,
    navigateToVerificationMethod: () -> Unit,
    navigateToOutcomeGraph: () -> Unit
) {
    val contactlessReadingUiState: ReadingUiState by paymentViewModel.contactlessReadingUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            SalesSummaryHeader(
                calculatorTotal = paymentViewModel.sale.calculatorTotal,

                tipTotal = paymentViewModel.sale.tipTotal,
                paymentMethodSelected = paymentViewModel.sale.paymentMethodSelected,
                creditInstalmentsSelected = paymentViewModel.sale.creditInstalmentsSelected,
                debitChangeSelected = paymentViewModel.sale.debitChangeSelected
            )

            when (contactlessReadingUiState) {
                ReadingUiState.Reading -> ContactlessReadingPrompt()
                ReadingUiState.Success -> {
                    LaunchedEffect(
                        key1 = Unit,
                        block = {
                            delay(timeMillis = 1000)
                            navigateToVerificationMethod()
                        }
                    )
                    SuccessPrompt()
                }

                ReadingUiState.Failure -> {
                    LaunchedEffect(
                        key1 = Unit,
                        block = {
                            delay(timeMillis = 1000)
                            if (true) {
                                navigateToContactReading()
                            } else {
                                TODO("IMPLEMENTAR SALIDA")
                                navigateToOutcomeGraph()
                            }
                        }
                    )
                    FailurePrompt()
                }
            }
        }
    )
}

@Composable
fun ContactlessReadingPrompt() {
    val transition = rememberInfiniteTransition()

    val waveRadius by transition.animateFloat(
        initialValue = 150f,
        targetValue = 750f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val waveAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val primary: Color = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            Text(
                text = "Acerque la tarjeta al icono en el dispositivo",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp),
                content = {
                    Canvas(
                        modifier = Modifier.fillMaxSize(),
                        onDraw = {
                            drawCircle(
                                color = primary,
                                radius = waveRadius,
                                center = center,
                                alpha = waveAlpha
                            )
                        }
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_contacless),
                        contentDescription = "Contactless icon",
                        tint = primary,
                        modifier = Modifier.size(128.dp)
                    )
                }
            )
        }
    )
}