package com.soleel.paymentapp.feature.salesprocess.setup

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.soleel.paymentapp.core.model.Sale
import kotlinx.serialization.Serializable



@Serializable
data class SetupGraph(val totalAmount: Float)

fun NavGraphBuilder.setupNavigationGraph(
    backToPrevious: () -> Unit,
    navigateToPaymentGraph: (sale: Sale) -> Unit
) {
    composable<SetupGraph>(
        content = { backStackEntry ->
            val totalAmount: Float = backStackEntry.toRoute<SetupGraph>().totalAmount
            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["totalAmount"] = totalAmount
//            SetupScreen(
//                backToPrevious = backToPrevious,
//                navigateToPaymentGraph = navigateToPaymentGraph
//            )
        }
    )
}

//@Serializable
//object TipSelection
//
//@Serializable
//object PaymentTypeSelection
//
//@Serializable
//object CashChangeCalculator
//
//@Serializable
//object CreditInstallmentsSelection
//
//@Serializable
//object DebitChangeSelection
//
//@SuppressLint("RestrictedApi")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SetupScreen(
//    navHostController: NavHostController = rememberNavController(),
//    setupViewModel: SetupViewModel = hiltViewModel(),
//    backToPrevious: () -> Unit,
//    navigateToPaymentGraph: (sale: Sale) -> Unit
//) {
//    val currentDestination: NavDestination? = navHostController.currentBackStackEntryAsState()
//        .value?.destination
//    val isAtStartDestination: Boolean =
//        currentDestination?.hasRoute(PaymentTypeSelection::class) == true
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { }, // README: Si se borra el content no es detectado
//                modifier = Modifier.background(Color.DarkGray),
//                navigationIcon = {
//                    IconButton(
//                        onClick = {
//                            if (isAtStartDestination) {
//                                backToPrevious()
//                            } else {
//                                navHostController.popBackStack()
//                            }
//                        },
//                        content = {
//                            Icon(
//                                imageVector = Icons.Filled.ArrowBack,
//                                contentDescription = "back"
//                            )
//                        }
//                    )
//                },
//                actions = {
//                    Surface(
//                        modifier = Modifier.padding(16.dp, 2.dp),
//                        onClick = {
//                            backToPrevious()
//                        },
//                        content = {
//                            Text(
//                                text = "Cancelar",
//                                style = MaterialTheme.typography.titleMedium,
//                            )
//                        }
//                    )
//                }
//            )
//        },
//        content = { paddingValues ->
//            NavHost(
//                navController = navHostController,
//                startDestination = PaymentTypeSelection,
//                modifier = Modifier.padding(paddingValues),
//                builder = {
//
//                    composable<TipSelection>(
//                        content = {
//                            TipSelectionScreen(
//                                setupViewModel = setupViewModel,
//                            )
//                        }
//                    )
//
//                    composable<PaymentTypeSelection>(
//                        content = {
//                            PaymentTypeSelectionScreen(
//                                setupViewModel = setupViewModel,
//                                whenSelectingCash = {
//                                    navHostController.navigate(CashChangeCalculator)
//                                },
//                                whenSelectingCredit = {
//                                    navHostController.navigate(CreditInstallmentsSelection)
//                                },
//                                whenSelectingDebit = {
//                                    navHostController.navigate(DebitChangeSelection)
//                                }
//                            )
//                        }
//                    )
//
//                    composable<CashChangeCalculator>(
//                        content = {
////                            backStackEntry ->
////                            val totalAmount: Float =
////                                setupViewModel.setupUiModel.totalAmount
////                            val tipTotal: Float? =
////                                setupViewModel.setupUiModel.tipTotal
////
////                            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
////                            savedStateHandle["totalAmount"] = totalAmount
////                            savedStateHandle["tipTotal"] = tipTotal
//
//                            CashChangeCalculatorScreen(
//                                setupViewModel = setupViewModel
//                            )
//                        }
//                    )
//
//                    composable<CreditInstallmentsSelection>(
//                        content = {
//                            CreditInstalmentsSelectionScreen(
//                                setupViewModel = setupViewModel,
//                                navigateToPaymentGraph = navigateToPaymentGraph
//                            )
//                        }
//                    )
//
//                    composable<DebitChangeSelection>(
//                        content = {
//                            DebitChangeSelectionScreen(
//                                setupViewModel = setupViewModel,
//                                navigateToPaymentGraph = navigateToPaymentGraph
//                            )
//                        }
//                    )
//                }
//            )
//        }
//    )
//}