package com.example.flantr.ui.routes.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flantr.data.model.Route
import com.example.flantr.ui.theme.BackgroundGradient
import com.example.flantr.ui.theme.PrimaryGradient
import com.example.flantr.ui.routes.favourites.components.EmptyFavouritesState
import com.example.flantr.ui.routes.favourites.components.CollectionCard

@Composable
fun FavouritesScreen(
    navController: NavController,
    viewModel: FavouritesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val displayRoutes = if (uiState.selectedCategory == "All") {
        uiState.favouriteRoutes
    } else {
        uiState.favouriteRoutes.filter { it.theme.contains(uiState.selectedCategory, ignoreCase = true) }
    }

    Scaffold(
        containerColor = Color.Transparent, // Let gradient show
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                // Title Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Favourites", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Your saved routes", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }

                    // Count Badge
                    Surface(
                        color = Color(0xFFFAF5FF), // Purple-50
                        shape = RoundedCornerShape(100),
                        modifier = Modifier.border(1.dp, Color(0xFFF3E8FF), RoundedCornerShape(100))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEC4899), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(uiState.favouriteRoutes.size.toString(), color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Categories
                val categories = listOf("All", "Bookstore", "Art", "Food", "Nature")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            label = category,
                            isSelected = uiState.selectedCategory == category,
                            onClick = { viewModel.selectCategory(category) }
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp), color = Color(0xFFE5E7EB))
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
            if (uiState.isLoading) {
                item { Text("Loading...", modifier = Modifier.padding(16.dp)) }
            } else if (displayRoutes.isEmpty()) {
                item {
                    EmptyFavouritesState()
                }
            } else {
                items(displayRoutes) { route ->
                    FavouriteRouteCard(
                        route = route,
                        onRemove = { viewModel.removeFavourite(route.id) },
                        onClick = { navController.navigate("active_route/${route.id}") }
                    )
                }
            }

            if (!uiState.isLoading && uiState.collections.isNotEmpty()) {
                item {
                    Text(
                        "Collections",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.collections) { collection ->
                            val (gradient, buttonColor) = getCollectionVisuals(collection.color)

                            CollectionCard(
                                title = collection.title,
                                count = collection.routeIds.size,
                                desc = collection.description,
                                gradient = gradient,
                                buttonColor = buttonColor,
                                modifier = Modifier.width(280.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(60.dp))
                }
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bg = if (isSelected) PrimaryGradient else Brush.linearGradient(listOf(Color.White, Color.White))
    val textColor = if (isSelected) Color.White else Color.Gray
    val border = if (isSelected) Color.Transparent else Color(0xFFE5E7EB)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(100))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, color = textColor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun getCollectionVisuals(colorName: String): Pair<Brush, Color> {
    return when (colorName.lowercase()) {
        "blue" -> Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF2563EB))) to Color(0xFF1E40AF)
        "purple" -> Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))) to Color(0xFF6D28D9)
        "pink" -> Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFDB2777))) to Color(0xFFBE185D)
        "orange" -> Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFEA580C))) to Color(0xFFC2410C)
        else -> Brush.linearGradient(listOf(Color(0xFF6B7280), Color(0xFF4B5563))) to Color(0xFF374151)
    }
}

@Composable
fun FavouriteRouteCard(route: Route, onRemove: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Image Area
            Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                AsyncImage(
                    model = route.imageUrl ?: "https://images.unsplash.com/photo-1587566657649-2b1a1a5c79e4", // Fallback
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Remove Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = "Remove", tint = Color(0xFFEC4899), modifier = Modifier.size(20.dp))
                }
            }

            // Content Area
            Column(modifier = Modifier.padding(16.dp)) {
                Text(route.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(route.distance, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFF3E8FF), shape = RoundedCornerShape(100)) {
                        Text(
                            route.theme,
                            color = Color(0xFF9333EA),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(16.dp))
                    Text("4.8", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                // Note (Simulated from description)
                if (route.description.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFFFEFCE8),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF08A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            route.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF854D0E),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(12.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onClick,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Start", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onRemove,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(40.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
