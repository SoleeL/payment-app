package com.soleel.paymentapp.feature.salesprocess.setup.paymentypeselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.core.model.enums.SalesResultOptionEnum
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar


@LongDevicePreview
@Composable
private fun PaymentTypeSelectionScreenLongPreview() {
    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    PaymentTypeSelectionScreen(
                        whenPaymentTypeSelected = { }
                    )
                }
            )
        }
    )
}

@Composable
fun PaymentTypeSelectionScreen(
    whenPaymentTypeSelected: (paymentMethodEnum: PaymentMethodEnum) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            Spacer(modifier = Modifier.weight(1f))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(
                        items = PaymentMethodEnum.entries,
                        itemContent = { option ->

                            Button(
                                onClick = { whenPaymentTypeSelected(option) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
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