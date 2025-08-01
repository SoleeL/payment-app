package com.soleel.paymentapp.feature.salesprocess

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.soleel.paymentapp.core.model.intentsale.IntentSaleStatusEnum
import com.soleel.paymentapp.core.navigation.createNavType
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.salesprocess.outcome.failedsale.FailedSaleScreen
import com.soleel.paymentapp.feature.salesprocess.outcome.pendingsale.PendingSaleScreen
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
                        finalizeSale = {},
                        finishWithResult = null
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
    finalizeSale: () -> Unit,
    finishWithResult: ((saleId: String?, status: IntentSaleStatusEnum, message: String?, errorCode: String?) -> Unit)?
) {
    composable<SalesProcessGraph>(
        content = {
            SalesProcessScreen(
                saleToNavType = saleToNavType,
                finalizeSale = finalizeSale,
                finishWithResult = finishWithResult
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
object FailedSale

@Serializable
object PendingSale


@Serializable
object SuccessfulSale

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
    finalizeSale: () -> Unit,
    finishWithResult: ((saleId: String?, status: IntentSaleStatusEnum, message: String?, errorCode: String?) -> Unit)?
) {
    val salesProcessUiModel: SalesProcessUiModel = salesProcessViewModel.salesProcessUiModel

    val startDestination = remember(calculation = { getStartDestination(salesProcessUiModel) })
    val currentDestination: NavDestination? = navHostController.currentBackStackEntryAsState()
        .value?.destination

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // README: Si se borra el content no es detectado
                modifier = Modifier.background(Color.DarkGray),
                actions = {
                    if (currentDestination?.hasRoute(ProcessPayment::class) == true ||
                        currentDestination?.hasRoute(RegisterSale::class) == true
                    ) {
                        // No mostrar acción
                    } else if (currentDestination?.hasRoute(SuccessfulSale::class) == true ||
                        currentDestination?.hasRoute(PendingSale::class) == true
                    ) {
                        Surface(
                            modifier = Modifier.padding(16.dp, 2.dp),
                            onClick = {
                                val message: String =
                                    if (currentDestination == PendingSale::class) {
                                        "Pago finalizado correntacmente, pero Venta pendiende te registro"
                                    } else {
                                        "Venta finalizada correctamente"
                                    }
                                finishWithResult?.invoke(
                                    salesProcessUiModel.uuidSale,
                                    IntentSaleStatusEnum.SUCCESS,
                                    message,
                                    null
                                )
                                finalizeSale()
                            },
                            content = {
                                Text(
                                    text = "Cerrar",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        )
                    } else {
                        Surface(
                            modifier = Modifier.padding(16.dp, 2.dp),
                            onClick = {
                                val status: IntentSaleStatusEnum =
                                    if (salesProcessUiModel.errorCode != null) {
                                        IntentSaleStatusEnum.ERROR
                                    } else {
                                        IntentSaleStatusEnum.CANCELLED
                                    }
                                finishWithResult?.invoke(
                                    salesProcessUiModel.uuidSale,
                                    status,
                                    salesProcessUiModel.errorMessage,
                                    salesProcessUiModel.errorCode
                                )
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
//                                            navHostController.navigate(PaymentTypeSelection)
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
                                        navigateToVerificationMethod = { brand: String, last4: Int ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.CardBrandDetected(brand)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.Last4Obtained(last4)
                                            )
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
                                        navigateToFailedPayment = { errorCode: String, errorMessage: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorCode(errorCode)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorMessage(errorMessage)
                                            )

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
                                        navigateToVerificationMethod = { brand: String, last4: Int ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.CardBrandDetected(brand)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.Last4Obtained(last4)
                                            )

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
                                        navigateToFailedPayment = { errorCode: String, errorMessage: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorCode(errorCode)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorMessage(errorMessage)
                                            )

                                            navHostController.navigate(
                                                route = FailedPayment,
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

                            composable<Pinpad>(
                                content = {
                                    PinpadScreen(
                                        onCancel = finalizeSale,
                                        navigateToFailedPayment = { errorCode: String, errorMessage: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorCode(errorCode)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorMessage(errorMessage)
                                            )

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
                                        navigateToProcessPayment = { pinBlock: String, ksn: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.SavePinBlock(pinBlock)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.SaveKSN(ksn)
                                            )

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
                                        navigateToFailedPayment = { errorCode: String, errorMessage: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorCode(errorCode)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorMessage(errorMessage)
                                            )

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
                                        navigateToRegisterSale = { sequenceNumber: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.SaveSequenceNumber(
                                                    sequenceNumber
                                                )
                                            )

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
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.CardBrandDetected(null)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.Last4Obtained(null)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorCode(null)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorMessage(null)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.SavePinBlock(null)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.SaveKSN(null)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.SaveSequenceNumber(null)
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
                                        navigateToFailedSale = { errorCode: String, errorMessage: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorCode(errorCode)
                                            )
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.ReadingErrorMessage(errorMessage)
                                            )

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

                                        navigateToPendingSale = { uuidSale: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.UUIDSale(uuidSale)
                                            )

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

                                        navigateToSuccessfulSale = { uuidSale: String ->
                                            salesProcessViewModel.onSalesProcessUiEvent(
                                                SalesProcessUiEvent.UUIDSale(uuidSale)
                                            )

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
                                    )
                                }
                            )

                            composable<FailedSale>(
                                content = {
                                    FailedSaleScreen(
                                        errorCode = salesProcessViewModel.salesProcessUiModel.errorCode,
                                        errorMessage = salesProcessViewModel.salesProcessUiModel.errorMessage,
                                        navigateToRegisterSale = {
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

                            composable<PendingSale>(
                                content = {
                                    PendingSaleScreen(
                                        finalizeSale = {
                                            finishWithResult?.invoke(
                                                salesProcessUiModel.uuidSale,
                                                IntentSaleStatusEnum.SUCCESS,
                                                "Pago exitoso, pero la Venta esta pendiente de registro",
                                                salesProcessUiModel.errorCode
                                            )
                                            finalizeSale()
                                        },
                                        navigateToRegisterSale = {
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

                            composable<SuccessfulSale>(
                                content = {
                                    SuccessfulSaleScreen(
                                        finalizeSale = {
                                            finishWithResult?.invoke(
                                                salesProcessUiModel.uuidSale,
                                                IntentSaleStatusEnum.SUCCESS,
                                                salesProcessUiModel.errorMessage,
                                                salesProcessUiModel.errorCode
                                            )
                                            finalizeSale()
                                        }
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