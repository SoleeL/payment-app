package com.soleel.paymentapp.feature.salesprocess.payment

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import kotlinx.serialization.Serializable
import kotlin.reflect.KType

@Serializable
data class PaymentGraph(val sale: Sale)

fun NavGraphBuilder.paymentNavigationGraph(
    saleToNavType: Map<KType, NavType<Sale>>,
    backToPrevious: () -> Unit,
    onRetryPaymentMethod: (Sale) -> Unit,
    onSelectAnotherPaymentMethod: () -> Unit,
    navigateToOutcomeGraph: (sale: Sale, paymentResult: PaymentResult) -> Unit
) {
    composable<PaymentGraph>(
        typeMap = saleToNavType,
        content = { backStackEntry ->
            val sale: Sale = backStackEntry.toRoute<PaymentGraph>().sale
            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["sale"] = sale
        }
    )
}
