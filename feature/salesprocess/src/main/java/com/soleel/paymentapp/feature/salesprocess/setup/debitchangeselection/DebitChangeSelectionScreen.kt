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


@Composable
fun DebitChangeSelectionScreen(
    navigateToPaymentProcess: (Float?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
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