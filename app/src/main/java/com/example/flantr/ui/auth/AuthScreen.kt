package com.example.flantr.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable

fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
){

    val uiState by viewModel.uiState.collectAsState()

    //TODO: Add layout here

}