package com.example.flantr.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flantr.data.model.Collection
import com.example.flantr.data.model.Route
import com.example.flantr.ui.navigation.Screen
import com.example.flantr.ui.profile.components.FlantrSwitch
import com.example.flantr.ui.profile.components.ProfileHeader
import com.example.flantr.ui.profile.components.ProfileSection
import com.example.flantr.ui.profile.components.SettingsRow
import com.example.flantr.ui.theme.BackgroundGradient
import com.example.flantr.ui.theme.BluePrimary
import com.example.flantr.ui.theme.PurplePrimary
import com.example.flantr.ui.theme.PrimaryGradient

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit){
        viewModel.refreshData()
    }
    val uiState by viewModel.uiState.collectAsState()

    // --- Dialogs ---
    if (uiState.showEditProfileDialog) {
        EditProfileDialog(
            currentName = uiState.name,
            onDismiss = { viewModel.toggleEditProfileDialog(false) },
            onSave = { newName -> viewModel.updateName(newName) }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Account", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Manage your profile and preferences", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
            // 1. Profile Header
            item {
                Column {
                    ProfileHeader(uiState)
                    Spacer(Modifier.height(12.dp))
                    // Edit Profile Button
                    OutlinedButton(
                        onClick = { viewModel.toggleEditProfileDialog(true) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Profile")
                    }
                }
            }

            // 1.5 Collections
            item {
                CollectionsSection(
                    collections = uiState.collections,
                    onCreateClick = { viewModel.toggleCreateDialog(true) },
                    onEditClick = { /*TODO: Add logic here*/ },
                    onDeleteClick = { viewModel.deleteCollection(it) }
                )
            }

            // 1.9 Created Routes
            item {
                CreatedRoutesSection(
                    routes = uiState.createdRoutes,
                    onRouteClick = { routeId -> navController.navigate("active_route/$routeId") }
                )
            }

            // 2. Transportation
            item {
                ProfileSection(title = "Transportation", icon = Icons.Default.DirectionsBus) {
                    SettingsRow(
                        title = "Include Public Transport",
                        subtitle = "Show bus, train, and metro options",
                        control = {
                            FlantrSwitch(uiState.includePublicTransport) {
                                viewModel.updateBoolean("includePublicTransport", !uiState.includePublicTransport)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Walking Pace", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaceButton("Slow", "slow", uiState.walkingPace) { viewModel.setWalkingPace(it) }
                        PaceButton("Moderate", "moderate", uiState.walkingPace) { viewModel.setWalkingPace(it) }
                        PaceButton("Fast", "fast", uiState.walkingPace) { viewModel.setWalkingPace(it) }
                    }
                    Text(
                        text = when (uiState.walkingPace) {
                            "slow" -> "Estimated 2 mph / 3 km/h"
                            "moderate" -> "Estimated 3 mph / 5 km/h"
                            "fast" -> "Estimated 4 mph / 6.5 km/h"
                            else -> ""
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1 mile", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("10 miles", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }

            // 7. Other Links
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        SettingsLink("Privacy & Security", Icons.Default.Security)
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        SettingsLink("App Settings", Icons.Default.Settings)
                    }
                }
            }

            // 8. Logout
            item {
                Button(
                    onClick = {
                        navController.navigate(Screen.Authentication.route) { popUpTo(0) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2), contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color(0xFFFECACA), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// --- EDIT PROFILE DIALOG ---

@Composable
fun EditProfileDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name) }, enabled = name.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

// --- HELPER COMPONENTS (Unchanged) ---
@Composable
fun RowScope.PaceButton(label: String, value: String, currentValue: String, onClick: (String) -> Unit) {
    val isSelected = value == currentValue
    val bgColor = if (isSelected) Color(0xFFFAF5FF) else Color.White
    val borderColor = if (isSelected) PurplePrimary else Color(0xFFD1D5DB)
    val textColor = if (isSelected) PurplePrimary else Color.Gray

    Box(
        modifier = Modifier
            .weight(1f)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick(value) }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RowScope.ThemeButton(label: String, value: String, icon: ImageVector, currentValue: String, onClick: (String) -> Unit) {
    val isSelected = value == currentValue
    val bgColor = if (isSelected) Color(0xFFFAF5FF) else Color.White
    val borderColor = if (isSelected) PurplePrimary else Color(0xFFD1D5DB)
    val contentColor = if (isSelected) PurplePrimary else Color.Gray

    Column(
        modifier = Modifier
            .weight(1f)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick(value) }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Text(label, color = contentColor, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SettingsLink(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun CollectionItem(
    collection: Collection,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val (bg, text, border) = when (collection.color) {
        "purple" -> Triple(Color(0xFFFAF5FF), Color(0xFF7E22CE), Color(0xFFE9D5FF))
        "green" -> Triple(Color(0xFFF0FDF4), Color(0xFF15803D), Color(0xFFBBF7D0))
        "pink" -> Triple(Color(0xFFFDF2F8), Color(0xFFBE185D), Color(0xFFFBCFE8))
        "blue" -> Triple(Color(0xFFEFF6FF), Color(0xFF1D4ED8), Color(0xFFBFDBFE))
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), Color(0xFFE5E7EB))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(collection.title, fontWeight = FontWeight.Bold, color = Color.Black)
            if (collection.description.isNotEmpty()) {
                Text(collection.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "${collection.routeIds.size} ${if (collection.routeIds.size == 1) "route" else "routes"}",
                style = MaterialTheme.typography.labelSmall,
                color = text
            )
        }

        Row {
            IconButton(onClick = { onEditClick(collection) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = { onDeleteClick(collection.id) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun EmptyCollectionsState(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text("No collections yet", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            Text("Create collections to organize your routes", color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(16.dp))
            Text(
                "Create your first collection",
                color = PurplePrimary,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.clickable { onCreateClick() }
            )
        }
    }
}

@Composable
fun CollectionsSection(
    collections: List<Collection>,
    onCreateClick: () -> Unit,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = PurplePrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("My Collections", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGradient)
                        .clickable { onCreateClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("New", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            if (collections.isEmpty()) {
                EmptyCollectionsState(onCreateClick)
            } else {
                collections.forEach { col ->
                    CollectionItem(col, onEditClick, onDeleteClick)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun CreatedRoutesSection(routes: List<Route>, onRouteClick: (String) -> Unit) {
    Column {
        Text(
            "Routes You Created",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        if (routes.isEmpty()) {
            Text("You haven't created any routes yet.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        } else {
            routes.forEach { route ->
                CreatedRouteItem(route, onClick = { onRouteClick(route.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CreatedRouteItem(route: Route, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFEFF6FF)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Map, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(route.name, fontWeight = FontWeight.Bold)
                Text("${route.stops.size} stops • ${route.distance}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}