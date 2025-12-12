package com.example.flantr.ui.profile
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flantr.ui.navigation.Screen


private val PurplePrimary = Color(0xFF9333EA)
private val BluePrimary = Color(0xFF3B82F6)
private val GradientPrimary = Brush.linearGradient(listOf(BluePrimary, PurplePrimary))
private val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFEFF6FF), Color.White, Color(0xFFFAF5FF))
)

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
            //Profile Card
            item {
                ProfileHeaderCard(uiState)
            }
            //TODO: add transportation options (see viewmodel)
            //Accessibility
            item {
                SectionCard(title = "Accessibility", icon = Icons.Default.AccessibilityNew) {
                    SettingsRow(
                        title = "Accessibility Mode",
                        subtitle = "Prioritize wheelchair-accessible routes",
                        control = { FlantrSwitch(uiState.accessibilityMode) { viewModel.toggleAccessibilityMode() } }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsRow(
                        title = "Avoid Stairs",
                        subtitle = "Find alternative routes without stairs",
                        control = { FlantrSwitch(uiState.avoidStairs) { viewModel.toggleAvoidStairs() } }
                    )
                }
            }

            //Logout
            item {
                Button(
                    onClick = {
                        navController.navigate(Screen.Authentication.route) {
                            popUpTo(0)
                        }
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
fun ProfileHeaderCard(state: ProfileUiState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GradientPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(state.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(state.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text("Member since ${state.memberSince}", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                }
                Icon(Icons.Default.Settings, contentDescription = null, tint = PurplePrimary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatBox(count = state.tripCount.toString(), label = "Trips", color = Color(0xFFEFF6FF), modifier = Modifier.weight(1f))
                StatBox(count = state.placesVisited.toString(), label = "Places", color = Color(0xFFFAF5FF), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatBox(count: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(count, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                Icon(icon, contentDescription = null, tint = PurplePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            content()
        }
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
fun FlantrSwitch(checked: Boolean, onCheckedChange: () -> Unit) {
    val offsetX by animateFloatAsState(targetValue = if (checked) 24f else 0f, label = "switch")

    Box(
        modifier = Modifier
            .width(52.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(100))
            .background(if (checked) GradientPrimary else Brush.linearGradient(listOf(Color(0xFFD1D5DB), Color(0xFFD1D5DB))))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCheckedChange() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = offsetX.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }