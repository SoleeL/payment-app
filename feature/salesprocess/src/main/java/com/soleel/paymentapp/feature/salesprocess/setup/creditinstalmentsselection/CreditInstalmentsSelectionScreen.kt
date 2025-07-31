package com.soleel.paymentapp.feature.salesprocess.setup.creditinstalmentsselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar

@LongDevicePreview
@Composable
private fun CreditInstalmentsSelectionScreenLongPreview() {


    val creditInstalmentsSelectionViewModel: CreditInstalmentsSelectionViewModel =
        CreditInstalmentsSelectionViewModel()

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    CreditInstalmentsSelectionScreen(
                        creditInstalmentsSelectionViewModel = creditInstalmentsSelectionViewModel,
                        navigateToPaymentProcess = { }
                    )
                }
            )
        }
    )
}

@Composable
fun CreditInstalmentsSelectionScreen(
    creditInstalmentsSelectionViewModel: CreditInstalmentsSelectionViewModel = hiltViewModel(),
    navigateToPaymentProcess: (creditInstalmentsSelected: Int?) -> Unit
) {
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
                        items = creditInstalmentsSelectionViewModel.instalmentsUiEvent,
                        itemContent = { instalmentUiEvent ->

                            val label: String = when (instalmentUiEvent) {
                                is InstalmentUiEvent.WhenIsNumber -> "${instalmentUiEvent.value} cuotas"
                                is InstalmentUiEvent.WhenIsOther -> instalmentUiEvent.value
                                is InstalmentUiEvent.WhenIsWithout -> "Sin cuotas"
                            }

                            val isWithout = instalmentUiEvent is InstalmentUiEvent.WhenIsWithout

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
                                    when (instalmentUiEvent) {
                                        is InstalmentUiEvent.WhenIsNumber -> {
                                            navigateToPaymentProcess(instalmentUiEvent.value)
                                        }
                                        is InstalmentUiEvent.WhenIsOther -> TODO()
                                        InstalmentUiEvent.WhenIsWithout -> {
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