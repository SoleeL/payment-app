package com.soleel.paymentapp.feature.salesprocess.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.soleel.paymentapp.feature.salesprocess.SalesProcessViewModel
import com.soleel.paymentapp.feature.salesprocess.components.SalesProcessSummaryHeader

@Composable
fun PaymentTypeSelectionScreen(
    salesProcessViewModel: SalesProcessViewModel,
    whenSelectingCash: () -> Unit,
    whenSelectingDebit: () -> Unit,
    whenSelectingCredit: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            SalesProcessSummaryHeader(
                calculatorTotal = salesProcessViewModel.salesProcessUiModel.calculatorTotal,
            )

            val functionName = Thread.currentThread().stackTrace
                .firstOrNull { it.methodName.contains("Screen") }?.methodName ?: "Unknown"

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = functionName)
            }
        }
    )
}