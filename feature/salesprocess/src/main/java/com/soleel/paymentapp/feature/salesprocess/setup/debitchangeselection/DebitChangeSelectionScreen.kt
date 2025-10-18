package com.soleel.paymentapp.feature.salesprocess.setup.debitchangeselection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.soleel.paymentapp.core.component.ListSelection
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.core.ui.visualtransformation.CLPCurrencyVisualTransformation


@Composable
fun DebitChangeSelectionScreen(
    debitChangeSelectionViewModel: DebitChangeSelectionViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToPaymentProcess: (Int?) -> Unit
) {
    BackHandler(enabled = true, onBack = onBack)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {

            Spacer(modifier = Modifier.weight(1f))

            Box(
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                content = {
                    ListSelection(
                        listSelectionItems = debitChangeSelectionViewModel.debitChangeUiState,
                        visualTransformation = CLPCurrencyVisualTransformation()
                    )
                }
            )
        }
    )
}

@LongDevicePreview
@Composable
private fun DebitChangeSelectionScreenLongPreview() {

    val debitChangeSelectionViewModel: DebitChangeSelectionViewModel =
        DebitChangeSelectionViewModel()

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    DebitChangeSelectionScreen(
                        debitChangeSelectionViewModel = debitChangeSelectionViewModel,
                        navigateToPaymentProcess = { },
                        onBack = { }
                    )
                }
            )
        }
    )
}