package com.soleel.paymentapp.feature.salesprocess

import androidx.compose.foundation.background
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.soleel.paymentapp.feature.salesprocess.screens.CashChangeCalculatorScreen
import com.soleel.paymentapp.feature.salesprocess.screens.CreditInstallmentsSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.screens.DebitChangeSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.screens.FailedSaleScreen
import com.soleel.paymentapp.feature.salesprocess.screens.PaymentTypeSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.screens.PendingSaleScreen
import com.soleel.paymentapp.feature.salesprocess.screens.RegisterSaleScreen
import com.soleel.paymentapp.feature.salesprocess.screens.SuccessfulSaleScreen
import com.soleel.paymentapp.feature.salesprocess.screens.TipSelectionScreen
import kotlinx.serialization.Serializable


@Serializable
data class SalesProcessGraph(val calculatorTotal: Float)

fun NavGraphBuilder.salesProcessNavigationGraph(
    backToPrevious: () -> Unit
) {
    composable<SalesProcessGraph>(
        content = { backStackEntry ->
            val calculatorTotal: Float = backStackEntry.toRoute<SalesProcessGraph>().calculatorTotal
            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["amount"] = calculatorTotal
            SalesProcessScreen(
                backToPrevious = backToPrevious
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
object RegisterSale

@Serializable
object PendingSale

@Serializable
object SuccessfulSale

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
    backToPrevious: () -> Unit
) {
    val currentDestination: NavDestination? = navHostController.currentBackStackEntryAsState()
        .value?.destination
    val isAtStartDestination: Boolean =
        currentDestination?.hasRoute(PaymentTypeSelection::class) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // README: Si se borra el content no es detectado
                modifier = Modifier.background(Color.DarkGray),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isAtStartDestination) {
                                backToPrevious()
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
                            backToPrevious()
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
            NavHost(
                navController = navHostController,
                startDestination = PaymentTypeSelection,
                modifier = Modifier.padding(paddingValues),
                builder = {

                    composable<TipSelection>(
                        content = {
                            TipSelectionScreen()
                        }
                    )

                    composable<PaymentTypeSelection>(
                        content = {
                            PaymentTypeSelectionScreen(
                                salesProcessViewModel = salesProcessViewModel,
                                whenSelectingCash = {
                                    navHostController.navigate(
                                        CashChangeCalculator
                                    )
                                },
                                whenSelectingDebit = {
                                    navHostController.navigate(
                                        CreditInstallmentsSelection
                                    )
                                },
                                whenSelectingCredit = {
                                    navHostController.navigate(
                                        DebitChangeSelection
                                    )
                                }
                            )
                        }
                    )

                    composable<CashChangeCalculator>(
                        content = {
                            CashChangeCalculatorScreen()
                        }
                    )

                    composable<CreditInstallmentsSelection>(
                        content = {
                            CreditInstallmentsSelectionScreen()
                        }
                    )

                    composable<DebitChangeSelection>(
                        content = {
                            DebitChangeSelectionScreen()
                        }
                    )

                    composable<RegisterSale>(
                        content = {
                            RegisterSaleScreen()
                        }
                    )

                    composable<PendingSale>(
                        content = {
                            PendingSaleScreen()
                        }
                    )

                    composable<SuccessfulSale>(
                        content = {
                            SuccessfulSaleScreen()
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