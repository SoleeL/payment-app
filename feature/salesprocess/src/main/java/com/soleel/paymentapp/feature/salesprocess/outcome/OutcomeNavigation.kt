package com.soleel.paymentapp.feature.salesprocess.outcome

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
data class OutcomeGraph(
    val sale: Sale,
    val paymentResult: PaymentResult
)

fun NavGraphBuilder.outcomeNavigationGraph(
    saleToNavType: Map<KType, NavType<Sale>>,
    paymentResultToNavType: Map<KType, NavType<PaymentResult>>,
    backToPrevious: () -> Unit
) {
    composable<OutcomeGraph>(
        typeMap = saleToNavType + paymentResultToNavType,
        content = { backStackEntry ->
            val sale: Sale = backStackEntry.toRoute<OutcomeGraph>().sale
            val paymentResult: PaymentResult = backStackEntry.toRoute<OutcomeGraph>().paymentResult
            val savedStateHandle: SavedStateHandle = backStackEntry.savedStateHandle
            savedStateHandle["sale"] = sale
            savedStateHandle["paymentResult"] = paymentResult
        }
    )
}