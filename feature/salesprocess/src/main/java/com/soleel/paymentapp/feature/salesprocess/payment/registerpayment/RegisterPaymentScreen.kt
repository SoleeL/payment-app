package com.soleel.paymentapp.feature.salesprocess.payment.registerpayment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.salesprocess.component.AdBanner
import kotlinx.coroutines.delay


@Composable
fun ProcessPaymentScreen(
    registerPaymentViewModel: RegisterPaymentViewModel = hiltViewModel(),
    navigateToFailedPayment: (errorCode: String, errorMessage: String) -> Unit,
    navigateToRegisterSale: (uuidPayment: String) -> Unit,
    method: PaymentMethodEnum,
    amount: Int,
    instalments: Int?,
    applicationLabel: String,
    aid: String,
    last4: String,
) {
    BackHandler(enabled = true, onBack = { })

    LaunchedEffect(Unit) {
        registerPaymentViewModel.startPaymentProcess(
            navigateToFailedPayment,
            navigateToRegisterSale,
            method,
            amount,
            instalments,
            applicationLabel,
            aid,
            last4
        )
    }

    val paymentStep: PaymentStepUiState by registerPaymentViewModel.paymentStepUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            AdBanner()

            PaymentProcessingChecklist(paymentStep = paymentStep)
        }
    )
}

@Composable
fun PaymentProcessingChecklist(
    modifier: Modifier = Modifier,
    paymentStep: PaymentStepUiState
) {
    val steps = listOf("Validando pago", "Confirmando pago", "Guardando pago")

    var animatedDots by remember { mutableStateOf("") }
    val currentStepIndex = when (paymentStep) {
        PaymentStepUiState.Validating -> 0
        PaymentStepUiState.Confirming -> 1
        PaymentStepUiState.Saving -> 2
        PaymentStepUiState.Done -> 3
    }

    LaunchedEffect(currentStepIndex) {
        if (currentStepIndex in 0 until steps.size) {
            while (true) {
                repeat(4) { i ->
                    animatedDots = ".".repeat(i)
                    delay(250)
                }
            }
        } else {
            animatedDots = ""
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val prefix = when {
                index < currentStepIndex -> "[ x ]"
                index == currentStepIndex -> "[  ]"
                else -> "[  ]"
            }

            val text = if (index == currentStepIndex) {
                "$prefix $step$animatedDots"
            } else {
                "$prefix $step"
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}