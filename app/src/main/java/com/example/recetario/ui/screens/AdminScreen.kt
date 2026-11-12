package com.example.recetario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recetario.data.entity.User
import com.example.recetario.data.entity.UserActivity
import com.example.recetario.ui.viewmodel.UserViewModel
import com.example.recetario.ui.viewmodel.RecipeViewModel
import com.example.recetario.ui.utils.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel,
    onBackClick: () -> Unit
) {
    val allUsers by userViewModel.allUsers.collectAsState()
    val deletedUsers by userViewModel.deletedUsers.collectAsState()
    val userActivities by userViewModel.userActivities.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showAllActivities by remember { mutableStateOf(false) }
    var showUserManagement by remember { mutableStateOf(false) }
    var showDeletedUsers by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
        userViewModel.loadDeletedUsers()
        userViewModel.loadAllActivities()
    }

    LaunchedEffect(selectedUser) {
        if (selectedUser != null) {
            userViewModel.loadUserActivities(selectedUser!!.id)
        }
    }

    // Limpiar error después de 5 segundos
    LaunchedEffect(error) {
        if (error != null) {
            kotlinx.coroutines.delay(5000)
            userViewModel.clearError()
        }
    }

    // Manejar navegación a sub-pantallas
    if (showUserManagement) {
        AdminUserManagementScreen(
            userViewModel = userViewModel,
            onBackClick = {
                showUserManagement = false
                selectedUser = null
            }
        )
        return
    }

    if (showAllActivities) {
        AdminAllActivitiesScreen(
            userViewModel = userViewModel,
            onBackClick = {
                showAllActivities = false
            }
        )
        return
    }

    if (showDeletedUsers) {
        AdminDeletedUsersScreen(
            userViewModel = userViewModel,
            onBackClick = {
                showDeletedUsers = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel de Administración",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE5E1DC))
                .padding(paddingValues)
        ) {
            // Información del administrador
            currentUser?.let { admin ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Administrador: ${admin.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = admin.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Botones de acción rápida SIEMPRE visibles
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Acciones de Administración",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B655E),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    AdminActionCard(
                        title = "Gestión de Usuarios",
                        description = "Ver todas las cuentas y sus actividades",
                        icon = Icons.Default.People,
                        onClick = {
                            showUserManagement = true
                        }
                    )
                }

                item {
                    AdminActionCard(
                        title = "Todas las Actividades",
                        description = "Ver el historial completo de actividades",
                        icon = Icons.Default.History,
                        onClick = {
                            showAllActivities = true
                        }
                    )
                }

                item {
                    AdminActionCard(
                        title = "Usuarios Eliminados",
                        description = "Ver y gestionar cuentas eliminadas",
                        icon = Icons.Default.Delete,
                        onClick = {
                            showDeletedUsers = true
                        }
                    )
                }

                item {
                    AdminActionCard(
                        title = "Estadísticas",
                        description = "Ver estadísticas de uso de la aplicación",
                        icon = Icons.Default.Analytics,
                        onClick = {
                            // TODO: Implementar estadísticas
                        }
                    )
                }

                item {
                    AdminActionCard(
                        title = "Configuración",
                        description = "Configurar opciones de administración",
                        icon = Icons.Default.Settings,
                        onClick = {
                            // TODO: Implementar configuración
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAllActivitiesScreen(
    userViewModel: UserViewModel,
    onBackClick: () -> Unit
) {
    val userActivities by userViewModel.userActivities.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadAllActivities()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Todas las Actividades",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE5E1DC))
                .padding(paddingValues)
        ) {
            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (userActivities.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay actividades registradas", color = Color.Gray)
                }
            } else {
                GroupedActivitiesList(
                    activities = userActivities
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupedActivitiesList(
    activities: List<UserActivity>,
    modifier: Modifier = Modifier,
    onDeleteSessionActivities: () -> Unit = {},
    onDeleteRecipeActivities: () -> Unit = {}
) {
    // Estados para controlar qué secciones están expandidas
    var sessionExpanded by remember { mutableStateOf(true) }
    var recipeExpanded by remember { mutableStateOf(true) }
    
    // Estados para los diálogos de confirmación
    var showDeleteSessionDialog by remember { mutableStateOf(false) }
    var showDeleteRecipeDialog by remember { mutableStateOf(false) }
    
    // Filtrar actividades por sección
    val sessionActivities = activities.filter { 
        it.activityType in listOf("LOGIN", "LOGOUT") 
    }
    val recipeActivities = activities.filter { 
        it.activityType in listOf("ADD_RECIPE", "EDIT_RECIPE", "DELETE_RECIPE") 
    }
    
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sección de Inicio de Sesión
        if (sessionActivities.isNotEmpty()) {
            item {
                // Encabezado desplegable de la sección
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Área clickeable para expandir/contraer
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { sessionExpanded = !sessionExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inicio de Sesión",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B655E)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "(${sessionActivities.size})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8B857B)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (sessionExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (sessionExpanded) "Contraer" else "Expandir",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Botón de eliminar
                        IconButton(
                            onClick = { showDeleteSessionDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar actividades de sesión",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Actividades de sesión (solo si está expandida)
            if (sessionExpanded) {
                items(sessionActivities) { activity ->
                    ActivityCard(activity = activity)
                }
            }
        }
        
        // Separador entre secciones
        if (sessionActivities.isNotEmpty() && recipeActivities.isNotEmpty()) {
            item {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
        }
        
        // Sección de Recetas Añadidas
        if (recipeActivities.isNotEmpty()) {
            item {
                // Encabezado desplegable de la sección
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Área clickeable para expandir/contraer
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { recipeExpanded = !recipeExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recetas Añadidas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B655E)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "(${recipeActivities.size})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8B857B)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (recipeExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (recipeExpanded) "Contraer" else "Expandir",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Botón de eliminar
                        IconButton(
                            onClick = { showDeleteRecipeDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar actividades de recetas",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Actividades de recetas (solo si está expandida)
            if (recipeExpanded) {
                items(recipeActivities) { activity ->
                    ActivityCard(activity = activity)
                }
            }
        }
        
        // Si no hay actividades en ninguna sección
        if (sessionActivities.isEmpty() && recipeActivities.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay actividades registradas", color = Color.Gray)
                }
            }
        }
    }
    
    // Diálogo de confirmación para eliminar actividades de sesión
    if (showDeleteSessionDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSessionDialog = false },
            title = {
                Text("Eliminar Actividades de Sesión")
            },
            text = {
                Text("¿Estás seguro de que quieres eliminar todas las actividades de inicio de sesión? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteSessionActivities()
                        showDeleteSessionDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteSessionDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para eliminar actividades de recetas
    if (showDeleteRecipeDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteRecipeDialog = false },
            title = {
                Text("Eliminar Actividades de Recetas")
            },
            text = {
                Text("¿Estás seguro de que quieres eliminar todas las actividades de recetas? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteRecipeActivities()
                        showDeleteRecipeDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteRecipeDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B655E)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8B857B)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF2E7D32)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementScreen(
    userViewModel: UserViewModel,
    onBackClick: () -> Unit
) {
    val allUsers by userViewModel.allUsers.collectAsState()
    val deletedUsers by userViewModel.deletedUsers.collectAsState()
    val userActivities by userViewModel.userActivities.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showDeleteActivitiesDialog by remember { mutableStateOf(false) }
    var showDeletedUsers by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
        userViewModel.loadDeletedUsers()
    }

    LaunchedEffect(selectedUser) {
        if (selectedUser != null) {
            userViewModel.loadUserActivities(selectedUser!!.id)
        }
    }

    // Limpiar error después de 5 segundos
    LaunchedEffect(error) {
        if (error != null) {
            kotlinx.coroutines.delay(5000)
            userViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedUser != null) "Actividades de ${selectedUser!!.name}" else "Gestión de Usuarios",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedUser != null) {
                            selectedUser = null
                            userViewModel.clearUserActivities()
                            userViewModel.clearLoading()
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE5E1DC))
                .padding(paddingValues)
        ) {
            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (selectedUser == null && allUsers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay usuarios registrados", color = Color.Gray)
                }
            } else if (selectedUser != null && userActivities.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay actividades para este usuario", color = Color.Gray)
                }
            } else {
                if (selectedUser == null) {
                    // Lista de usuarios
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allUsers) { user ->
                            AdminUserCard(
                                user = user,
                                onClick = {
                                    selectedUser = user
                                },
                                onDeleteUser = { user ->
                                    userViewModel.deleteUser(user)
                                }
                            )
                        }
                    }
                } else if (userActivities.isNotEmpty()) {
                    // Actividades del usuario seleccionado
                    Column {
                        // Botón para eliminar todas las actividades
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Eliminar todas las actividades de ${selectedUser!!.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(
                                    onClick = {
                                        showDeleteActivitiesDialog = true
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                        
                        GroupedActivitiesList(
                            activities = userActivities,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación para eliminar todas las actividades
    if (showDeleteActivitiesDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDeleteActivitiesDialog = false },
            title = {
                Text("Eliminar Todas las Actividades")
            },
            text = {
                Text("¿Estás seguro de que quieres eliminar todas las actividades de ${selectedUser!!.name} (${selectedUser!!.email})? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedUser?.let { user ->
                            userViewModel.deleteAllUserActivities(user)
                        }
                        showDeleteActivitiesDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteActivitiesDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserCard(
    user: User,
    onClick: () -> Unit,
    onDeleteUser: (User) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (user.isAdmin) Color(0xFF2E7D32) else Color(0xFF6B655E)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B655E)
                    )
                    if (user.isAdmin) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ADMIN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFF2E7D32), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8B857B)
                )
                Text(
                    text = "Creado: ${formatDate(user.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8B857B)
                )
            }
            
            // Botón para ver actividades
            IconButton(onClick = onClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Ver actividades",
                    tint = Color(0xFF2E7D32)
                )
            }
            
            // Botón de eliminar (solo para usuarios no admin)
            if (!user.isAdmin) {
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar usuario",
                        tint = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
    
    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Eliminar Usuario")
            },
            text = {
                Text("¿Estás seguro de que quieres eliminar la cuenta de ${user.name} (${user.email})? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteUser(user)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDeletedUsersScreen(
    userViewModel: UserViewModel,
    onBackClick: () -> Unit
) {
    val deletedUsers by userViewModel.deletedUsers.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadDeletedUsers()
    }

    // Limpiar error después de 5 segundos
    LaunchedEffect(error) {
        if (error != null) {
            kotlinx.coroutines.delay(5000)
            userViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Usuarios Eliminados",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE5E1DC))
                .padding(paddingValues)
        ) {
            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (deletedUsers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay usuarios eliminados", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(deletedUsers) { user ->
                        AdminDeletedUserCard(
                            user = user,
                            onRestoreUser = { user ->
                                userViewModel.restoreUser(user)
                            },
                            onPermanentlyDeleteUser = { user ->
                                userViewModel.permanentlyDeleteUser(user)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDeletedUserCard(
    user: User,
    onRestoreUser: (User) -> Unit,
    onPermanentlyDeleteUser: (User) -> Unit
) {
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (user.isAdmin) Color(0xFF2E7D32) else Color(0xFF6B655E)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B655E)
                    )
                    if (user.isAdmin) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ADMIN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFF2E7D32), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8B857B)
                )
                Text(
                    text = "Creado: ${formatDate(user.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8B857B)
                )
            }
            
            // Botón para restaurar
            IconButton(
                onClick = { showRestoreDialog = true }
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Restaurar usuario",
                    tint = Color(0xFF2E7D32)
                )
            }
            
            // Botón de eliminar (solo para usuarios no admin)
            if (!user.isAdmin) {
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar usuario",
                        tint = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
    
    // Diálogo de confirmación para restaurar
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = {
                Text("Restaurar Usuario")
            },
            text = {
                Text("¿Estás seguro de que quieres restaurar la cuenta de ${user.name} (${user.email})? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRestoreUser(user)
                        showRestoreDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF2E7D32)
                    )
                ) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRestoreDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Eliminar Usuario")
            },
            text = {
                Text("¿Estás seguro de que quieres eliminar la cuenta de ${user.name} (${user.email})? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPermanentlyDeleteUser(user)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

 