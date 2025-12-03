package com.example.flantr.ui.navigation

sealed class Screen(val route: String){
    data object Authentication : Screen("auth")
    data object Home : Screen("home")
}