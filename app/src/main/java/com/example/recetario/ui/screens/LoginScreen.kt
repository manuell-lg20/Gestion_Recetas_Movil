package com.example.recetario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recetario.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showAdminLogin by remember { mutableStateOf(false) }
    val currentUser by userViewModel.currentUser.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E1DC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono chef
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text("\uD83C\uDF73", fontSize = 40.sp) // Emoji chef
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Iniciar Sesión",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B655E),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(50),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFFF8356)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(50),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFFF8356)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
            if (error != null) {
                Text(
                    text = error!!,
                    color = Color(0xFFFF8356),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        userViewModel.loginWithPassword(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8356)
                ),
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
            ) {
                Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold, color = Color.White)
            }
            if (!isLoading && email.isBlank() && password.isBlank()) {
                Text("Ingresa tus credenciales para iniciar sesión", color = Color.Gray, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B857B)
                )
            ) {
                Text("Regístrate", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¿Olvidaste la contraseña?",
                color = Color(0xFF6B655E),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    showAdminLogin = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text("Iniciar como Administrador", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { /* TODO: Social login */ }) {
                    Icon(
                        painter = painterResource(id = com.example.recetario.R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        tint = Color(0xFF444444),
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = { /* TODO: Social login */ }) {
                    Icon(
                        painter = painterResource(id = com.example.recetario.R.drawable.ic_twitter),
                        contentDescription = "Twitter",
                        tint = Color(0xFF444444),
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = { /* TODO: Social login */ }) {
                    Icon(
                        painter = painterResource(id = com.example.recetario.R.drawable.ic_google),
                        contentDescription = "Google",
                        tint = Color(0xFF444444),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
    
    // Diálogo de login de administrador
    if (showAdminLogin) {
        AdminLoginScreen(
            onDismiss = { showAdminLogin = false },
            onLoginSuccess = { showAdminLogin = false },
            userViewModel = userViewModel
        )
    }
}

@Composable
fun QuickLoginButton(
    email: String,
    password: String,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onLogin,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF43A047)
        )
    ) {
        Text(
            text = email.split("@")[0],
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun AdminLoginScreen(
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit,
    userViewModel: UserViewModel
) {
    var adminEmail by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null && currentUser!!.isAdmin) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E1DC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de administrador
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text("\uD83D\uDC51", fontSize = 40.sp) // Emoji corona
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Acceso de Administrador",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B655E),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            OutlinedTextField(
                value = adminEmail,
                onValueChange = { adminEmail = it },
                label = { Text("Email de Administrador") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(50),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFFF8356)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = adminPassword,
                onValueChange = { adminPassword = it },
                label = { Text("Contraseña de Administrador") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(50),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFFF8356)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
            if (error != null) {
                Text(
                    text = error!!,
                    color = Color(0xFFFF8356),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (adminEmail.isNotBlank() && adminPassword.isNotBlank()) {
                        userViewModel.createAdminUser()
                        userViewModel.loginAsAdmin(adminEmail, adminPassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                enabled = adminEmail.isNotBlank() && adminPassword.isNotBlank() && !isLoading
            ) {
                Text("INICIAR SESIÓN ADMIN", fontWeight = FontWeight.Bold, color = Color.White)
            }
            if (!isLoading && adminEmail.isBlank() && adminPassword.isBlank()) {
                Text("Ingresa las credenciales de administrador", color = Color.Gray, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B857B)
                )
            ) {
                Text("Volver al Login", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Panel de Administración",
                color = Color(0xFF6B655E),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { /* TODO: Social login */ }) {
                    Icon(
                        painter = painterResource(id = com.example.recetario.R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        tint = Color(0xFF444444),
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = { /* TODO: Social login */ }) {
                    Icon(
                        painter = painterResource(id = com.example.recetario.R.drawable.ic_twitter),
                        contentDescription = "Twitter",
                        tint = Color(0xFF444444),
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = { /* TODO: Social login */ }) {
                    Icon(
                        painter = painterResource(id = com.example.recetario.R.drawable.ic_google),
                        contentDescription = "Google",
                        tint = Color(0xFF444444),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SocialEmojiButton(emoji: String, color: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp,
            color = Color.White
        )
    }
} 