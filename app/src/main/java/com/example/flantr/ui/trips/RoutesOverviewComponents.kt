package com.example.flantr.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flantr.data.model.Collection
import com.example.flantr.data.model.Route
import com.example.flantr.ui.theme.PrimaryGradient

@Composable
fun TripsFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFF9333EA) else Color.White, // Purple or White
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)) else null,
        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}

@Composable
fun TripCollectionCard(
    collection: Collection,
    routeCount: Int,
    onClick: () -> Unit
) {
    // Map colors strings to Compose colors
    val (bgColor, borderColor, textColor) = when(collection.color) {
        "purple" -> Triple(Color(0xFFFAF5FF), Color(0xFFE9D5FF), Color(0xFF7E22CE))
        "green" -> Triple(Color(0xFFF0FDF4), Color(0xFFBBF7D0), Color(0xFF15803D))
        "pink" -> Triple(Color(0xFFFDF2F8), Color(0xFFFBCFE8), Color(0xFFBE185D))
        else -> Triple(Color(0xFFEFF6FF), Color(0xFFBFDBFE), Color(0xFF1D4ED8)) // Blue default
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .padding(end = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(collection.title, fontWeight = FontWeight.Bold, color = textColor)
                    Text(
                        collection.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                Icon(Icons.Outlined.Edit, contentDescription = null, tint = borderColor)
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("$routeCount routes", style = MaterialTheme.typography.labelSmall, color = textColor)
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = textColor),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                Text("View Collection", fontSize = 12.sp)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun TripRouteCard(
    route: Route,
    isFavourited: Boolean,
    onToggleFavourite: () -> Unit,
    onStart: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(route.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(route.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            IconButton(onClick = onToggleFavourite) {
                Icon(
                    imageVector = if (isFavourited) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = if (isFavourited) Color(0xFFEF4444) else Color.Gray
                )
            }


            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TripStat(Icons.Default.Place, "${route.stops.size} stops")
                TripStat(Icons.Default.Schedule, "${route.totalTimeMinutes / 60}h ${route.totalTimeMinutes % 60}m")
                TripStat(Icons.Default.Map, route.distance)
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Stops:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    route.stops.take(3).forEachIndexed { index, stop ->
                        Row(Modifier.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(PrimaryGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text((index + 1).toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(stop.name, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (route.stops.size > 3) {
                        Text("+${route.stops.size - 3} more stops", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(start = 28.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onStart,
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Start Trip", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TripStat(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}