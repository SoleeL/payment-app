package com.soleel.paymentapp.feature.salesprocess.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.feature.salesprocess.payment.screens.ContactReadingScreen
import com.soleel.paymentapp.feature.salesprocess.payment.screens.ContactlessReadingScreen
import com.soleel.paymentapp.feature.salesprocess.payment.screens.PinpadScreen
import com.soleel.paymentapp.feature.salesprocess.payment.screens.ProcessPaymentScreen
import kotlinx.serialization.Serializable
import kotlin.reflect.KType

@Serializable
data class PaymentGraph(val sale: Sale)

fun NavGraphBuilder.paymentNavigationGraph(
    saleToNavType: Map<KType, NavType<Sale>>,
    backToPrevious: () -> Unit,
    navigateToOutcomeGraph: () -> Unit
) {
    composable<PaymentGraph>(
        typeMap = saleToNavType,
        content = { backStackEntry ->
            val sale: Sale = backStackEntry.toRoute<PaymentGraph>().sale
            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["sale"] = sale
            PaymentScreen(
                backToPrevious = backToPrevious,
                navigateToOutcomeGraph = navigateToOutcomeGraph
            )
        }
    )
}

@Serializable
object ContactlessReading

@Serializable
object ContactReading

@Serializable
object Pinpad

@Serializable
object ProcessPayment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navHostController: NavHostController = rememberNavController(),
    paymentViewModel: PaymentViewModel = hiltViewModel(),
    backToPrevious: () -> Unit, // TODO Cambiar por un cancel
    navigateToOutcomeGraph: () -> Unit
) {
    val currentDestination: NavDestination? = navHostController.currentBackStackEntryAsState()
        .value?.destination
    val isAtStartDestination: Boolean =
        currentDestination?.hasRoute(ContactlessReading::class) == true

    val contactlessReadingUiState: ReadingUiState by paymentViewModel.contactlessReadingUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // README: Si se borra el content no es detectado
                modifier = Modifier.background(Color.DarkGray),
                actions = {
                    when (contactlessReadingUiState) {
                        is ReadingUiState.Reading -> {
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
                        is ReadingUiState.Success -> {}
                        is ReadingUiState.Failure -> {
                            // README: Es importante llamar a paymentViewModel.contactReadingUiState
                            // aqui, ya que si se invoca antes, el flow se invocara y el timer no
                            // funcionara bien
                            val contactReadingUiState: ReadingUiState by paymentViewModel.contactReadingUiState.collectAsStateWithLifecycle()

                            when (contactReadingUiState) {
                                is ReadingUiState.Reading -> Surface(
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
                                is ReadingUiState.Success,
                                is ReadingUiState.Failure -> {}
                            }
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            NavHost(
                navController = navHostController,
                startDestination = ContactlessReading,
                modifier = Modifier.padding(paddingValues),
                builder = {
                    composable<ContactlessReading>(
                        content = {
                            ContactlessReadingScreen(
                                paymentViewModel = paymentViewModel,
                                navigateToContactReading = { navHostController.navigate(ContactReading)},
                                navigateToVerificationMethod = { navHostController.navigate(Pinpad) },
                                navigateToOutcomeGraph = navigateToOutcomeGraph
                            )
                        }
                    )

                    composable<ContactReading>(
                        content = {
                            ContactReadingScreen(
                                paymentViewModel = paymentViewModel,
                                navigateToVerificationMethod = { navHostController.navigate(Pinpad) },
                                navigateToOutcomeGraph = navigateToOutcomeGraph
                            )
                        }
                    )

                    composable<Pinpad>(
                        content = {
                            PinpadScreen(
                                paymentViewModel = paymentViewModel,
                                onCancel = backToPrevious,
                                navigateToRegisterPayment = { navHostController.navigate(ProcessPayment) },
                                navigateToOutcomeGraph = navigateToOutcomeGraph
                            )
                        }
                    )

                    composable<ProcessPayment>(
                        content = {
                            ProcessPaymentScreen(
                                paymentViewModel = paymentViewModel,
                                navigateToOutcomeGraph = navigateToOutcomeGraph
                            )
                        }
                    )
                }
            )
        }
    )
}