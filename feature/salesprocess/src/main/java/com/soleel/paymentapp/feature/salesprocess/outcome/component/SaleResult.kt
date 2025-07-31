package com.soleel.paymentapp.feature.salesprocess.outcome.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.soleel.paymentapp.core.model.outcomeprocess.RegisterSaleResult
import com.soleel.paymentapp.core.ui.R


@Composable
fun FailedSaleResult(result: RegisterSaleResult) {
    SaleResult(
        message = result.message!!,
        color = MaterialTheme.colorScheme.secondary,
        iconId = R.drawable.ic_error_circle
    )
}

@Composable
fun PedingSaleResult(result: RegisterSaleResult) {
    SaleResult(
        message = result.message!!,
        color = MaterialTheme.colorScheme.error,
        iconId = R.drawable.ic_failed
    )
}


@Preview
@Composable
fun SaleResultPreview() {
    SaleResult(
        message = "Venta exitosa, felicidades",
        color = MaterialTheme.colorScheme.primary,
        iconId = R.drawable.ic_check_circle
    )
}

@Composable
fun SaleResult(
    message: String,
    color: Color,
    iconId: Int
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}