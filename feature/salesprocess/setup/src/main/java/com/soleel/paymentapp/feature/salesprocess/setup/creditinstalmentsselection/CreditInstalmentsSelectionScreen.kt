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
import androidx.lifecycle.SavedStateHandle
import com.soleel.paymentapp.core.component.SalesSummaryHeader
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.salesprocess.setup.SetupUiEvent
import com.soleel.paymentapp.feature.salesprocess.setup.SetupViewModel

@LongDevicePreview
@Composable
private fun CreditInstalmentsSelectionScreenLongPreview() {
    val fakeSavedStateHandle = SavedStateHandle(mapOf("calculatorTotal" to 7000f))

    val setupViewModel: SetupViewModel = SetupViewModel(fakeSavedStateHandle)

    setupViewModel.onSetupUiEvent(SetupUiEvent.TipSelected(tipTotal = 700f))

    setupViewModel.onSetupUiEvent(
        SetupUiEvent.PaymentMethodSelected(
            paymentMethodSelected = PaymentMethodEnum.CASH,
            navigationToNextScreen = {}
        )
    )

    val creditInstalmentsSelectionViewModel: CreditInstalmentsSelectionViewModel =
        CreditInstalmentsSelectionViewModel()

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    CreditInstalmentsSelectionScreen(
                        setupViewModel = setupViewModel,
                        creditInstalmentsSelectionViewModel = creditInstalmentsSelectionViewModel,
                        navigateToPaymentGraph = { }
                    )
                }
            )
        }
    )
}

@Composable
fun CreditInstalmentsSelectionScreen(
    setupViewModel: SetupViewModel,
    creditInstalmentsSelectionViewModel: CreditInstalmentsSelectionViewModel = hiltViewModel(),
    navigateToPaymentGraph: (sale: Sale) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        content = {
            SalesSummaryHeader(
                calculatorTotal = setupViewModel.setupUiModel.calculatorTotal,
                paymentMethodSelected = setupViewModel.setupUiModel.paymentMethodSelected
            )

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
                                // Colores más resaltados
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                // Colores claros
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    when (instalmentUiEvent) {
                                        is InstalmentUiEvent.WhenIsNumber -> {
                                            creditInstalmentsSelectionViewModel.onInstalmentUiEvent(
                                                event = instalmentUiEvent,
                                                setupEvent = {
                                                    setupViewModel.onSetupUiEvent(
                                                        SetupUiEvent.CreditInstalmentsSelected(
                                                            creditInstalmentsSelected = instalmentUiEvent.value,
                                                            navigationToNextScreen = navigateToPaymentGraph
                                                        )
                                                    )
                                                }
                                            )
                                        }

                                        is InstalmentUiEvent.WhenIsOther -> {
                                            creditInstalmentsSelectionViewModel.onInstalmentUiEvent(
                                                event = instalmentUiEvent,
                                                setupEvent = {}
                                            )
                                        }

                                        is InstalmentUiEvent.WhenIsWithout -> {
                                            creditInstalmentsSelectionViewModel.onInstalmentUiEvent(
                                                event = instalmentUiEvent,
                                                setupEvent = {
                                                    setupViewModel.onSetupUiEvent(
                                                        SetupUiEvent.CreditInstalmentsSelected(
                                                            creditInstalmentsSelected = 0,
                                                            navigationToNextScreen = navigateToPaymentGraph
                                                        )
                                                    )
                                                }
                                            )
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