package com.example.flantr.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
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
import com.example.flantr.ui.navigation.Screen
import com.example.flantr.ui.profile.components.FlantrSwitch
import com.example.flantr.ui.profile.components.ProfileHeader
import com.example.flantr.ui.profile.components.ProfileSection
import com.example.flantr.ui.profile.components.SettingsRow
import com.example.flantr.ui.theme.BackgroundGradient
import com.example.flantr.ui.theme.PurplePrimary

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            item { ProfileHeader(uiState) }

            // 2. Transportation
            item {
                ProfileSection (title = "Transportation", icon = Icons.Default.DirectionsBus) {
                    // Toggle
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

                    // Pace Selector
                    Text("Walking Pace", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaceButton("Slow", "slow", uiState.walkingPace) { viewModel.setWalkingPace(it) }
                        PaceButton("Moderate", "moderate", uiState.walkingPace) { viewModel.setWalkingPace(it) }
                        PaceButton("Fast", "fast", uiState.walkingPace) { viewModel.setWalkingPace(it) }
                    }
                    Text(
                        text = when(uiState.walkingPace) {
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

                    // Distance Slider
                    Text("Max Walking Distance: ${uiState.maxWalkingDistance.toInt()} miles", fontWeight = FontWeight.Medium)
                    Slider(
                        value = uiState.maxWalkingDistance,
                        onValueChange = { viewModel.setMaxDistance(it) },
                        valueRange = 1f..10f,
                        steps = 17, // 0.5 steps
                        colors = SliderDefaults.colors(
                            thumbColor = PurplePrimary,
                            activeTrackColor = PurplePrimary
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1 mile", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("10 miles", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }

            // 3. Accessibility
            item {
                ProfileSection (title = "Accessibility", icon = Icons.Default.AccessibilityNew) {
                    SettingsRow(
                        title = "Accessibility Mode",
                        subtitle = "Prioritize wheelchair-accessible routes",
                        control = {
                            FlantrSwitch(uiState.accessibilityMode) {
                                viewModel.updateBoolean("accessibilityMode", !uiState.accessibilityMode)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsRow(
                        title = "Avoid Stairs",
                        subtitle = "Find alternative routes without stairs",
                        control = {
                            FlantrSwitch(uiState.avoidStairs) {
                                viewModel.updateBoolean("avoidStairs", !uiState.avoidStairs)
                            }
                        }
                    )
                }
            }

            // 4. Route Preferences
            item {
                ProfileSection (title = "Route Preferences", icon = Icons.Default.Map) {
                    SettingsRow(
                        title = "Prefer Scenic Routes",
                        subtitle = "Choose routes through parks and scenic areas",
                        control = {
                            FlantrSwitch(uiState.preferScenic) {
                                viewModel.updateBoolean("preferScenic", !uiState.preferScenic)
                            }
                        }
                    )
                }
            }

            // 5. Notifications
            item {
                ProfileSection (title = "Notifications", icon = Icons.Default.Notifications) {
                    SettingsRow("Route Reminders", "Get notified about upcoming trips") {
                        FlantrSwitch(uiState.notifyRouteReminders) { viewModel.updateBoolean("notifyRouteReminders", !uiState.notifyRouteReminders) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsRow("New Routes", "Discover newly added routes") {
                        FlantrSwitch(uiState.notifyNewRoutes) { viewModel.updateBoolean("notifyNewRoutes", !uiState.notifyNewRoutes) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsRow("Nearby Places", "Suggestions based on your location") {
                        FlantrSwitch(uiState.notifyNearbyPlaces) { viewModel.updateBoolean("notifyNearbyPlaces", !uiState.notifyNearbyPlaces) }
                    }
                }
            }

            // 6. Appearance
            item {
                ProfileSection (title = "Appearance", icon = Icons.Default.LightMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeButton("Light", "light", Icons.Default.LightMode, uiState.appTheme) { viewModel.setTheme(it) }
                        ThemeButton("Dark", "dark", Icons.Default.DarkMode, uiState.appTheme) { viewModel.setTheme(it) }
                        ThemeButton("Auto", "auto", Icons.Default.Bolt, uiState.appTheme) { viewModel.setTheme(it) }
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

@Composable
fun RowScope.PaceButton(label: String, value: String, currentValue: String, onClick: (String) -> Unit) {
    val isSelected = value == currentValue
    val bgColor = if(isSelected) Color(0xFFFAF5FF) else Color.White
    val borderColor = if(isSelected) PurplePrimary else Color(0xFFD1D5DB)
    val textColor = if(isSelected) PurplePrimary else Color.Gray

    Box(
        modifier = Modifier
            .weight(1f)
            .border(if(isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
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
    val bgColor = if(isSelected) Color(0xFFFAF5FF) else Color.White
    val borderColor = if(isSelected) PurplePrimary else Color(0xFFD1D5DB)
    val contentColor = if(isSelected) PurplePrimary else Color.Gray

    Column(
        modifier = Modifier
            .weight(1f)
            .border(if(isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
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
fun SettingsRow(title: String, subtitle: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        control()
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
