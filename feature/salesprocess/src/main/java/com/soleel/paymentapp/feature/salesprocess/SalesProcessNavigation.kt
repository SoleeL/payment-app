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
import com.soleel.paymentapp.feature.salesprocess.screens.DebitChangeCalculatorScreen
import com.soleel.paymentapp.feature.salesprocess.screens.FailedSaleScreen
import com.soleel.paymentapp.feature.salesprocess.screens.PaymentTypeSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.screens.PendingSaleScreen
import com.soleel.paymentapp.feature.salesprocess.screens.RegisterSaleScreen
import com.soleel.paymentapp.feature.salesprocess.screens.SuccessfulSaleScreen
import kotlinx.serialization.Serializable


@Serializable
data class SalesProcessGraph(val amount: Int)

fun NavGraphBuilder.salesProcessNavigationGraph(
    backToPrevious: () -> Unit
) {
    composable<SalesProcessGraph>(
        content = { backStackEntry ->
            val amount: Int = backStackEntry.toRoute<SalesProcessGraph>().amount
            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["amount"] = amount
            SalesProcessScreen(
                backToPrevious = backToPrevious
            )
        }
    )
}

@Serializable
object PaymentTypeSelection

@Serializable
object CashChangeCalculator

@Serializable
object CreditInstallmentsSelection

@Serializable
object DebitChangeCalculator

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
                    composable<PaymentTypeSelection>(
                        content = {
                            PaymentTypeSelectionScreen(
                                whenSelectingCash = { navHostController.navigate(CashChangeCalculator) },
                                whenSelectingDebit = { navHostController.navigate(CreditInstallmentsSelection) },
                                whenSelectingCredit = { navHostController.navigate(DebitChangeCalculator) }
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

                    composable<DebitChangeCalculator>(
                        content = {
                            DebitChangeCalculatorScreen()
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