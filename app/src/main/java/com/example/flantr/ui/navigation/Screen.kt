package com.example.flantr.ui.navigation

sealed class Screen(val route: String){
    data object Authentication : Screen("auth")
    data object Home : Screen("home")
    data object Profile: Screen("profile")
}