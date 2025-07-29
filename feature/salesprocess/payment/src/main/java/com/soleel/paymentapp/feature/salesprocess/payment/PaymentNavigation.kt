package com.soleel.paymentapp.feature.salesprocess.payment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.soleel.paymentapp.feature.salesprocess.payment.screens.ContactReadingScreen
import com.soleel.paymentapp.feature.salesprocess.payment.screens.ContactlessReadingScreen
import com.soleel.paymentapp.feature.salesprocess.payment.screens.PinpadScreen
import com.soleel.paymentapp.feature.salesprocess.payment.screens.RegisterPaymentScreen
import kotlinx.serialization.Serializable


@Serializable
object PaymentGraph

@Serializable
object ContactlessReading

@Serializable
object ContactReading

@Serializable
object Pinpad

@Serializable
object RegisterPayment

fun NavGraphBuilder.paymentNavigationGraph() {
    navigation<PaymentGraph>(startDestination = ContactlessReading) {
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

        composable<RegisterPayment>(
            content = {
                RegisterPaymentScreen()
            }
        )
    }
}