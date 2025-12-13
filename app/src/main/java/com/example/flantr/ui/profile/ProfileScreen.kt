package com.example.flantr.ui.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                ProfileHeader(uiState)
            }
            //TODO: add transportation options (see viewmodel)

            //Accessibility
            item {
                ProfileSection(title = "Accessibility", icon = Icons.Default.AccessibilityNew) {
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

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }