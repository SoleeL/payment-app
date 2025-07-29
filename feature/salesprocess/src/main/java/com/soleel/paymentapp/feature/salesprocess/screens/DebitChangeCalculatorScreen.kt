package com.soleel.paymentapp.feature.salesprocess.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun DebitChangeCalculatorScreen() {
    val functionName = Thread.currentThread().stackTrace
        .firstOrNull { it.methodName.contains("Screen") }?.methodName ?: "Unknown"

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = functionName)
    }
}