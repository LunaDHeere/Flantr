package com.example.flantr.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flantr.ui.theme.BackgroundGradient
import com.example.flantr.ui.theme.PrimaryGradient

@Composable
fun TripsScreen(
    navController: NavController,
    viewModel: TripsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Filter Data Constants
    val themes = listOf("All", "Custom", "Exploration", "Health & Wellness", "Photography", "Food & Drinks", "Art & Culture")
    val durations = listOf("All", "Under 2h", "2-4h", "Over 4h")
    val distances = listOf("All", "Under 2 miles", "2-3 miles", "Over 3 miles")

    val hasActiveFilters = uiState.selectedTheme != "All" || uiState.selectedDuration != "All" || uiState.selectedDistance != "All"

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(Modifier.background(Color.White)) {
                // Top Header
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("My Trips", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Your saved routes and adventures", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    // New Trip Button
                    Button(
                        onClick = { navController.navigate("CreateRoute") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryGradient)
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("New Trip", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Search Bar
                Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearch(it) },
                        placeholder = { Text("Search trips, stops...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        trailingIcon = if (uiState.searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { viewModel.updateSearch("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                            focusedContainerColor = Color.White
                        )
                    )
                }

                // Filter Toggle Bar
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    FilterToggleButton(
                        isActive = uiState.showFilters || hasActiveFilters,
                        hasFilters = hasActiveFilters,
                        onClick = { viewModel.toggleFilters() }
                    )
                }

                // Expandable Filter Panel
                if (uiState.showFilters) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB))
                            .padding(16.dp)
                    ) {
                        FilterSection("Theme", themes, uiState.selectedTheme) { viewModel.setFilterTheme(it) }
                        FilterSection("Duration", durations, uiState.selectedDuration) { viewModel.setFilterDuration(it) }
                        FilterSection("Distance", distances, uiState.selectedDistance) { viewModel.setFilterDistance(it) }

                        if (hasActiveFilters) {
                            Text(
                                "Clear all filters",
                                color = Color(0xFF9333EA),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 8.dp).clickable { viewModel.clearFilters() }
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFE5E7EB))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Collections Section
            if (uiState.collections.isNotEmpty() && !hasActiveFilters && uiState.searchQuery.isEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color(0xFF9333EA))
                        Spacer(Modifier.width(8.dp))
                        Text("Collections", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    LazyRow {
                        items(uiState.collections) { collection ->
                            // Count actual routes in this collection
                            val routeCount = uiState.allRoutes.count { it.id in collection.routeIds }
                            TripCollectionCard(collection, routeCount) {
                                // TODO: Navigate to collection detail or filter list
                            }
                        }
                    }
                }
            }

            // All Routes Header
            item {
                Text("All Routes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // Empty State
            if (uiState.filteredRoutes.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier.size(80.dp).clip(RoundedCornerShape(50)).background(Color(0xFFF3E8FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(40.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if(hasActiveFilters || uiState.searchQuery.isNotEmpty()) "No trips found" else "No trips yet",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Try adjusting filters or create a new one.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                // Route List
                items(uiState.filteredRoutes) { route ->
                    TripRouteCard(
                        route = route,
                        onStart = { navController.navigate("active_route/${route.id}") },
                        onEdit = { /* Navigate to Edit */ },
                        onDelete = { viewModel.deleteRoute(route.id) }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun FilterToggleButton(isActive: Boolean, hasFilters: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) Color(0xFF9333EA) else Color(0xFFF3F4F6),
            contentColor = if (isActive) Color.White else Color.Black
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text("Filters")
        if (hasFilters && !isActive) {
            Spacer(Modifier.width(8.dp))
            Box(Modifier.clip(RoundedCornerShape(4.dp)).background(Color.White).padding(horizontal = 4.dp)) {
                Text("Active", color = Color(0xFF9333EA), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun FilterSection(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(Modifier.padding(bottom = 12.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Using a LazyRow just in case chips overflow
            LazyRow {
                items(options) { option ->
                    TripsFilterChip(
                        label = option,
                        isSelected = option == selected,
                        onClick = { onSelect(option) }
                    )
                }
            }
        }
    }
}