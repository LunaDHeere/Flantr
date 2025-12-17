package com.example.flantr.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flantr.ui.theme.BackgroundGradient

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Configuration Limits
    val savedRoutesLimit = 2
    val popularRoutesLimit = 4

    // Helper set for O(1) lookups to check if a route is saved
    val savedRouteIds = remember(uiState.savedRoutes) {
        uiState.savedRoutes.map { it.id }.toSet()
    }

    Scaffold(
        topBar = {
            HomeHeader(onCreateRoute = { navController.navigate("createRoute") })
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            item { HomeHeroSection() }

            // Saved Routes Section
            if (uiState.savedRoutes.isNotEmpty()) {
                item {
                    HomeSectionHeader(
                        title = "My Saved Routes",
                        icon = Icons.Default.Bookmark,
                        // Only show "See All" if we have more routes than the limit
                        onSeeAllClick = if (uiState.savedRoutes.size > savedRoutesLimit) {
                            { navController.navigate("favouriteRoutes") }
                        } else null
                    )
                }

                // Only display the first 'savedRoutesLimit' items
                items(uiState.savedRoutes.take(savedRoutesLimit)) { route ->
                    HomeSavedRouteItem(
                        route = route,
                        onRemove = { viewModel.bookmarkRoute(route) },
                        onClick = { navController.navigate("active_route/${route.id}") }
                    )
                }
            }

            // Popular Routes Section
            item {
                HomeSectionHeader(
                    title = "Popular Routes",
                    icon = Icons.Default.TrendingUp,
                    onSeeAllClick = if (uiState.popularRoutes.size > popularRoutesLimit) {
                        { navController.navigate("routesOverview") }
                    } else null
                )
            }

            // Only display the first 'popularRoutesLimit' items
            items(uiState.popularRoutes.take(popularRoutesLimit)) { route ->
                HomePopularRouteCard(
                    route = route,
                    isSaved = savedRouteIds.contains(route.id),
                    onToggleSave = { viewModel.bookmarkRoute(route) },
                    onClick = { navController.navigate("active_route/${route.id}") }
                )
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun getThemeIcon(theme: String): ImageVector {
    return when {
        theme.contains("Coffee", true) || theme.contains("Book", true) -> Icons.Default.MenuBook
        theme.contains("Art", true) -> Icons.Default.Palette
        theme.contains("Food", true) -> Icons.Default.LocalCafe
        else -> Icons.Default.Place
    }
}