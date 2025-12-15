package com.example.flantr.ui.navigation

sealed class Screen(val route: String){
    data object Authentication : Screen("auth")
    data object Home : Screen("home")
    data object Profile: Screen("profile")
    data object ActiveRoute: Screen("activeRoute")
    data object FavouriteRoutes: Screen("favouriteRoutes")
    data object CreateRoute: Screen("createRoute")
}