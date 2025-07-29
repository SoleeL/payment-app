package com.soleel.paymentapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soleel.paymentapp.feature.home.HomeGraph
import com.soleel.paymentapp.feature.home.homeNavigationGraph
import com.soleel.paymentapp.feature.salesprocess.SalesProcessGraph
import com.soleel.paymentapp.feature.salesprocess.salesProcessNavigationGraph
import com.soleel.paymentapp.feature.transactionprocess.transactionProcessNavigationGraph
import kotlinx.serialization.Serializable

@Serializable
object Loading

@Serializable
object Error

@Composable
fun PaymentAppNavigationGraph() {
    val navHostController: NavHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = HomeGraph,
        builder = {
            composable<Loading>(
                content = {
                    SplashScreen()
                }
            )

            composable<Error>(
                content = {
                    ErrorScreen()
                }
            )

            homeNavigationGraph(
                navigateToSalesProcessGraph = { calculatorTotal: Float ->
                    navHostController.navigate(SalesProcessGraph(calculatorTotal))
                }
            )

            salesProcessNavigationGraph(
                backToPrevious = { navHostController.popBackStack() }
            )

            transactionProcessNavigationGraph()
        }
    )
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(100.dp)
            )
        }
    )
}

@Composable
fun ErrorScreen(
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Algo salió mal",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                onRetry?.let {
                    Button(onClick = it) {
                        Text("Reintentar")
                    }
                }
            }
        }
    )
}