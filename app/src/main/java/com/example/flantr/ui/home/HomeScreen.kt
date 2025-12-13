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

    Scaffold(
        topBar = {
            HomeHeader(
                onCreateRoute = { /* TODO */ }
            )
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

            item {
                HomeHeroSection()
            }

            if (uiState.savedRoutes.isNotEmpty()) {
                item {
                    HomeSectionHeader(
                        title = "My Saved Routes",
                        icon = Icons.Default.Bookmark
                    )
                }

                items(uiState.savedRoutes) { route ->
                    HomeSavedRouteItem(route) {
                        navController.navigate("active_route/${route.id}")
                    }
                }
            }

            item {
                HomeSectionHeader(
                    title = "Popular Routes",
                    icon = Icons.Default.TrendingUp
                )
            }

            items(uiState.popularRoutes) { route ->
                HomePopularRouteCard(route) {
                    navController.navigate("active_route/${route.id}")
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
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