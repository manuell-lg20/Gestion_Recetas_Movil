# Funcionalidad de Cuentas de Usuario y Seguimiento de Actividades

## Descripción

Se ha añadido una nueva funcionalidad a la aplicación Recetario que permite:

1. **Verificar las cuentas existentes** en el dispositivo
2. **Rastrear las actividades** que han realizado los usuarios
3. **Ver el historial de cambios** realizados por cada usuario

## Características Implementadas

### 1. Rastreo de Actividades de Usuario

La aplicación ahora registra automáticamente las siguientes actividades:

- **LOGIN**: Cuando un usuario inicia sesión
- **LOGOUT**: Cuando un usuario cierra sesión
- **ADD_RECIPE**: Cuando un usuario crea una nueva receta
- **EDIT_RECIPE**: Cuando un usuario edita una receta existente
- **DELETE_RECIPE**: Cuando un usuario elimina una receta
- **UPDATE_PROFILE**: Cuando un usuario actualiza su perfil

### 2. Base de Datos de Actividades

Se ha creado una nueva tabla `user_activities` que almacena:

- ID de la actividad
- ID del usuario que realizó la actividad
- Tipo de actividad
- Descripción de la actividad
- Timestamp de cuando ocurrió
- ID de la receta (si aplica)
- Valores anteriores y nuevos (para cambios)

### 3. Pantalla de Cuentas de Usuario

Nueva pantalla accesible desde el menú de perfil que permite:

- **Ver todas las cuentas** registradas en el dispositivo
- **Seleccionar un usuario** para ver sus actividades específicas
- **Ver el historial completo** de actividades de cada usuario
- **Filtrar actividades** por tipo

## Cómo Usar la Funcionalidad

### Acceder a las Cuentas de Usuario

1. Inicia sesión en la aplicación
2. Toca el icono de perfil en la esquina superior derecha
3. Selecciona "Cuentas de Usuario" del menú desplegable

### Ver Actividades de un Usuario Específico

1. En la pantalla de cuentas, toca sobre cualquier usuario
2. Verás una lista de todas las actividades realizadas por ese usuario
3. Cada actividad muestra:
   - Tipo de actividad
   - Descripción
   - Fecha y hora
   - Icono representativo
   - Valores anteriores y nuevos (si aplica)

### Ver Todas las Actividades

1. Selecciona un usuario
2. Toca el icono de lista en la barra superior
3. Verás todas las actividades de todos los usuarios

## Estructura Técnica

### Nuevas Entidades

```kotlin
// UserActivity.kt
data class UserActivity(
    val id: Long = 0,
    val userId: Long,
    val activityType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val recipeId: Long? = null,
    val oldValue: String? = null,
    val newValue: String? = null
)
```

### Nuevos DAOs y Repositorios

- `UserActivityDao`: Maneja las operaciones de base de datos para actividades
- `UserActivityRepository`: Proporciona métodos de alto nivel para manejar actividades

### Actualizaciones en ViewModels

- `UserViewModel`: Añadidos métodos para cargar usuarios y actividades
- `RecipeViewModel`: Integrado registro automático de actividades al crear/editar/eliminar recetas

## Beneficios

1. **Transparencia**: Los usuarios pueden ver qué cambios han realizado
2. **Auditoría**: Historial completo de actividades para cada usuario
3. **Seguridad**: Rastreo de accesos y modificaciones
4. **Debugging**: Facilita la identificación de problemas o cambios no deseados

## Consideraciones de Privacidad

- Las actividades se almacenan localmente en el dispositivo
- No se comparten datos con servidores externos
- Los usuarios pueden ver solo sus propias actividades y las de otros usuarios registrados en el mismo dispositivo

## Futuras Mejoras

- Filtros por fecha y tipo de actividad
- Exportación de historial de actividades
- Notificaciones de actividades importantes
- Estadísticas de uso por usuario 