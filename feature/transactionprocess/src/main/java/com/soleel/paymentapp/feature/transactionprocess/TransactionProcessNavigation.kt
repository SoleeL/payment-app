package com.soleel.paymentapp.feature.transactionprocess

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.soleel.paymentapp.feature.transactionprocess.screens.ContactReadingScreen
import com.soleel.paymentapp.feature.transactionprocess.screens.ContactlessReadingScreen
import com.soleel.paymentapp.feature.transactionprocess.screens.PinpadScreen
import com.soleel.paymentapp.feature.transactionprocess.screens.RegisterTransactionScreen
import kotlinx.serialization.Serializable


@Serializable
object TransactionProcessGraph

@Serializable
object ContactlessReading

@Serializable
object ContactReading

@Serializable
object Pinpad

@Serializable
object RegisterTransaction

fun NavGraphBuilder.transactionProcessNavigationGraph() {
    navigation<TransactionProcessGraph>(startDestination = ContactlessReading) {
        composable<ContactlessReading>(
            content = {
                ContactlessReadingScreen()
            }
        )

        composable<ContactReading>(
            content = {
                ContactReadingScreen()
            }
        )

        composable<Pinpad>(
            content = {
                PinpadScreen()
            }
        )

        composable<RegisterTransaction>(
            content = {
                RegisterTransactionScreen()
            }
        )
    }
}