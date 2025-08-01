package com.soleel.paymentapp.feature.salesprocess.outcome.failedsale

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
fun FailedSaleScreen(
    errorCode: String?,
    errorMessage: String?,
    navigateToRegisterSale: () -> Unit
) {

    // TODO: Mientras... se decide bloquear el salir del usuario
    BackHandler(enabled = true, onBack = {})

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
                    code = errorCode!!,
                    message = errorMessage!!,
                    color = MaterialTheme.colorScheme.secondary,
                    iconId = R.drawable.ic_error_circle
                )
            }

            Button(
                onClick = navigateToRegisterSale,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = SalesResultOptionEnum.RETRY_STORE_SALE.icon),
                            contentDescription = SalesResultOptionEnum.RETRY_STORE_SALE.displayName,
                            modifier = Modifier.size(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = SalesResultOptionEnum.RETRY_STORE_SALE.displayName,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            )
        }
    )
}