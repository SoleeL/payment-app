package com.soleel.paymentapp.feature.salesprocess.outcome.successfulsale

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.soleel.paymentapp.core.model.enums.SalesResultOptionEnum
import com.soleel.paymentapp.core.ui.R
import com.soleel.paymentapp.feature.salesprocess.outcome.component.SaleResult


@Composable
fun SuccessfulSaleScreen(
    finalizeSale: () -> Unit
) {
    BackHandler(
        enabled = true,
        onBack = {
            // TODO: si ya se abrio un modal... entonces se permite terminal
            if (true) {
                finalizeSale()
            } else {
                // TODO: abrir modal
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SaleResult(
                    message = "Venta exitosa, felicidades",
                    color = MaterialTheme.colorScheme.primary,
                    iconId = R.drawable.ic_check_circle
                )
            }

            val selectedOptions = listOf(
                SalesResultOptionEnum.PRINT,
                SalesResultOptionEnum.EMAIL,
                SalesResultOptionEnum.QR,
                SalesResultOptionEnum.FINISH_SALE
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(
                        items = selectedOptions,
                        itemContent = { option ->
                            val buttonColors = if (option.highlight) {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    when (option) {
                                        SalesResultOptionEnum.PRINT -> TODO()
                                        SalesResultOptionEnum.EMAIL -> TODO()
                                        SalesResultOptionEnum.QR -> TODO()
                                        SalesResultOptionEnum.FINISH_SALE -> {
                                            finalizeSale()
                                        }

                                        SalesResultOptionEnum.RETRY_STORE_SALE -> TODO()
                                        SalesResultOptionEnum.RETRY_REGISTER_SALE -> TODO()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = buttonColors,
                                shape = RoundedCornerShape(12.dp),
                                content = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            painter = painterResource(id = option.icon),
                                            contentDescription = option.displayName,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = option.displayName,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            if (option.displayDescription.isNotEmpty()) {
                                                Text(
                                                    text = option.displayDescription,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}