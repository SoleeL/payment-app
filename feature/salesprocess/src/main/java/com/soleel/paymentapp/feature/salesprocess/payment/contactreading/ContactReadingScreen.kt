package com.soleel.paymentapp.feature.salesprocess.payment.contactreading

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soleel.paymentapp.core.ui.R
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.salesprocess.payment.component.FailurePrompt
import com.soleel.paymentapp.feature.salesprocess.payment.component.SuccessPrompt
import com.soleel.paymentapp.feature.salesprocess.payment.utils.ReadingUiState

@LongDevicePreview
@Composable
private fun ContactReadingScreenLongPreview() {
    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    ContactReadingScreen(
                        navigateToFailedPayment = { _, _ -> },
                        navigateToVerificationMethod = { _, _, _ -> },

                        )
                }
            )
        }
    )
}

@Composable
fun ContactReadingScreen(
    contactReadingViewModel: ContactReadingViewModel = hiltViewModel(),
    navigateToFailedPayment: (errorCode: String, errorMessage: String) -> Unit,
    navigateToVerificationMethod: (applicationLabel: String, aid: String, last4: String) -> Unit
) {
    BackHandler(enabled = true, onBack = { })

    LaunchedEffect(Unit) {
        contactReadingViewModel.startContactReading(
            onFailedPayment = navigateToFailedPayment,
            onVerificationMethod = navigateToVerificationMethod
        )
    }

    val contactReadingUiState: ReadingUiState by contactReadingViewModel.contactReadingUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            when (contactReadingUiState) {
                ReadingUiState.Reading -> ContactReaderPrompt()
                is ReadingUiState.Success -> SuccessPrompt()
                is ReadingUiState.Failure -> FailurePrompt()
            }
        }
    )
}

@Composable
fun ContactReaderPrompt() {
    val transition = rememberInfiniteTransition()

    val waveHeight by transition.animateFloat(
        initialValue = 0f,
        targetValue = 900f,
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

    Box(
        modifier = Modifier.fillMaxSize(),
        content = {
            Canvas(
                modifier = Modifier.fillMaxSize(),
                onDraw = {
                    val rectWidth = size.width
                    val rectTop = size.height - waveHeight

                    drawRect(
                        color = primary,
                        topLeft = Offset(0f, rectTop),
                        size = Size(rectWidth, waveHeight),
                        alpha = waveAlpha
                    )
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                content = {
                    Text(
                        text = "Inserte el chip de su tarjeta en la ranura del dispositivo",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(200.dp),
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_contact),
                                contentDescription = "Contact icon",
                                tint = primary,
                                modifier = Modifier
                                    .rotate(180f)
                                    .size(128.dp)
                            )
                        }
                    )
                }
            )
        }
    )
}