package com.soleel.paymentapp.feature.salesprocess2

import android.annotation.SuppressLint
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
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.feature.salesprocess.setup.cashchangecalculator.CashChangeCalculatorScreen
import com.soleel.paymentapp.feature.salesprocess.setup.creditinstalmentsselection.CreditInstalmentsSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.setup.debitchangeselection.DebitChangeSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.setup.paymentypeselection.PaymentTypeSelectionScreen
import com.soleel.paymentapp.feature.salesprocess.setup.tipselection.TipSelectionScreen
import kotlinx.serialization.Serializable



@Serializable
data class SetupGraph(val calculatorTotal: Float)

fun NavGraphBuilder.setupNavigationGraph(
    backToPrevious: () -> Unit,
    navigateToPaymentGraph: (sale: Sale) -> Unit
) {
    composable<SetupGraph>(
        content = { backStackEntry ->
            val calculatorTotal: Float = backStackEntry.toRoute<SetupGraph>().calculatorTotal
            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["calculatorTotal"] = calculatorTotal
            SetupScreen(
                backToPrevious = backToPrevious,
                navigateToPaymentGraph = navigateToPaymentGraph
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

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    navHostController: NavHostController = rememberNavController(),
    setupViewModel: SetupViewModel = hiltViewModel(),
    backToPrevious: () -> Unit,
    navigateToPaymentGraph: (sale: Sale) -> Unit
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
                            TipSelectionScreen(
                                setupViewModel = setupViewModel,
                            )
                        }
                    )

                    composable<PaymentTypeSelection>(
                        content = {
                            PaymentTypeSelectionScreen(
                                setupViewModel = setupViewModel,
                                whenSelectingCash = {
                                    navHostController.navigate(CashChangeCalculator)
                                },
                                whenSelectingCredit = {
                                    navHostController.navigate(CreditInstallmentsSelection)
                                },
                                whenSelectingDebit = {
                                    navHostController.navigate(DebitChangeSelection)
                                }
                            )
                        }
                    )

                    composable<CashChangeCalculator>(
                        content = {
//                            backStackEntry ->
//                            val calculatorTotal: Float =
//                                setupViewModel.setupUiModel.calculatorTotal
//                            val tipTotal: Float? =
//                                setupViewModel.setupUiModel.tipTotal
//
//                            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
//                            savedStateHandle["calculatorTotal"] = calculatorTotal
//                            savedStateHandle["tipTotal"] = tipTotal

                            CashChangeCalculatorScreen(
                                setupViewModel = setupViewModel
                            )
                        }
                    )

                    composable<CreditInstallmentsSelection>(
                        content = {
                            CreditInstalmentsSelectionScreen(
                                setupViewModel = setupViewModel,
                                navigateToPaymentGraph = navigateToPaymentGraph
                            )
                        }
                    )

                    composable<DebitChangeSelection>(
                        content = {
                            DebitChangeSelectionScreen(
                                setupViewModel = setupViewModel,
                                navigateToPaymentGraph = navigateToPaymentGraph
                            )
                        }
                    )
                }
            )
        }
    )
}