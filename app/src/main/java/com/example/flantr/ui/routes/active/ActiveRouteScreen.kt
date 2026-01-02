package com.example.flantr.ui.routes.active

import androidx.compose.foundation.background
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flantr.data.model.Route
import com.example.flantr.ui.theme.BackgroundGradient
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.core.app.ActivityCompat
import com.example.flantr.ui.theme.PurplePrimary
import com.google.android.gms.location.LocationServices

@Composable
fun ActiveRouteScreen(
    navController: NavController,
    viewModel: ActiveRouteViewModel,
    route: Route,
    onExit: () -> Unit,
    onOpenMap: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(route) {
        if (uiState.activeRoute == null) {
            viewModel.startNavigation(route)
        }
    }

    val activeRoute = uiState.activeRoute ?: route
    val stops = activeRoute.stops

    val currentStopIndex = uiState.currentStopIndex
    val currentStop = route.stops[currentStopIndex]

    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val userPace = "moderate"
    val userTransport = true

    val handleGetDirections = {
        viewModel.startNavigation(route)
        onOpenMap()
    }
    val progress by animateFloatAsState(
        targetValue = if (stops.isNotEmpty()) {
            val totalStops = stops.size
            val indexProgress = (currentStopIndex.toFloat()) / totalStops
            val isCurrentCompleted = uiState.completedStops.contains(stops[currentStopIndex].id)
            val completionBoost = if (isCurrentCompleted) 1f / totalStops else 0f

            (indexProgress + completionBoost).coerceAtMost(1f)
        } else 0f,
        label = "progress"
    )

    val remainingTime = route.stops
        .drop(currentStopIndex + 1) // Logic: Time to reach all FUTURE stops
        .sumOf { stop ->
            val stopIndex = route.stops.indexOf(stop)
            uiState.calculatedDurations[stopIndex] ?: stop.estimatedTimeMinutes
        }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null && route.stops.isNotEmpty()) {
                        viewModel.calculateStartEta(
                            loc.latitude,
                            loc.longitude,
                            route.stops[0],
                            userPace,
                            userTransport
                        )
                    }
                }
            }
        }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ActiveRouteTopBar(
                currentIndex = currentStopIndex,
                totalStops = stops.size,
                progress = progress,
                onExit = onExit
            )
        }
    ) { padding ->

        if (currentStop == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PurplePrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGradient)
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

            /* -------- Route Info -------- */
            item {
                Column {
                    Text(
                        route.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        route.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            /* -------- Current Stop -------- */
            item {
                CurrentStopCard(
                    stop = currentStop,
                    index = currentStopIndex,
                    isCompleted = uiState.completedStops.contains(currentStop.id),
                    userNote = uiState.userNotes[currentStop.id],
                    isEditingNote = uiState.editingNoteId == currentStop.id,
                    onGetDirections = handleGetDirections,
                    onEditNote = {
                        viewModel.setEditingNote(
                            if (it) currentStop.id else null
                        )
                    },
                    onUpdateNote = {
                        viewModel.updateUserNote(currentStop.id, it)
                    }
                )
            }

            /* -------- Navigation Buttons -------- */
            item {
                NavigationButtons(
                    isFirst = currentStopIndex == 0,
                    isLast = currentStopIndex == route.stops.lastIndex,
                    showComplete = !uiState.completedStops.contains(currentStop.id),
                    onPrevious = { viewModel.previousStop() },
                    onComplete = { viewModel.completeStop() },
                    onNext = { viewModel.nextStop() }
                )
            }

            /* -------- Route Overview -------- */
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Route Overview", fontWeight = FontWeight.Bold)
                            Text(
                                "${remainingTime / 60}h ${remainingTime % 60}m remaining",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        route.stops.forEachIndexed { index, stop ->
                            val displayTime =
                                if (index == 0) null
                                else uiState.calculatedDurations[index]
                                    ?: stop.estimatedTimeMinutes

                            OverviewStopItem(
                                index = index,
                                stop = stop,
                                isCurrent = index == currentStopIndex,
                                isCompleted = uiState.completedStops.contains(stop.id),
                                displayTime = displayTime,
                                onClick = { viewModel.jumpToStop(index) }
                            )

                            if (index < route.stops.lastIndex) {
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            /* -------- Owner Actions -------- */
            item {
                val canEdit = uiState.currentUserId.isNotEmpty() &&
                        route.authorId.isNotEmpty() &&
                        route.authorId == uiState.currentUserId
                if (canEdit) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(Modifier.padding(bottom = 16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* TODO */ },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Edit")
                            }

                            Button(
                                onClick = {
                                    viewModel.deleteRoute(route.id) {
                                        onExit()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF4444)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
}