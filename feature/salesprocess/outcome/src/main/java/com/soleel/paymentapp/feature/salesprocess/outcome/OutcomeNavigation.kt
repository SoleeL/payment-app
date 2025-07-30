package com.soleel.paymentapp.feature.salesprocess.outcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.toRoute
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import kotlinx.serialization.Serializable
import kotlin.reflect.KType

@Serializable
data class OutcomeGraph(val paymentResult: PaymentResult)

fun NavGraphBuilder.outcomeNavigationGraph(
    paymentResultToNavType: Map<KType, NavType<PaymentResult>>,
) {
    composable<OutcomeGraph>(
        typeMap = paymentResultToNavType,
        content = { backStackEntry ->
            val paymentResult: PaymentResult = backStackEntry.toRoute<OutcomeGraph>().paymentResult
            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["paymentResult"] = paymentResult
            OutcomeScreen()
        }
    )
}

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
fun OutcomeScreen(
    navHostController: NavHostController = rememberNavController(),
    outcomeViewModel: OutcomeViewModel = hiltViewModel()
) {
    val currentDestination: NavDestination? = navHostController.currentBackStackEntryAsState()
        .value?.destination
    val isAtStartDestination: Boolean =
        currentDestination?.hasRoute(RegisterSale::class) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // README: Si se borra el content no es detectado
                modifier = Modifier.background(Color.DarkGray),
                actions = {
                    Surface(
                        modifier = Modifier.padding(16.dp, 2.dp),
//                        onClick = { // TODO: Para las vistas de error, si puedes tener cancel
//                            backToPrevious()
//                        },
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
                startDestination = RegisterSale,
                modifier = Modifier.padding(paddingValues),
                builder = {
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