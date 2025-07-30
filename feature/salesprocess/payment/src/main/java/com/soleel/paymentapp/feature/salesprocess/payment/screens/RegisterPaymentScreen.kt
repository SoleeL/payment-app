package com.soleel.paymentapp.feature.salesprocess.payment.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soleel.paymentapp.core.common.retryflow.RetryableFlowTrigger
import com.soleel.paymentapp.core.component.SalesSummaryHeader
import com.soleel.paymentapp.core.ui.R
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.domain.payment.RequestConfirmingPaymentUseCaseMock
import com.soleel.paymentapp.domain.payment.RequestValidationPaymentUseCaseMock
import com.soleel.paymentapp.domain.payment.SavePaymentUseCaseMock
import com.soleel.paymentapp.feature.salesprocess.payment.PaymentStepUiState
import com.soleel.paymentapp.feature.salesprocess.payment.PaymentViewModel

@LongDevicePreview
@Composable
private fun ProcessPaymentScreenLongPreview() {
    val fakeSavedStateHandle = SavedStateHandle(
        mapOf("calculatorTotal" to 7000f)
    )

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    ProcessPaymentScreen(
                        paymentViewModel = PaymentViewModel(
                            savedStateHandle = fakeSavedStateHandle,
                            requestValidationPaymentUseCase = RequestValidationPaymentUseCaseMock(),
                            requestConfirmationPaymentUseCase = RequestConfirmingPaymentUseCaseMock(),
                            savePaymentUseCase = SavePaymentUseCaseMock(),
                            retryableFlowTrigger = RetryableFlowTrigger(),
                        ),
                        navigateToOutcomeGraph = {}
                    )
                }
            )
        }
    )
}

@Composable
fun ProcessPaymentScreen(
    paymentViewModel: PaymentViewModel,
    navigateToOutcomeGraph: () -> Unit
) {
    LaunchedEffect(Unit) {
        paymentViewModel.startPaymentProcess()
    }

    val paymentStep: PaymentStepUiState by paymentViewModel.paymentStepUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            SalesSummaryHeader(
                calculatorTotal = paymentViewModel.sale.calculatorTotal,

                tipTotal = paymentViewModel.sale.tipTotal,
                paymentMethodSelected = paymentViewModel.sale.paymentMethodSelected,
                creditInstalmentsSelected = paymentViewModel.sale.creditInstalmentsSelected,
                debitChangeSelected = paymentViewModel.sale.debitChangeSelected
            )
            AdBanner()

            PaymentProcessingChecklist(
                paymentStep = paymentStep,
                onFinish = navigateToOutcomeGraph
            )
        }
    )
}

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp)),
        content = {
            Image(
                painter = painterResource(id = R.drawable.ad_deinn),
                contentDescription = "Publicidad durante procesamiento",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.Center),
                contentScale = ContentScale.FillWidth
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .size(24.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar anuncio",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            Text(
                text = "Ads by Google",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontSize = 10.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    )
}

@Composable
fun PaymentProcessingChecklist(
    modifier: Modifier = Modifier,
    paymentStep: PaymentStepUiState,
    onFinish: () -> Unit = {}
) {
    val steps = listOf("Validando pago", "Confirmando pago", "Guardando pago")
    val currentStepIndex = when (paymentStep) {
        PaymentStepUiState.Idle -> -1
        PaymentStepUiState.Validating -> 0
        PaymentStepUiState.Confirming -> 1
        PaymentStepUiState.Saving -> 2
        PaymentStepUiState.Done -> 3
        is PaymentStepUiState.Error -> {
            // Error lo colocamos en el paso donde ocurrió
            paymentStep.step.ordinal
        }
    }

    val isProcessingFinished = currentStepIndex >= steps.size

    LaunchedEffect(isProcessingFinished) {
        if (isProcessingFinished) {
            onFinish()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val prefix = when {
                index < currentStepIndex -> "[ x ]"
                index == currentStepIndex && !isProcessingFinished -> "[ > ]"
                else -> "[  ]"
            }

            Text(
                text = "$prefix $step",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (paymentStep is PaymentStepUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error en paso ${paymentStep.step}: ${paymentStep.message ?: "desconocido"}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

//
//@Composable
//fun PaymentProcessingChecklist(
//    modifier: Modifier = Modifier,
//    onFinish: () -> Unit = {}
//) {
//    val steps = listOf(
//        "Validando pago",
//        "Confirmando pago",
//        "Guardando pago"
//    )
//
//    var currentStepIndex by remember { mutableIntStateOf(0) }
//    var animatedDots by remember { mutableStateOf("") }
//    var isProcessingFinished by remember { mutableStateOf(false) }
//
//    LaunchedEffect(currentStepIndex) {
//        if (currentStepIndex < steps.size) {
//            while (true) {
//                repeat(4) { i ->
//                    animatedDots = ".".repeat(i)
//                    delay(250)
//                }
//            }
//        }
//    }
//
//    LaunchedEffect(animatedDots) {
//        if (animatedDots == "...") {
//            delay(250)
//            if (currentStepIndex < steps.size) {
//                currentStepIndex++
//                if (currentStepIndex == steps.size) {
//                    isProcessingFinished = true
//                    onFinish()
//                }
//            }
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth(),
//        content = {
//            steps.forEachIndexed { index, step ->
//                val prefix = when {
//                    index < currentStepIndex -> "[ x ]"
//                    index == currentStepIndex -> "[  ]"
//                    else -> "[  ]"
//                }
//
//                val text = if (index == currentStepIndex && !isProcessingFinished) {
//                    "$prefix $step$animatedDots"
//                } else {
//                    "$prefix $step"
//                }
//
//                Text(
//                    text = text,
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.padding(vertical = 8.dp)
//                )
//            }
//        }
//    )
//}