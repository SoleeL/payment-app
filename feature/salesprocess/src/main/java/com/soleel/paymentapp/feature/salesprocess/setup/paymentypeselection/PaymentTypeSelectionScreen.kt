package com.soleel.paymentapp.feature.salesprocess.setup.paymentypeselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
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
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            Spacer(modifier = Modifier.weight(1f))

            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    items(PaymentMethodEnum.entries) { paymentMethod ->
                        PaymentMethodButton(
                            paymentMethodEnum = paymentMethod,
                            onClick = whenPaymentTypeSelected
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun PaymentMethodButton(
    paymentMethodEnum: PaymentMethodEnum,
    onClick: (paymentMethodEnum: PaymentMethodEnum) -> Unit
) {
    Button(
        onClick = { onClick(paymentMethodEnum) } ,
        shape = RoundedCornerShape(25),
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Icon(
                        painter = painterResource(id = paymentMethodEnum.icon),
                        contentDescription = paymentMethodEnum.value,
                        modifier = Modifier.size(64.dp)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Text(
                                text = paymentMethodEnum.value,
                                style = MaterialTheme.typography.headlineLarge,
                            )
                        }
                    )
                }
            )
        }
    )
}
