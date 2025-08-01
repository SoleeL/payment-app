package com.soleel.paymentapp.feature.salesprocess.setup.debitchangeselection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.core.ui.visualtransformation.CLPCurrencyVisualTransformation
import com.soleel.paymentapp.feature.salesprocess.setup.creditinstalmentsselection.CreditInstalmentsSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.setup.creditinstalmentsselection.CreditInstalmentsSelectionViewModel
import com.soleel.paymentapp.feature.salesprocess.setup.creditinstalmentsselection.InstalmentUiEvent

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

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(
                        items = debitChangeSelectionViewModel.debitChangeUiEvent,
                        itemContent = { changeUiEvent ->
                            val label: String = when (changeUiEvent) {
                                is ChangeUiEvent.WhenIsNumber -> {
                                    CLPCurrencyVisualTransformation()
                                        .filter(AnnotatedString(text = changeUiEvent.value.toInt().toString()))
                                        .text.toString()
                                }
                                is ChangeUiEvent.WhenIsWithout -> "Sin vuelto"
                            }

                            val isWithout = changeUiEvent is ChangeUiEvent.WhenIsWithout

                            val buttonColors = if (isWithout) {
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
                                    when (changeUiEvent) {
                                        is ChangeUiEvent.WhenIsNumber -> {
                                            navigateToPaymentProcess(changeUiEvent.value)
                                        }
                                        ChangeUiEvent.WhenIsWithout -> {
                                            navigateToPaymentProcess(null)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = buttonColors,
                                shape = RoundedCornerShape(12.dp),
                                content = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}