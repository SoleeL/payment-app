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
        mapOf("calculatorTotal" to 7000f)
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
data class SalesProcessGraph(val calculatorTotal: Float)

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
data class TipSelection(val sale: Sale)

@Serializable
object PaymentTypeSelection

@Serializable
data class CashChangeCalculator(val sale: Sale)

@Serializable
data class CreditInstallmentsSelection(val sale: Sale)

@Serializable
data class DebitChangeSelection(val sale: Sale)

@Serializable
data class ContactlessReading(val sale: Sale)

@Serializable
object ContactReading

@Serializable
object Pinpad

@Serializable
object ProcessPayment

@Serializable
object FailedPayment

@Serializable
data class RegisterSale(val sale: Sale)

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
                        calculatorTotal = salesProcessUiModel.calculatorTotal,
                        tipTotal = salesProcessUiModel.tipTotal,
                        paymentMethodSelected = salesProcessUiModel.paymentMethodSelected,
                        creditInstalmentsSelected = salesProcessUiModel.creditInstalmentsSelected,
                        debitChangeSelected = salesProcessUiModel.debitChangeSelected
                    )

                    NavHost(
                        navController = navHostController,
                        startDestination = PaymentTypeSelection,
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
                                            val sale: Sale =
                                                salesProcessViewModel.salesProcessUiModel.toSale()
                                            when (it) {
                                                PaymentMethodEnum.CASH -> {
                                                    navHostController.navigate(
                                                        CashChangeCalculator(sale = sale)
                                                    )
                                                }

                                                PaymentMethodEnum.CREDIT -> {
                                                    navHostController.navigate(
                                                        CreditInstallmentsSelection(sale = sale)
                                                    )
                                                }

                                                PaymentMethodEnum.DEBIT -> {
                                                    navHostController.navigate(
                                                        DebitChangeSelection(sale = sale)
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
                                        navigateToRegisterSale = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.CashChangeSelected(it)
                                            )
                                            val sale: Sale =
                                                salesProcessViewModel.salesProcessUiModel.toSale()
                                            navHostController.navigate(
                                                route = RegisterSale(sale),
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
                                        navigateToPaymentProcess = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.CreditInstalmentsSelected(it)
                                            )
                                            val sale: Sale =
                                                salesProcessViewModel.salesProcessUiModel
                                                    .toSale()
                                            navHostController.navigate(
                                                route = ContactlessReading(sale),
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
                                        navigateToPaymentProcess = {
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.DebitChangeSelected(it)
                                            )
                                            val sale: Sale =
                                                salesProcessViewModel.salesProcessUiModel.toSale()
                                            navHostController.navigate(
                                                route = ContactlessReading(sale),
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
                                                route = RegisterSale(sale),
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
                                                route = ContactlessReading(it),
                                                builder = {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    }
                                                    launchSingleTop = true
                                                }
                                            )
                                        },
                                        onSelectAnotherPaymentMethod = {
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