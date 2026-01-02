package com.example.flantr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flantr.ui.auth.AuthScreen
import com.example.flantr.ui.home.HomeScreen
import com.example.flantr.ui.home.HomeViewModel
import com.example.flantr.ui.map.MapScreen
import com.example.flantr.ui.navigation.FlantrBottomBar
import com.example.flantr.ui.profile.ProfileScreen
import com.example.flantr.ui.routes.active.ActiveRouteScreen
import com.example.flantr.ui.routes.active.ActiveRouteViewModel // Added Import
import com.example.flantr.ui.routes.create.CreateRouteScreen
import com.example.flantr.ui.routes.favourites.FavouritesScreen
import com.example.flantr.ui.theme.FlantrTheme
import com.example.flantr.ui.trips.RoutesOverviewScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlantrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlantrApp()
                }
            }
        }
    }
}

@Composable
fun FlantrApp() {
    val navController = rememberNavController()

    // 1. Hoist the ViewModel here so it survives navigation changes
    // This instance will be shared between ActiveRouteScreen and MapScreen
    val activeRouteViewModel: ActiveRouteViewModel = viewModel()

    Scaffold(
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != "auth") {
                FlantrBottomBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "auth",
            modifier = Modifier.padding(padding)
        ) {

            composable("auth") {
                AuthScreen(navController = navController)
            }

            composable("home") {
                HomeScreen(navController = navController)
            }

            composable("profile") {
                ProfileScreen(navController = navController)
            }

            composable("active_route/{routeId}") { backStackEntry ->
                val routeId = backStackEntry.arguments?.getString("routeId") ?: ""

                LaunchedEffect(routeId) {
                    activeRouteViewModel.loadRoute(routeId)
                }
                // We need the HomeViewModel just to look up the route data
                val homeViewModel: HomeViewModel = viewModel()
                val uiState by homeViewModel.uiState.collectAsState()
                val activeUiState by activeRouteViewModel.uiState.collectAsState()

                val allRoutes = uiState.popularRoutes + uiState.savedRoutes
                val selectedRoute = allRoutes.find { it.id == routeId }

                if (activeUiState.activeRoute != null) {
                    ActiveRouteScreen(
                        navController = navController,
                        route = activeUiState.activeRoute!!,
                        viewModel = activeRouteViewModel, // PASS THE SHARED INSTANCE
                        onExit = { navController.popBackStack() },
                        onOpenMap = { navController.navigate("map") }
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            composable("favouriteRoutes") {
                FavouritesScreen(navController = navController)
            }

            composable("CreateRoute") {
                CreateRouteScreen(navController = navController)
            }

            composable("routesOverview") {
                RoutesOverviewScreen(navController = navController)
            }

            composable("map") {
                MapScreen(
                    navController = navController,
                    viewModel = activeRouteViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}