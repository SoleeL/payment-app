package com.soleel.paymentapp.feature.salesprocess.payment.contactlessreading

import androidx.activity.compose.BackHandler
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import com.soleel.paymentapp.core.ui.R
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.salesprocess.payment.component.FailurePrompt
import com.soleel.paymentapp.feature.salesprocess.payment.component.SuccessPrompt
import com.soleel.paymentapp.feature.salesprocess.payment.utils.ReadingUiState


@LongDevicePreview
@Composable
private fun ContactlessReadingScreenLongPreview() {
    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    ContactlessReadingScreen(
                        navigateToContactReading = {},
                        navigateToVerificationMethod = { _, _ -> },
                        navigateToFailedPayment = { _, _ -> }
                    )
                }
            )
        }
    )
}

@Composable
fun ContactlessReadingScreen(
    contactlessReadingViewModel: ContactlessReadingViewModel = hiltViewModel(),
    navigateToContactReading: () -> Unit,
    navigateToFailedPayment: (errorCode: String, errorMessage: String) -> Unit,
    navigateToVerificationMethod: (brand: String, last4: Int) -> Unit
) {
    BackHandler(enabled = true, onBack = { })

    LaunchedEffect(Unit) {
        contactlessReadingViewModel.startContactlessReading(
            withOtherReadingInterface = navigateToContactReading,
            onVerificationMethod = navigateToVerificationMethod,
            onFailedPayment = navigateToFailedPayment
        )
    }

    val contactlessReadingUiState: ReadingUiState by contactlessReadingViewModel.contactlessReadingUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            when (contactlessReadingUiState) {
                ReadingUiState.Reading -> ContactlessReadingPrompt()
                is ReadingUiState.Success -> SuccessPrompt()
                is ReadingUiState.Failure -> FailurePrompt()
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