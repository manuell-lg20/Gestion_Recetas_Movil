package com.example.recetario.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*
import com.example.recetario.data.entity.UserActivity

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}

fun getActivityTypeText(activityType: String): String {
    return when (activityType) {
        "LOGIN" -> "Inicio de Sesión"
        "LOGOUT" -> "Cierre de Sesión"
        "ADD_RECIPE" -> "Añadir Receta"
        "EDIT_RECIPE" -> "Editar Receta"
        "DELETE_RECIPE" -> "Eliminar Receta"
        "UPDATE_PROFILE" -> "Actualizar Perfil"
        else -> activityType
    }
}

fun getActivityIcon(activityType: String) = when (activityType) {
    "LOGIN" -> Icons.AutoMirrored.Filled.Login
    "LOGOUT" -> Icons.AutoMirrored.Filled.Logout
    "ADD_RECIPE" -> Icons.Default.Add
    "EDIT_RECIPE" -> Icons.Default.Edit
    "DELETE_RECIPE" -> Icons.Default.Delete
    "UPDATE_PROFILE" -> Icons.Default.Person
    else -> Icons.Default.Info
}

fun getActivityColor(activityType: String): Color = when (activityType) {
    "LOGIN" -> Color(0xFF2196F3)
    "LOGOUT" -> Color(0xFF9C27B0)
    "ADD_RECIPE" -> Color(0xFF4CAF50)
    "EDIT_RECIPE" -> Color(0xFF2196F3)
    "DELETE_RECIPE" -> Color(0xFFF44336)
    "UPDATE_PROFILE" -> Color(0xFF2196F3)
    else -> Color(0xFF757575)
}

// Función para agrupar actividades por tipo
fun groupActivitiesByType(activities: List<UserActivity>): Map<String, List<UserActivity>> {
    return activities.groupBy { activity -> activity.activityType }
}

// Función para obtener el título de la sección
fun getSectionTitle(activityType: String): String {
    return when (activityType) {
        "LOGIN", "LOGOUT" -> "Sesiones"
        "ADD_RECIPE", "EDIT_RECIPE", "DELETE_RECIPE" -> "Gestión de Recetas"
        "UPDATE_PROFILE" -> "Perfil"
        "DELETE_USER", "DELETE_USER_ACTIVITIES" -> "Administración"
        else -> "Otras Actividades"
    }
}

// Función para obtener el ícono de la sección
fun getSectionIcon(activityType: String) = when (activityType) {
    "LOGIN", "LOGOUT" -> Icons.AutoMirrored.Filled.Login
    "ADD_RECIPE", "EDIT_RECIPE", "DELETE_RECIPE" -> Icons.Default.Restaurant
    "UPDATE_PROFILE" -> Icons.Default.Person
    "DELETE_USER", "DELETE_USER_ACTIVITIES" -> Icons.Default.AdminPanelSettings
    else -> Icons.Default.Info
}

// Función para obtener el color de la sección
fun getSectionColor(activityType: String): Color = when (activityType) {
    "LOGIN", "LOGOUT" -> Color(0xFF2196F3)
    "ADD_RECIPE", "EDIT_RECIPE", "DELETE_RECIPE" -> Color(0xFF4CAF50)
    "UPDATE_PROFILE" -> Color(0xFF2196F3)
    "DELETE_USER", "DELETE_USER_ACTIVITIES" -> Color(0xFFD32F2F)
    else -> Color(0xFF757575)
} 