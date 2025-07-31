package com.soleel.paymentapp.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceKey
import com.soleel.paymentapp.core.ui.R
import com.soleel.paymentapp.core.ui.utils.LongDevicePreview
import com.soleel.paymentapp.core.ui.utils.WithFakeSystemBars
import com.soleel.paymentapp.core.ui.utils.WithFakeTopAppBar
import com.soleel.paymentapp.feature.home.bills.BillsScreen
import com.soleel.paymentapp.feature.home.calculator.CalculatorScreen
import com.soleel.paymentapp.feature.home.catalog.CatalogScreen
import com.soleel.paymentapp.feature.home.profile.ProfileScreen
import com.soleel.paymentapp.feature.home.sales.SalesScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


@Serializable
object HomeGraph

fun NavGraphBuilder.homeNavigationGraph(
    navigateToSalesProcessGraph: (calculatorTotal: Float) -> Unit
) {
    composable<HomeGraph> {
        HomeScreen(
            navigateToSalesProcessGraph = navigateToSalesProcessGraph
        )
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
    homeViewModel: HomeViewModel = hiltViewModel(),
    navHostController: NavHostController = rememberNavController(),
    navigateToSalesProcessGraph: (calculatorTotal: Float) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentDestination: NavDestination? =
        navHostController.currentBackStackEntryAsState().value?.destination

    val topBarScreens = remember {
        listOf(
            HomeTopBarScreens.Calculator,
            HomeTopBarScreens.Catalog,
            HomeTopBarScreens.Bills,
            HomeTopBarScreens.Sales,
            HomeTopBarScreens.Profile
        )
    }

    val developerTogglesUiState: List<DeveloperToggleUiState> by homeViewModel.developerTogglesUiState.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Opciones de desarrollador",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                Divider()

                DeveloperPreferencesScreen(
                    developerTogglesUiState = developerTogglesUiState,
                    onToggleChanged = { event -> homeViewModel.onDeveloperToggleUiEvent(event) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Pago App",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        topBarScreens.forEach { screen ->
                            val isSelected = currentDestination?.hasRoute(screen::class) == true
                            IconButton(
                                onClick = {
                                    if (!isSelected) {
                                        navHostController.navigate(
                                            route = screen,
                                            navOptions = navOptions {
                                                popUpTo(navHostController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    painterResource(id = screen.icon),
                                    contentDescription = screen.name,
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )
            },
            content = { paddingValues ->
                NavHost(
                    navController = navHostController,
                    startDestination = HomeTopBarScreens.Calculator,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable<HomeTopBarScreens.Calculator> {
                        CalculatorScreen(navigateToSalesProcessGraph = navigateToSalesProcessGraph)
                    }
                    composable<HomeTopBarScreens.Catalog> { CatalogScreen() }
                    composable<HomeTopBarScreens.Bills> { BillsScreen() }
                    composable<HomeTopBarScreens.Sales> { SalesScreen() }
                    composable<HomeTopBarScreens.Profile> { ProfileScreen() }
                }
            }
        )
    }
}

@LongDevicePreview
@Composable
private fun DeveloperPreferencesScreenLongPreview() {
    val fakeToggles = listOf(
        DeveloperToggleUiState(
            developerPreferenceKey = DeveloperPreferenceKey.CONTACTLESS_READER_FALLBACK,
            isEnabled = true
        ),
        DeveloperToggleUiState(
            developerPreferenceKey = DeveloperPreferenceKey.CONTACTLESS_READER_INVALID_CARD,
            isEnabled = false
        ),
        DeveloperToggleUiState(
            developerPreferenceKey = DeveloperPreferenceKey.CONTACTLESS_READER_OTHER_ERROR,
            isEnabled = true
        ),
        DeveloperToggleUiState(
            developerPreferenceKey = DeveloperPreferenceKey.PAYMENT_CONFIRMATION_FAIL_BY_AUTH_ERROR,
            isEnabled = true
        )
    )

    WithFakeSystemBars(
        content = {
            WithFakeTopAppBar(
                content = {
                    DeveloperPreferencesScreen(
                        developerTogglesUiState= fakeToggles,
                        onToggleChanged = {}
                    )
                }
            )
        }
    )
}

@Composable
fun DeveloperPreferencesScreen(
    developerTogglesUiState: List<DeveloperToggleUiState>,
    onToggleChanged: (DeveloperToggleUiEvent) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        content = {
            items(developerTogglesUiState) { toggleUiState ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Column(
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            content = {
                                Text(
                                    text = toggleUiState.developerPreferenceKey.displayName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = toggleUiState.developerPreferenceKey.displayDescription,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )

                        Switch(
                            checked = toggleUiState.isEnabled,
                            onCheckedChange = {
                                onToggleChanged(
                                    DeveloperToggleUiEvent.ReverseDeveloperPreference(toggleUiState)
                                )
                            }
                        )
                    }
                )
            }
        }
    )
}