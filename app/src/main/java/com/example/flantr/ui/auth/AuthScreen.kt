package com.example.flantr.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flantr.ui.components.StandardTextField
import com.example.flantr.ui.navigation.Screen

@Composable

fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
){

    val uiState by viewModel.uiState.collectAsState()

    //TODO: instead of creating a list of colors here i should be able to get it from
    // the theme file.
    val gradientColors = listOf(
        Color(0xFF2563EB),
        Color(0xFF9333EA),
        Color(0xFFEC4899)
    )

    Box( //background
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors = gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF9333EA)
                                )
                            )
                        )
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = Color(0xFF9333EA),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Flantr",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text("Explore. Discover. Wander.", color = Color.White.copy(alpha = 0.9f))
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        AuthToggleButton(
                            text = "Log In",
                            isSelected = uiState.isLoginMode,
                            onClick = { if (!uiState.isLoginMode) viewModel.onToggleMode() }
                        )
                        AuthToggleButton(
                            text = "Sign Up",
                            isSelected = !uiState.isLoginMode,
                            onClick = { if (uiState.isLoginMode) viewModel.onToggleMode() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState.errorMessage != null) {
                        Text(uiState.errorMessage!!, color = Color.Red, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // NAME FIELD (Only shows if NOT logging in)
                    AnimatedVisibility(visible = !uiState.isLoginMode) {
                        Column {
                            StandardTextField(
                                value = uiState.username,
                                onValueChange = { viewModel.onUsernameChange(it) },
                                label = "Full Name",
                                icon = Icons.Default.Person
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    StandardTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "Email",
                        icon = Icons.Default.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    StandardTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // SUBMIT BUTTON
                    Button(
                        onClick = {
                            viewModel.onSubmit(
                                onSuccess = { navController.navigate(Screen.Home.route) }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (uiState.isLoginMode) "Log In" else "Create Account")
                        }
                    }

                    // Footer Text
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if(uiState.isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Log In",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { viewModel.onToggleMode() }.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.AuthToggleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.White else Color.Transparent
    val textColor = if (isSelected) Color(0xFF9333EA) else Color.Gray
    val shadow = if (isSelected) 2.dp else 0.dp

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = shadow,
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
            Text(text, color = textColor, fontWeight = FontWeight.SemiBold)
        }
    }
}