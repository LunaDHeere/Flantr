package com.example.flantr.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.text.font.FontWeight
import com.example.flantr.ui.theme.GrayBorder
import com.example.flantr.ui.theme.GrayText
import com.example.flantr.ui.theme.PurplePrimary

private val ActiveItemGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFEFF6FF), Color(0xFFFAF5FF))
)

sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Trips : BottomNavItem("routesOverview", "Trips", Icons.Default.Explore) // Changed label from "routesOverview" to "Trips"
    object Map : BottomNavItem("map", "Map", Icons.Default.Map)
    object Favourites : BottomNavItem("favouriteRoutes", "Favourites", Icons.Default.Favorite)
    object Account : BottomNavItem("profile", "Account", Icons.Default.Person)
}

@Composable
fun FlantrBottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Trips,
        BottomNavItem.Map,
        BottomNavItem.Favourites,
        BottomNavItem.Account
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            HorizontalDivider(thickness = 1.dp, color = GrayBorder)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    Column(
                        modifier = Modifier
                            .background(
                                brush = if (isSelected) ActiveItemGradient else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        // FIX: Pop up to the HOME route, not the graph start (which might be Auth)
                                        // This ensures we don't build a huge stack of screens
                                        popUpTo(BottomNavItem.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) PurplePrimary else GrayText,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) PurplePrimary else GrayText
                        )
                    }
                }
            }
        }
    }
}