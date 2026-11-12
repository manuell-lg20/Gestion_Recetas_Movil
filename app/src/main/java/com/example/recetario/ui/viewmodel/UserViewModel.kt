package com.example.recetario.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recetario.data.entity.User
import com.example.recetario.data.entity.UserActivity
import com.example.recetario.data.repository.UserRepository
import com.example.recetario.data.repository.UserActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userActivityRepository: UserActivityRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _registrationSuccess = MutableStateFlow<String?>(null)
    val registrationSuccess: StateFlow<String?> = _registrationSuccess.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _deletedUsers = MutableStateFlow<List<User>>(emptyList())
    val deletedUsers: StateFlow<List<User>> = _deletedUsers.asStateFlow()

    private val _userActivities = MutableStateFlow<List<UserActivity>>(emptyList())
    val userActivities: StateFlow<List<UserActivity>> = _userActivities.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    fun loginUser(name: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = userRepository.createUserIfNotExists(name, email)
                _currentUser.value = user
                // Registrar actividad de login
                userActivityRepository.logActivity(
                    userId = user.id,
                    activityType = "LOGIN",
                    description = "Usuario inició sesión"
                )
            } catch (e: Exception) {
                _error.value = "Error al iniciar sesión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _registrationSuccess.value = null
            try {
                // Validar que el email no exista
                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    _error.value = "El email ya está registrado. Intenta con otro email o inicia sesión."
                    return@launch
                }
                
                // Validar contraseña
                if (password.length < 6) {
                    _error.value = "La contraseña debe tener al menos 6 caracteres"
                    return@launch
                }
                
                // Crear nuevo usuario con contraseña
                val user = userRepository.createUserWithPassword(name, email, password)
                // No establecer el usuario como logueado automáticamente
                // _currentUser.value = user
                _registrationSuccess.value = "¡Usuario registrado exitosamente! Ahora puedes iniciar sesión."
            } catch (e: Exception) {
                _error.value = "Error al registrar usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithPassword(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = userRepository.authenticateUser(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _isAdmin.value = user.isAdmin
                    // Registrar actividad de login
                    userActivityRepository.logActivity(
                        userId = user.id,
                        activityType = "LOGIN",
                        description = "Usuario inició sesión con contraseña"
                    )
                } else {
                    _error.value = "Email o contraseña incorrectos"
                }
            } catch (e: Exception) {
                _error.value = "Error al iniciar sesión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginAsAdmin(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = userRepository.authenticateAdmin(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _isAdmin.value = true
                    // Registrar actividad de login como administrador
                    userActivityRepository.logActivity(
                        userId = user.id,
                        activityType = "LOGIN",
                        description = "Administrador inició sesión"
                    )
                } else {
                    _error.value = "Credenciales de administrador incorrectas"
                }
            } catch (e: Exception) {
                _error.value = "Error al iniciar sesión como administrador: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createAdminUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.createAdminUserIfNotExists()
            } catch (e: Exception) {
                _error.value = "Error al crear usuario administrador: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserById(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.getUserById(userId)
                _currentUser.value = user
            } catch (e: Exception) {
                _error.value = "Error al cargar el usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.updateUser(user)
                _currentUser.value = user
            } catch (e: Exception) {
                _error.value = "Error al actualizar el usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        val currentUser = _currentUser.value
        if (currentUser != null) {
            viewModelScope.launch {
                userActivityRepository.logActivity(
                    userId = currentUser.id,
                    activityType = "LOGOUT",
                    description = if (currentUser.isAdmin) "Administrador cerró sesión" else "Usuario cerró sesión"
                )
            }
        }
        _currentUser.value = null
        _isAdmin.value = false
        _error.value = null
        _registrationSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun clearRegistrationSuccess() {
        _registrationSuccess.value = null
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = userRepository.getAllUsers()
                _allUsers.value = users
            } catch (e: Exception) {
                _error.value = "Error al cargar usuarios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDeletedUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val deletedUsers = userRepository.getDeletedUsers()
                _deletedUsers.value = deletedUsers
            } catch (e: Exception) {
                _error.value = "Error al cargar usuarios eliminados: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserActivities(userId: Long? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val targetUserId = userId ?: _currentUser.value?.id
                if (targetUserId != null) {
                    userActivityRepository.getUserActivities(targetUserId).collect { activities ->
                        _userActivities.value = activities
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar actividades: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userActivityRepository.getAllActivities().collect { activities ->
                    _userActivities.value = activities
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar todas las actividades: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logUserActivity(
        activityType: String,
        description: String,
        recipeId: Long? = null,
        oldValue: String? = null,
        newValue: String? = null
    ) {
        val currentUser = _currentUser.value
        if (currentUser != null) {
            viewModelScope.launch {
                userActivityRepository.logActivity(
                    userId = currentUser.id,
                    activityType = activityType,
                    description = description,
                    recipeId = recipeId,
                    oldValue = oldValue,
                    newValue = newValue
                )
            }
        }
    }

    fun clearUserActivities() {
        _userActivities.value = emptyList()
    }

    fun clearLoading() {
        _isLoading.value = false
    }

    fun deleteUser(user: User) {
        val currentUser = _currentUser.value
        if (currentUser != null && currentUser.isAdmin) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Registrar actividad de eliminación antes de eliminar
                    userActivityRepository.logActivity(
                        userId = currentUser.id,
                        activityType = "DELETE_USER",
                        description = "Administrador eliminó la cuenta de ${user.name} (${user.email})",
                        oldValue = "Usuario: ${user.name}, Email: ${user.email}"
                    )
                    
                    // Soft delete del usuario
                    userRepository.softDeleteUser(user.id)
                    
                    // Recargar la lista de usuarios
                    loadAllUsers()
                    loadDeletedUsers()
                } catch (e: Exception) {
                    _error.value = "Error al eliminar usuario: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _error.value = "No tienes permisos para eliminar usuarios"
        }
    }

    fun restoreUser(user: User) {
        val currentUser = _currentUser.value
        if (currentUser != null && currentUser.isAdmin) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Registrar actividad de restauración
                    userActivityRepository.logActivity(
                        userId = currentUser.id,
                        activityType = "RESTORE_USER",
                        description = "Administrador restauró la cuenta de ${user.name} (${user.email})",
                        oldValue = "Usuario: ${user.name}, Email: ${user.email}"
                    )
                    
                    // Restaurar el usuario
                    userRepository.restoreUser(user.id)
                    
                    // Recargar las listas de usuarios
                    loadAllUsers()
                    loadDeletedUsers()
                } catch (e: Exception) {
                    _error.value = "Error al restaurar usuario: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _error.value = "No tienes permisos para restaurar usuarios"
        }
    }

    fun permanentlyDeleteUser(user: User) {
        val currentUser = _currentUser.value
        if (currentUser != null && currentUser.isAdmin) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Registrar actividad de eliminación permanente
                    userActivityRepository.logActivity(
                        userId = currentUser.id,
                        activityType = "PERMANENT_DELETE_USER",
                        description = "Administrador eliminó permanentemente la cuenta de ${user.name} (${user.email})",
                        oldValue = "Usuario: ${user.name}, Email: ${user.email}"
                    )
                    
                    // Eliminación permanente del usuario
                    userRepository.deleteUser(user)
                    
                    // Recargar la lista de usuarios eliminados
                    loadDeletedUsers()
                } catch (e: Exception) {
                    _error.value = "Error al eliminar permanentemente usuario: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _error.value = "No tienes permisos para eliminar usuarios"
        }
    }

    fun deleteAllUserActivities(user: User) {
        val currentUser = _currentUser.value
        if (currentUser != null && currentUser.isAdmin) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    userActivityRepository.deleteUserActivities(user.id)
                    // Registrar actividad de auditoría
                    userActivityRepository.logActivity(
                        userId = currentUser.id,
                        activityType = "DELETE_USER_ACTIVITIES",
                        description = "Administrador eliminó todas las actividades de ${user.name} (${user.email})",
                        oldValue = "userId: ${user.id}"
                    )
                    // Limpiar lista en UI
                    if (_userActivities.value.isNotEmpty()) {
                        _userActivities.value = emptyList()
                    }
                } catch (e: Exception) {
                    _error.value = "Error al eliminar actividades: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _error.value = "No tienes permisos para eliminar actividades"
        }
    }

    fun deleteUserActivitiesByType(user: User, activityTypes: List<String>) {
        val currentUser = _currentUser.value
        if (currentUser != null && currentUser.isAdmin) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Eliminar actividades por tipo
                    userActivityRepository.deleteUserActivitiesByType(user.id, activityTypes)
                    
                    // Registrar actividad de auditoría
                    val typeNames = when {
                        activityTypes.contains("LOGIN") && activityTypes.contains("LOGOUT") -> "sesión"
                        activityTypes.contains("ADD_RECIPE") || activityTypes.contains("EDIT_RECIPE") || activityTypes.contains("DELETE_RECIPE") -> "recetas"
                        else -> "actividades específicas"
                    }
                    
                    userActivityRepository.logActivity(
                        userId = currentUser.id,
                        activityType = "DELETE_USER_ACTIVITIES_BY_TYPE",
                        description = "Administrador eliminó actividades de $typeNames de ${user.name} (${user.email})",
                        oldValue = "Tipos: ${activityTypes.joinToString(", ")}"
                    )
                    
                    // Recargar las actividades del usuario
                    loadUserActivities(user.id)
                } catch (e: Exception) {
                    _error.value = "Error al eliminar actividades: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _error.value = "No tienes permisos para eliminar actividades"
        }
    }
} 