package com.example.flantr.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flantr.data.model.Route

// --- COLORS (Matches your previous theme) ---
private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFEFF6FF), Color.White, Color(0xFFFAF5FF)) // Blue-50 to Purple-50
)
private val PrimaryGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF3B82F6), Color(0xFF9333EA)) // Blue to Purple
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Main Scaffold (Standard Android layout structure)
    Scaffold(
        topBar = { HomeHeader(onCreateRoute = { /* TODO */ }) }
    ) { paddingValues ->

        // LazyColumn is like a Recycler View (Scrollable List)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // Margins
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // 1. HERO SECTION
            item {
                Column(
                    modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Explore Your City", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Whether traveling or rediscovering, find your perfect route.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // 2. SAVED ROUTES SECTION
            if (uiState.savedRoutes.isNotEmpty()) {
                item { SectionHeader(title = "My Saved Routes", icon = Icons.Default.Bookmark) }
                items(uiState.savedRoutes) { route ->
                    SavedRouteItem(route = route, onClick = { /* Start Route */ })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // 3. POPULAR ROUTES SECTION
            item { SectionHeader(title = "Popular Routes", icon = Icons.Default.TrendingUp) }
            items(uiState.popularRoutes) { route ->
                PopularRouteCard(route = route, onClick = { /* Start Route */ })
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Spacer
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun HomeHeader(onCreateRoute: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo Area
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryGradient)
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Flantr", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Discover your city", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        // Create Button
        Button(
            onClick = onCreateRoute,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            // Gradient Border/Background Hack for Buttons
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGradient)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, contentDescription = null, tint = Color(0xFF9333EA)) // Purple
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SavedRouteItem(route: Route, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(route.name, fontWeight = FontWeight.Bold)
                Text(route.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    BadgeInfo(icon = Icons.Default.Place, text = "${route.stops.size} stops")
                    Spacer(modifier = Modifier.width(12.dp))
                    BadgeInfo(icon = Icons.Default.Schedule, text = "${route.totalTimeMinutes} min")
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun PopularRouteCard(route: Route, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // IMAGE SECTION
            Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                AsyncImage(
                    model = route.imageUrl,
                    contentDescription = route.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Stop Count Badge
                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                ) {
                    Text(
                        text = "${route.stops.size} stops",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // CONTENT SECTION
            Column(modifier = Modifier.padding(16.dp)) {
                // Theme Icon Badge
                Surface(
                    color = Color(0xFFF3E8FF), // Light Purple
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        getThemeIcon(route.theme),
                        contentDescription = null,
                        tint = Color(0xFF9333EA),
                        modifier = Modifier.padding(6.dp).size(20.dp)
                    )
                }

                Text(route.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(route.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    BadgeInfo(Icons.Default.Schedule, "${route.totalTimeMinutes / 60}h ${route.totalTimeMinutes % 60}m")
                    Spacer(modifier = Modifier.width(16.dp))
                    BadgeInfo(Icons.Default.Map, route.distance)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryGradient)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Start Route", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Helper for small icons with text (e.g. "3 stops")
@Composable
fun BadgeInfo(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

// Logic to pick icon based on string (from your Figma code)
fun getThemeIcon(theme: String): ImageVector {
    return when {
        theme.contains("Coffee") || theme.contains("Book") -> Icons.Default.MenuBook
        theme.contains("Art") -> Icons.Default.Palette
        theme.contains("Food") -> Icons.Default.LocalCafe
        else -> Icons.Default.Place
    }
}