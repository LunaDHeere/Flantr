package com.example.flantr.ui.routes.active

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flantr.data.model.Stop
import com.example.flantr.ui.theme.*

/* ---------------- TOP BAR ------------------------ */

@Composable
fun ActiveRouteTopBar(
    currentIndex: Int,
    totalStops: Int,
    progress: Float,
    onExit: () -> Unit
) {
    Column(Modifier.background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onExit, colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
                Text("Exit Route")
            }
            Text(
                "Stop ${currentIndex + 1} of $totalStops",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = PurplePrimary,
            trackColor = Color(0xFFE5E7EB),
            drawStopIndicator = {}
        )
    }
}

/* ---------------- NAV BUTTONS -------------------- */

@Composable
fun NavigationButtons(
    isFirst: Boolean,
    isLast: Boolean,
    showComplete: Boolean,
    onPrevious: () -> Unit,
    onComplete: () -> Unit,
    onNext: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        Button(
            onClick = onPrevious,
            enabled = !isFirst,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Gray),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            modifier = Modifier.weight(1f).height(56.dp)
        ) { Text("Previous") }

        if (showComplete) {
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Complete")
            }
        }

        Button(
            onClick = onNext,
            enabled = !isLast,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).height(56.dp)
        ) {
            Box(
                Modifier.fillMaxSize().background(PrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isLast) "Last Stop" else "Next")
                    if (!isLast) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                    }
                }
            }
        }
    }
}

/* ---------------- OVERVIEW ITEM ------------------ */

@Composable
fun OverviewStopItem(
    index: Int,
    stop: Stop,
    isCurrent: Boolean,
    isCompleted: Boolean,
    displayTime: Int?,
    onClick: () -> Unit
) {
    val bg = when {
        isCurrent -> Color(0xFFFAF5FF)
        isCompleted -> Color(0xFFF0FDF4)
        else -> Color(0xFFF9FAFB)
    }

    val border = when {
        isCurrent -> Color(0xFFE9D5FF)
        isCompleted -> Color(0xFFDCFCE7)
        else -> Color(0xFFE5E7EB)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> SolidColor(Color(0xFF22C55E))
                        isCurrent -> PrimaryGradient
                        else -> SolidColor(Color(0xFFD1D5DB))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    (index + 1).toString(),
                    color = if (isCurrent) Color.White else Color(0xFF4B5563),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(stop.name, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
            if (displayTime != null) {
                Text("$displayTime min", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        if (isCurrent) {
            Icon(Icons.Default.NearMe, null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
        }
    }
}


@Composable
fun CurrentStopCard(
    stop: Stop,
    index: Int,
    isCompleted: Boolean,
    userNote: String?,
    isEditingNote: Boolean,
    onGetDirections: () -> Unit,
    onEditNote: (Boolean) -> Unit,
    onUpdateNote: (String) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE9D5FF)) // Light Purple Border
    ) {
        Column(Modifier.padding(24.dp)) {
            // Header
            Row(verticalAlignment = Alignment.Top) {
                // Big Number Badge
                Box(
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text((index + 1).toString(), color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(16.dp))

                // Info
                Column(Modifier.weight(1f)) {
                    Text(stop.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(stop.address, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }

                // Completed Badge
                if (isCompleted) {
                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(100), contentColor = Color(0xFF15803D)) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Done", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(stop.description, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
            Spacer(Modifier.height(24.dp))

            // Time & Directions
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = PurplePrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("${stop.estimatedTimeMinutes} mins")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onGetDirections() }
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PurplePrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Get Directions", color = PurplePrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // 1. Original Notes (Blue Box)
            if (stop.notes != null) {
                Surface(
                    color = Color(0xFFEFF6FF), // Blue 50
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(Modifier.padding(16.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB)) // Blue 600
                        Spacer(Modifier.width(12.dp))
                        Text(stop.notes, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1E3A8A))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // 2. Personal Notes (Yellow Box)
            Surface(
                color = Color(0xFFFEFCE8), // Yellow 50
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF08A))
            ) {
                Column(Modifier.padding(16.dp).fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = Color(0xFFCA8A04)) // Yellow 600
                        Spacer(Modifier.width(8.dp))
                        Text("Your Notes", fontWeight = FontWeight.Bold, color = Color(0xFF854D0E))
                    }

                    if (isEditingNote) {
                        OutlinedTextField(
                            value = userNote ?: "",
                            onValueChange = onUpdateNote,
                            modifier = Modifier.fillMaxWidth().background(Color.White),
                            placeholder = { Text("Add your own notes...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFCA8A04),
                                unfocusedBorderColor = Color(0xFFFEF08A)
                            )
                        )
                        TextButton(
                            onClick = { onEditNote(false) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Done", color = Color(0xFFCA8A04))
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEditNote(true) }
                                .padding(vertical = 4.dp)
                        ) {
                            if (!userNote.isNullOrEmpty()) {
                                Text(userNote, color = Color(0xFF713F12))
                            } else {
                                Text("Click to add notes...", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

}
