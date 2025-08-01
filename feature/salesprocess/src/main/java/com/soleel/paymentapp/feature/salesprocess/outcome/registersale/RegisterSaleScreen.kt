package com.soleel.paymentapp.feature.salesprocess.outcome.registersale

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
import com.soleel.paymentapp.feature.salesprocess.component.AdBanner
import kotlinx.coroutines.delay


@Composable
fun RegisterSaleScreen(
    registerSaleViewModel: RegisterSaleViewModel = hiltViewModel(),
    navigateToFailedSale: (errorCode: String, errorMessage: String) -> Unit,
    navigateToPendingSale: (uuidSale: String) -> Unit,
    navigateToSuccessfulSale: (uuidSale: String) -> Unit
) {
    BackHandler(enabled = true, onBack = { })

    LaunchedEffect(Unit) {
        registerSaleViewModel.startPaymentProcess(
            navigateToFailedSale,
            navigateToPendingSale,
            navigateToSuccessfulSale
        )
    }

    val saleRegisterStepUiState: SaleRegisterStepUiState by registerSaleViewModel.registerSaleStepUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            AdBanner()
            RegisterSaleProcessingChecklist(saleRegisterStepUiState = saleRegisterStepUiState)
        }
    )

}

@Composable
fun RegisterSaleProcessingChecklist(
    saleRegisterStepUiState: SaleRegisterStepUiState
) {
    val steps = listOf("Almacenando venta", "Registrando venta")

    var animatedDots by remember { mutableStateOf("") }
    val currentStepIndex = when (saleRegisterStepUiState) {
        SaleRegisterStepUiState.Storing -> 0
        SaleRegisterStepUiState.Recording -> 1
        SaleRegisterStepUiState.Done -> 2
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

    Column(modifier = Modifier.fillMaxWidth()) {
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