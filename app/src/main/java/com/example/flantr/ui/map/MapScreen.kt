package com.example.flantr.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.flantr.ui.routes.active.ActiveRouteViewModel
import com.google.android.gms.location.LocationServices

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: ActiveRouteViewModel, // Shared ViewModel
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // --- Permissions Logic ---
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            //TODO: Permission granted logic
        }
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    // --- UI ---
    Scaffold(
        floatingActionButton = {
            // STOP ROUTE BUTTON
            // Only show if a route is currently active on map
            if (uiState.isMapNavigationActive && uiState.activeRoute != null) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.stopNavigationOnMap() },
                    containerColor = Color(0xFFEF4444), // Red
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Close, null) },
                    text = { Text("Stop Route") }
                )
            }
        },
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = onBack,
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }

                Spacer(Modifier.width(16.dp))

                // ETA Card - Only show if active
                if (uiState.isMapNavigationActive && uiState.timeToFirstStop != null) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp
                    ) {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                            Icon(Icons.Default.Timer, null, tint = Color(0xFF8B5CF6))
                            Spacer(Modifier.width(8.dp))
                            Text("${uiState.timeToFirstStop} min to start")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            // Dynamically pass data to RouteMap based on state
            RouteMap(
                // If navigation is active, pass the stops. If not, pass empty list.
                stops = if (uiState.isMapNavigationActive) uiState.activeRoute?.stops ?: emptyList() else emptyList(),
                // If navigation is active, pass the points. If not, pass empty list.
                routePoints = if (uiState.isMapNavigationActive) uiState.routePoints else emptyList()
            )
        }
    }
}