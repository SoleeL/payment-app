package com.soleel.paymentapp.feature.salesprocess.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.SavedStateHandle
import com.soleel.paymentapp.core.model.PaymentMethodEnum
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.salesprocess.SalesProcessUiEvent
import com.soleel.paymentapp.feature.salesprocess.SalesProcessViewModel
import com.soleel.paymentapp.feature.salesprocess.components.SalesProcessSummaryHeader


@LongDevicePreview
@Composable
private fun CalculatorScreenLongPreview() {
    val fakeSavedStateHandle = SavedStateHandle(
        mapOf("calculatorTotal" to 7000f)
    )

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    PaymentTypeSelectionScreen(
                        salesProcessViewModel = SalesProcessViewModel(
                            savedStateHandle = fakeSavedStateHandle
                        ),
                        whenSelectingCash = {},
                        whenSelectingDebit = {},
                        whenSelectingCredit = {}
                    )
                }
            )
        }
    )
}

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

            Column(
                modifier = Modifier.padding(16.dp),
                content = {
                    PaymentMethodButton(
                        paymentMethodEnum = PaymentMethodEnum.CASH,
                        onClick = { paymentMethodEnum ->
                            salesProcessViewModel.onSalesProcessUiEvent(
                                SalesProcessUiEvent.PaymentMethodSelected(
                                    paymentMethodEnum = paymentMethodEnum,
                                    navigationToNextScreen = whenSelectingCash
                                )
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PaymentMethodButton(
                        paymentMethodEnum = PaymentMethodEnum.CREDIT,
                        onClick = { paymentMethodEnum ->
                            salesProcessViewModel.onSalesProcessUiEvent(
                                SalesProcessUiEvent.PaymentMethodSelected(
                                    paymentMethodEnum = paymentMethodEnum,
                                    navigationToNextScreen = whenSelectingCredit
                                )
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PaymentMethodButton(
                        paymentMethodEnum = PaymentMethodEnum.DEBIT,
                        onClick = { paymentMethodEnum ->
                            salesProcessViewModel.onSalesProcessUiEvent(
                                SalesProcessUiEvent.PaymentMethodSelected(
                                    paymentMethodEnum = paymentMethodEnum,
                                    navigationToNextScreen = whenSelectingDebit
                                )
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun PaymentMethodButton(
    paymentMethodEnum: PaymentMethodEnum,
    onClick : (paymentMethodEnum: PaymentMethodEnum) -> Unit
) {
    Button(
        onClick = { onClick(paymentMethodEnum) },
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
