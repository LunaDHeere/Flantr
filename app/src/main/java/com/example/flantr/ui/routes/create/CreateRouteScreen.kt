package com.example.flantr.ui.routes.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flantr.data.model.Stop
import com.example.flantr.ui.theme.BackgroundGradient
import com.example.flantr.ui.theme.PrimaryGradient

@Composable
fun CreateRouteScreen(
    navController: NavController,
    viewModel: CreateRouteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(Modifier.background(Color.White)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text("Back", color = Color.Gray)
                    }

                    Button(
                        onClick = {
                            viewModel.saveRoute {
                                navController.popBackStack()
                            }
                        },
                        enabled = !uiState.isSaving && uiState.routeName.isNotBlank(),
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
                                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (uiState.isSaving) "Saving..." else "Save Route", color = Color.White, fontWeight = FontWeight.Bold)
                            }
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
            // 1. Route Details Form
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text("Route Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.routeName,
                            onValueChange = { viewModel.updateName(it) },
                            label = { Text("Route Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.routeTheme,
                            onValueChange = { viewModel.updateTheme(it) },
                            label = { Text("Theme (e.g. Art, Coffee)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.routeDescription,
                            onValueChange = { viewModel.updateDescription(it) },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // 2. Summary Badge
            item {
                val totalTime = uiState.stops.sumOf { it.estimatedTimeMinutes }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(Modifier.background(Brush.linearGradient(listOf(Color(0xFFEFF6FF), Color(0xFFFAF5FF))))) {
                        Row(
                            Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            BadgeInfo(Icons.Default.Place, "${uiState.stops.size} stops")
                            BadgeInfo(Icons.Default.AccessTime, "${totalTime / 60}h ${totalTime % 60}m total")
                        }
                    }
                }
            }

            // 3. Stops List Header
            item {
                Text("Stops", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // 4. Dynamic Stop Items
            itemsIndexed(uiState.stops) { index, stop ->
                EditableStopItem(
                    index = index,
                    stop = stop,
                    isFirst = index == 0,
                    isLast = index == uiState.stops.lastIndex,
                    isEditing = uiState.editingStopId == stop.id,
                    onToggleEdit = { viewModel.toggleEditStop(if (it) stop.id else null) },
                    onUpdate = { updatedStop -> viewModel.updateStop(stop.id, context) { updatedStop } },
                    onDelete = { viewModel.removeStop(stop.id) },
                    onMoveUp = { viewModel.moveStop(index, -1) },
                    onMoveDown = { viewModel.moveStop(index, 1) }
                )
            }

            // 5. Add Stop Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)) // Dashed border simulated
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.addStop() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Stop", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun EditableStopItem(
    index: Int,
    stop: Stop,
    isFirst: Boolean,
    isLast: Boolean,
    isEditing: Boolean,
    onToggleEdit: (Boolean) -> Unit,
    onUpdate: (Stop) -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header Row (Always visible)
            Row(verticalAlignment = Alignment.Top) {
                // Index Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text((index + 1).toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(12.dp))

                // Title Area (Clickable to expand)
                Column(
                    modifier = Modifier.weight(1f).clickable { onToggleEdit(!isEditing) }
                ) {
                    Text(
                        text = stop.name.ifBlank { "Untitled Stop" },
                        fontWeight = FontWeight.Bold,
                        color = if (stop.name.isBlank()) Color.Gray else Color.Black
                    )
                    if (stop.address.isNotBlank() && !isEditing) {
                        Text(stop.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                // Delete Button
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.Gray)
                }
            }

            // Expanded Edit Form
            if (isEditing) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF3F4F6))
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = stop.name,
                    onValueChange = { onUpdate(stop.copy(name = it)) },
                    label = { Text("Stop Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = stop.address,
                    onValueChange = { onUpdate(stop.copy(address = it)) },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stop.estimatedTimeMinutes.toString(),
                        onValueChange = { onUpdate(stop.copy(estimatedTimeMinutes = it.toIntOrNull() ?: 0)) },
                        label = { Text("Mins") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    // Move Buttons
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = onMoveUp, enabled = !isFirst) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Up")
                        }
                        IconButton(onClick = onMoveDown, enabled = !isLast) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Down")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = stop.description,
                    onValueChange = { onUpdate(stop.copy(description = it)) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(Modifier.height(12.dp))
                TextButton(
                    onClick = { onToggleEdit(false) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Done Editing", color = Color(0xFF9333EA))
                }
            }
        }
    }
}

@Composable
fun BadgeInfo(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}