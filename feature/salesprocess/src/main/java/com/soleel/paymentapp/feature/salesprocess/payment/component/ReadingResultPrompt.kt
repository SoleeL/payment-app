package com.soleel.paymentapp.feature.salesprocess.payment.component

import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soleel.paymentapp.core.ui.R

@Composable
fun SuccessPrompt() {
    ReadingResultPrompt(
        message = "Lectura de tarjeta exitosa",
        color = MaterialTheme.colorScheme.tertiary,
        iconId = R.drawable.ic_check_circle
    )
}

@Composable
fun FailurePrompt() {
    ReadingResultPrompt(
        message = "Lectura de tarjeta fallida",
        color = MaterialTheme.colorScheme.error,
        iconId = R.drawable.ic_error_circle
    )
}

@Composable
fun ReadingResultPrompt(
    message: String,
    color: Color,
    @DrawableRes iconId: Int
) {
    val transition = rememberInfiniteTransition()

    val waveRadius by transition.animateFloat(
        initialValue = 150f,
        targetValue = 750f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val waveAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            Text(
                text = message,
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
                                color = color,
                                radius = waveRadius,
                                center = center,
                                alpha = waveAlpha
                            )
                        }
                    )

                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = "error icon",
                        tint = color,
                        modifier = Modifier.size(128.dp)
                    )
                }
            )
        }
    )
}