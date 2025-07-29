package com.soleel.paymentapp.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.soleel.paymentapp.core.ui.R
import com.soleel.paymentapp.feature.home.bills.BillsScreen
import com.soleel.paymentapp.feature.home.calculator.CalculatorScreen
import com.soleel.paymentapp.feature.home.catalog.CatalogScreen
import com.soleel.paymentapp.feature.home.profile.ProfileScreen
import com.soleel.paymentapp.feature.home.sales.SalesScreen
import kotlinx.serialization.Serializable


@Serializable
object HomeGraph

fun NavGraphBuilder.homeNavigationGraph() {
    composable<HomeGraph> {
        HomeScreen()
    }
}

@Serializable
sealed class HomeTopBarScreens<T>(val name: String, val icon: Int, val route: T) {
    @Serializable
    data object Calculator : HomeTopBarScreens<Calculator>(
        name = "Calculator",
        icon = R.drawable.ic_calculator,
        route = Calculator
    )

    @Serializable
    data object Catalog : HomeTopBarScreens<Catalog>(
        name = "Catalog",
        icon = R.drawable.ic_catalog,
        route = Catalog
    )

    @Serializable
    data object Bills : HomeTopBarScreens<Bills>(
        name = "Bills",
        icon = R.drawable.ic_bills,
        route = Bills
    )

    @Serializable
    data object Sales : HomeTopBarScreens<Sales>(
        name = "Sales",
        icon = R.drawable.ic_sales,
        route = Sales
    )

    @Serializable
    data object Profile : HomeTopBarScreens<Profile>(
        name = "Profile",
        icon = R.drawable.ic_commerce_profile,
        route = Profile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController = rememberNavController()
) {
    val currentDestination: NavDestination? = navHostController.currentBackStackEntryAsState()
        .value?.destination

    val topBarScreens: List<HomeTopBarScreens<*>> = remember(
        calculation = {
            listOf(
                HomeTopBarScreens.Calculator,
                HomeTopBarScreens.Catalog,
                HomeTopBarScreens.Bills,
                HomeTopBarScreens.Sales,
                HomeTopBarScreens.Profile
            )
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pago App")
                },
//                navigationIcon = {
//                    IconButton(
//                        onClick = { navHostController.navigate(route = Menu) },
//                        content = {
//                            Icon(Icons.Default.Menu, contentDescription = "Menu")
//                        }
//                    )
//                },
                actions = {
                    topBarScreens.forEach(action = { screen ->
                        val isSelected = currentDestination?.hasRoute(screen::class) == true
                        IconButton(
                            onClick = {
                                if (!isSelected) {
                                    navHostController.navigate(
                                        route = screen,
                                        navOptions = navOptions(
                                            optionsBuilder = {
                                                popUpTo(
                                                    id = navHostController.graph
                                                        .findStartDestination().id,
                                                    popUpToBuilder = {
                                                        //inclusive = true // SI QUIERO QUE SOLO EXISTA 1 SCREEN A LA VEZ Y AL RETROCEDER SE CIERRE LA APP
                                                        // README: Actualmente el retroceso siempre lleva a la calculadora
                                                        saveState = true
                                                    }
                                                )
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        )
                                    )
                                }
                            },
                            content = {
                                Icon(
                                    painterResource(id = screen.icon),
                                    contentDescription = screen.name,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    })
                }
            )
        },
        content = { paddingValues ->
            NavHost(
                navController = navHostController,
                startDestination = HomeTopBarScreens.Calculator,
                modifier = Modifier.padding(paddingValues),
                builder = {
                    composable<HomeTopBarScreens.Calculator>(
                        content = {
                            CalculatorScreen()
                        }
                    )

                    composable<HomeTopBarScreens.Catalog>(
                        content = {
                            CatalogScreen()
                        }
                    )

                    composable<HomeTopBarScreens.Bills>(
                        content = {
                            BillsScreen()
                        }
                    )

                    composable<HomeTopBarScreens.Sales>(
                        content = {
                            SalesScreen()
                        }
                    )

                    composable<HomeTopBarScreens.Profile>(
                        content = {
                            ProfileScreen()
                        }
                    )
                }
            )
        }
    )
}