package com.example.flantr.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flantr.data.model.Route
import com.example.flantr.ui.theme.PrimaryGradient

/* ---------- HEADER ---------- */

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
        HomeLogo()
        HomeCreateButton(onCreateRoute)
    }
}

@Composable
private fun HomeLogo() {
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
}

@Composable
private fun HomeCreateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
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
                Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ---------- HERO ---------- */

@Composable
fun HomeHeroSection() {
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Explore Your City",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Whether traveling or rediscovering, find your perfect route.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/* ---------- SECTION HEADER ---------- */

@Composable
fun HomeSectionHeader(
    title: String,
    icon: ImageVector,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side: Icon + Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF9333EA))
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (onSeeAllClick != null) {
            TextButton(onClick = onSeeAllClick) {
                Text("See All", color = Color(0xFF9333EA))
            }
        }
    }
}

/* ---------- SAVED ROUTES ---------- */
@Composable
fun HomeSavedRouteItem(
    route: Route,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(route.name, fontWeight = FontWeight.Bold)
                Text(
                    route.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    HomeBadgeInfo(Icons.Default.Place, "${route.stops.size} stops")
                    Spacer(Modifier.width(12.dp))
                    HomeBadgeInfo(Icons.Default.Schedule, "${route.totalTimeMinutes} min")
                }
            }

            // Remove Button (Filled Heart)
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Unsave",
                    tint = Color(0xFFEF4444) // Red color
                )
            }
        }
    }
}
/* ---------- POPULAR ROUTES ---------- */

@Composable
fun HomePopularRouteCard(
    route: Route,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            HomeRouteImage(route, isSaved, onToggleSave)
            HomeRouteContent(route, onClick)
        }
    }
}
@Composable
private fun HomeRouteImage(
    route: Route,
    isSaved: Boolean,
    onToggleSave: () -> Unit
) {
    Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
        AsyncImage(
            model = route.imageUrl,
            contentDescription = route.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Stops Badge (Top Right)
        Surface(
            shape = RoundedCornerShape(100),
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
        ) {
            Text(
                "${route.stops.size} stops",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Bookmark Button (Top Left)
        // Using a Surface for better visibility over images
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
        ) {
            IconButton(
                onClick = onToggleSave,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Bookmark",
                    tint = if (isSaved) Color(0xFFEF4444) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeRouteContent(route: Route, onClick: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        HomeThemeBadge(route.theme)

        Text(route.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(route.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        Row {
            HomeBadgeInfo(Icons.Default.Schedule, "${route.totalTimeMinutes / 60}h ${route.totalTimeMinutes % 60}m")
            Spacer(Modifier.width(16.dp))
            HomeBadgeInfo(Icons.Default.Map, route.distance)
        }

        Spacer(Modifier.height(16.dp))

        HomeStartButton(onClick)
    }
}

@Composable
private fun HomeThemeBadge(theme: String) {
    Surface(
        color = Color(0xFFF3E8FF),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            getThemeIcon(theme),
            contentDescription = null,
            tint = Color(0xFF9333EA),
            modifier = Modifier.padding(6.dp)
        )
    }
}

@Composable
private fun HomeStartButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxWidth()
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

/* ---------- SMALL HELPERS ---------- */

@Composable
fun HomeBadgeInfo(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}