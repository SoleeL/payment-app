package com.soleel.paymentapp.feature.salesprocess

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.soleel.paymentapp.core.component.SalesSummaryHeader
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.core.model.outcomeprocess.RegisterSaleResult
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import com.soleel.paymentapp.core.navigation.createNavType
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.salesprocess.outcome.FailedSaleScreen
import com.soleel.paymentapp.feature.salesprocess.outcome.PendingSaleScreen
import com.soleel.paymentapp.feature.salesprocess.outcome.registersale.RegisterSaleScreen
import com.soleel.paymentapp.feature.salesprocess.outcome.successfulsale.SuccessfulSaleScreen
import com.soleel.paymentapp.feature.salesprocess.payment.contactlessreading.ContactlessReadingScreen
import com.soleel.paymentapp.feature.salesprocess.payment.contactreading.ContactReadingScreen
import com.soleel.paymentapp.feature.salesprocess.payment.failedpayment.FailedPaymentScreen
import com.soleel.paymentapp.feature.salesprocess.payment.pinpad.PinpadScreen
import com.soleel.paymentapp.feature.salesprocess.payment.registerpayment.ProcessPaymentScreen
import com.soleel.paymentapp.feature.salesprocess.setup.cashchangecalculator.CashChangeCalculatorScreen
import com.soleel.paymentapp.feature.salesprocess.setup.creditinstalmentsselection.CreditInstalmentsSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.setup.debitchangeselection.DebitChangeSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.setup.paymentypeselection.PaymentTypeSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.utils.toSale
import kotlinx.serialization.Serializable
import kotlin.reflect.KType
import kotlin.reflect.typeOf


@LongDevicePreview
@Composable
private fun SalesProcessScreenLongPreview() {
    val fakeSavedStateHandle = SavedStateHandle(
        mapOf("totalAmount" to 7000f)
    )

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    SalesProcessScreen(
                        salesProcessViewModel = SalesProcessViewModel(
                            savedStateHandle = fakeSavedStateHandle
                        ),
                        saleToNavType = mapOf(typeOf<Sale>() to createNavType<Sale>()),
                        finalizeSale = {}
                    )
                }
            )
        }
    )
}

@Serializable
data class SalesProcessGraph(
    val totalAmount: Int,
    val paymentMethod: Int = -1,
    val cashChange: Int = -1,
    val creditInstalments: Int = -1,
    val debitChange: Int = -1
)

fun NavGraphBuilder.salesProcessGraph(
    saleToNavType: Map<KType, NavType<Sale>>,
    finalizeSale: () -> Unit
) {
    composable<SalesProcessGraph>(
        content = {
            SalesProcessScreen(
                saleToNavType = saleToNavType,
                finalizeSale = finalizeSale
            )
        }
    )
}

@Serializable
object TipSelection

@Serializable
object PaymentTypeSelection

@Serializable
object CashChangeCalculator

@Serializable
object CreditInstallmentsSelection

@Serializable
object DebitChangeSelection

@Serializable
object ContactlessReading

@Serializable
object ContactReading

@Serializable
object Pinpad

@Serializable
object ProcessPayment

@Serializable
object FailedPayment

@Serializable
object RegisterSale

@Serializable
object SuccessfulSale

@Serializable
object PendingSale

@Serializable
object FailedSale

@Serializable
object PrintVoucher // README: Puede ser un modal

@Serializable
object SendVoucherToEmail // README: Puede ser un modal

@Serializable
object QRDownloadVoucher // README: Puede ser un modal

fun getStartDestination(salesProcessUiModel: SalesProcessUiModel): Any {
    if (salesProcessUiModel.paymentMethodSelected == null) {
        return PaymentTypeSelection
    }

    when (salesProcessUiModel.paymentMethodSelected) {
        PaymentMethodEnum.CASH -> {
            if (salesProcessUiModel.cashChangeSelected == null) {
                return CashChangeCalculator
            }
            return RegisterSale
        }

        PaymentMethodEnum.CREDIT -> {
            if (salesProcessUiModel.creditInstalmentsSelected == null) {
                return CreditInstallmentsSelection
            }
            return ContactlessReading
        }

        PaymentMethodEnum.DEBIT -> {
            if (salesProcessUiModel.debitChangeSelected == null) {
                return DebitChangeSelection
            }
            return ContactlessReading
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesProcessScreen(
    navHostController: NavHostController = rememberNavController(),
    salesProcessViewModel: SalesProcessViewModel = hiltViewModel(),
    saleToNavType: Map<KType, NavType<Sale>>,
    finalizeSale: () -> Unit
) {
    val currentDestination: NavDestination? = navHostController.currentBackStackEntryAsState()
        .value?.destination
    val isAtStartDestination: Boolean =
        currentDestination?.hasRoute(PaymentTypeSelection::class) == true

    val salesProcessUiModel: SalesProcessUiModel = salesProcessViewModel.salesProcessUiModel

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // README: Si se borra el content no es detectado
                modifier = Modifier.background(Color.DarkGray),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isAtStartDestination) {
                                finalizeSale()
                            } else {
                                navHostController.popBackStack()
                            }
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    )
                },
                actions = {
                    Surface(
                        modifier = Modifier.padding(16.dp, 2.dp),
                        onClick = {
                            finalizeSale()
                        },
                        content = {
                            Text(
                                text = "Cancelar",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    )
                }
            )
        },
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                content = {

                    SalesSummaryHeader(
                        totalAmount = salesProcessUiModel.totalAmount,
//                        tipTotal = salesProcessUiModel.tipTotal,
                        paymentMethodSelected = salesProcessUiModel.paymentMethodSelected,
                        creditInstalmentsSelected = salesProcessUiModel.creditInstalmentsSelected,
                        debitChangeSelected = salesProcessUiModel.debitChangeSelected
                    )

                    val startDestination = remember(
                        calculation = { getStartDestination(salesProcessUiModel) }
                    )

                    NavHost(
                        navController = navHostController,
                        startDestination = startDestination,
                        modifier = Modifier.fillMaxSize(),
                        builder = {

                            // SETUP

//                            composable<TipSelection>(
//                                content = {
//                                    TipSelectionScreen(
//                                        navigateToPaymentTypeSelection = {
//                                            salesProcessViewModel.onSalesProcessUiEvent(
//                                                SalesProcessUiEvent.TipSelected(it)
//                                            )
//                                            val sale: Sale = salesProcessViewModel.salesProcessUiModel.toSale()
//                                            navHostController.navigate(PaymentTypeSelection(sale))
//                                        }
//                                    )
//                                }
//                            )

                            composable<PaymentTypeSelection>(
                                content = {
                                    PaymentTypeSelectionScreen(
                                        whenPaymentTypeSelected = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.PaymentMethodSelected(it)
                                            )
                                            when (it) {
                                                PaymentMethodEnum.CASH -> {
                                                    navHostController.navigate(
                                                        CashChangeCalculator
                                                    )
                                                }

                                                PaymentMethodEnum.CREDIT -> {
                                                    navHostController.navigate(
                                                        CreditInstallmentsSelection
                                                    )
                                                }

                                                PaymentMethodEnum.DEBIT -> {
                                                    navHostController.navigate(
                                                        DebitChangeSelection
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            )

                            composable<CashChangeCalculator>(
                                typeMap = saleToNavType, // TODO... SE TENE QUE PASAR DESDE EL NAV PRINCIPAL
                                content = {
                                    CashChangeCalculatorScreen(
                                        onBack = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.PaymentMethodSelected(null)
                                            )
                                            navHostController.popBackStack()
                                        },
                                        navigateToRegisterSale = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.CashChangeSelected(it)
                                            )
                                            salesProcessViewModel.salesProcessUiModel.toSale()
                                            navHostController.navigate(
                                                route = RegisterSale,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        }
                                    )
                                }
                            )

                            composable<CreditInstallmentsSelection>(
                                typeMap = saleToNavType,
                                content = {
                                    CreditInstalmentsSelectionScreen(
                                        onBack = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.PaymentMethodSelected(null)
                                            )
                                            navHostController.popBackStack()
                                        },
                                        navigateToPaymentProcess = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.CreditInstalmentsSelected(it)
                                            )
                                            salesProcessViewModel.salesProcessUiModel
                                                .toSale()
                                            navHostController.navigate(
                                                route = ContactlessReading,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        }
                                    )
                                }
                            )

                            composable<DebitChangeSelection>(
                                typeMap = saleToNavType,
                                content = {
                                    DebitChangeSelectionScreen(
                                        onBack = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.PaymentMethodSelected(null)
                                            )
                                            navHostController.popBackStack()
                                        },
                                        navigateToPaymentProcess = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.DebitChangeSelected(it)
                                            )
                                            salesProcessViewModel.salesProcessUiModel.toSale()
                                            navHostController.navigate(
                                                route = ContactlessReading,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        }
                                    )
                                }
                            )

                            // PAYMENT

                            composable<ContactlessReading>(
                                typeMap = saleToNavType,
                                content = {
                                    ContactlessReadingScreen(
                                        navigateToContactReading = {
                                            navHostController.navigate(
                                                route = ContactReading,
                                                builder = {
                                                    // README: Esto elimina todo el stack y queda el
                                                    //  nuevo destino en la pila
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        navigateToVerificationMethod = {
                                            navHostController.navigate(
                                                route = Pinpad,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        navigateToFailedPayment = {
                                            navHostController.navigate(
                                                route = FailedPayment,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                    )
                                }
                            )

                            composable<ContactReading>(
                                content = {
                                    ContactReadingScreen(
                                        navigateToVerificationMethod = {
                                            navHostController.navigate(
                                                route = Pinpad,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        navigateToFailedPayment = {
                                            navHostController.navigate(
                                                route = FailedPayment,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                    )
                                }
                            )

                            composable<Pinpad>(
                                content = {
                                    PinpadScreen(
                                        onCancel = finalizeSale,
                                        navigateToFailedPayment = {
                                            navHostController.navigate(
                                                route = FailedPayment,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        navigateToProcessPayment = {
                                            navHostController.navigate(
                                                route = ProcessPayment,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        }
                                    )
                                }
                            )

                            composable<ProcessPayment>(
                                content = {
                                    ProcessPaymentScreen(
                                        navigateToFailedPayment = {
                                            navHostController.navigate(
                                                route = FailedPayment,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        navigateToRegisterSale = { sale: Sale, paymentResult: PaymentResult ->
                                            navHostController.navigate(
                                                route = RegisterSale,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        }
                                    )
                                }
                            )

                            composable<FailedPayment>(
                                content = {
                                    FailedPaymentScreen(
                                        onRetryPaymentMethod = {
                                            navHostController.navigate(
                                                route = ContactlessReading,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        onSelectAnotherPaymentMethod = {
                                            // Descartar selecciones
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.PaymentMethodSelected(null)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.CreditInstalmentsSelected(null)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.DebitChangeSelected(null)
                                            )

                                            navHostController.navigate(
                                                route = PaymentTypeSelection,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        }
                                    )
                                }
                            )

                            // OUTCOME

                            composable<RegisterSale>(
                                typeMap = saleToNavType,
                                content = {
                                    RegisterSaleScreen(
                                        whenRegisterSaleIsSuccessful = { registerSaleResult: RegisterSaleResult ->
                                            navHostController.navigate(
                                                route = SuccessfulSale,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        whenRegisterSaleIsPending = { registerSaleResult: RegisterSaleResult ->
                                            navHostController.navigate(
                                                route = PendingSale,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        whenRegisterSaleIsFailed = { registerSaleResult: RegisterSaleResult ->
                                            navHostController.navigate(
                                                route = FailedSale,
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                    )
                                }
                            )

                            composable<SuccessfulSale>(
                                content = {
                                    SuccessfulSaleScreen(
                                        finalizeSale = finalizeSale
                                    )
                                }
                            )

                            composable<PendingSale>(
                                content = {
                                    PendingSaleScreen()
                                }
                            )

                            composable<FailedSale>(
                                content = {
                                    FailedSaleScreen()
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}