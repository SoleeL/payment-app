package com.soleel.paymentapp.feature.salesprocess.setup.debitchangeselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soleel.paymentapp.core.component.SalesSummaryHeader
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.feature.salesprocess.setup.SetupViewModel


@Composable
fun DebitChangeSelectionScreen(
    setupViewModel: SetupViewModel,
    navigateToPaymentGraph: (sale: Sale) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            SalesSummaryHeader(
                calculatorTotal = setupViewModel.setupUiModel.calculatorTotal,
                paymentMethodSelected = setupViewModel.setupUiModel.paymentMethodSelected
            )

            Column(
                modifier = Modifier.padding(16.dp),
                content = {
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
    )
}