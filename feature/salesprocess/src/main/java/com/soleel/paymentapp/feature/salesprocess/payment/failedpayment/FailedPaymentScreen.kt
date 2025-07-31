package com.soleel.paymentapp.feature.salesprocess.payment.failedpayment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import com.soleel.paymentapp.core.ui.R
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar

@LongDevicePreview
@Composable
private fun FailedPaymentScreenLongPreview() {
    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    FailedPaymentScreen(
                        onRetryPaymentMethod = {},
                        onSelectAnotherPaymentMethod = {}
                    )
                }
            )
        }
    )
}

@Composable
fun FailedPaymentScreen(
    failedPaymentViewModel: FailedPaymentViewModel = hiltViewModel(),
    onRetryPaymentMethod: (sale: Sale) -> Unit,
    onSelectAnotherPaymentMethod: () -> Unit
) {
    val sale: Sale = failedPaymentViewModel.sale
    val failedPaymentResult: PaymentResult = failedPaymentViewModel.paymentResult

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            FailedPaymentScreenContent(
                failedPaymentResult = failedPaymentResult,
                onRetryPaymentMethod = { onRetryPaymentMethod(failedPaymentViewModel.sale) },
                onSelectAnotherPaymentMethod = onSelectAnotherPaymentMethod
            )
        }
    )
}

@Composable
fun FailedPaymentScreenContent(
    failedPaymentResult: PaymentResult,
    onRetryPaymentMethod: () -> Unit,
    onSelectAnotherPaymentMethod: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_error_circle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No se pudo completar el pago",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )

            failedPaymentResult.message?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            failedPaymentResult.paymentId?.let { id ->
                Text(
                    text = "ID de pago: $id",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        Column(
            content = {
                Button(
                    onClick = onRetryPaymentMethod,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    content = {
                        Text(
                            text = "Reintentar",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onSelectAnotherPaymentMethod() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    content = {
                        Text(
                            text = "Seleccionar otro metodo",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                )
            }
        )
    }
}