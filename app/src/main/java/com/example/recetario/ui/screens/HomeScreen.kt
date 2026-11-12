package com.example.recetario.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.recetario.R
import com.example.recetario.ui.viewmodel.UserViewModel
import com.example.recetario.ui.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel,
    onAddRecipe: () -> Unit = {},
    onCategories: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onUserAccounts: () -> Unit = {},
    onRecipeClick: (Long) -> Unit = {},
    onMyRecipes: () -> Unit = {}
) {
    val backgroundColor = Color(0xFF6B655E)
    val orange = Color(0xFFFF8356)
    val lightGray = Color(0xFFE5E1DC)
    val white = Color.White
    
    // Estado para el menú de perfil
    var showProfileMenu by remember { mutableStateOf(false) }
    // Estado para el drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentUser by userViewModel.currentUser.collectAsState()
    
    // Estado para mostrar categorías en el drawer
    var showCategoriesInDrawer by remember { mutableStateOf(false) }
    
    // Lista de categorías
    val categories = listOf(
        Category("Veggie", R.drawable.ic_veggie),
        Category("Platos Principales", R.drawable.ic_main_dish),
        Category("Postres", R.drawable.ic_dessert),
        Category("Comida Rápida", R.drawable.ic_fastfood),
        Category("Mariscos", R.drawable.ic_seafood),
        Category("Sopas", R.drawable.ic_soup)
    )

    var searchText by remember { mutableStateOf("") }
    val recipes by recipeViewModel.recipes.collectAsState()

    // Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (showCategoriesInDrawer) {
                CategoriesDrawerContent(
                    categories = categories,
                    onCategoryClick = { category ->
                        scope.launch { drawerState.close() }
                        showCategoriesInDrawer = false
                        onCategoryClick(category.name)
                    },
                    onBackClick = {
                        showCategoriesInDrawer = false
                    }
                )
            } else {
                DrawerContent(
                    currentUser = currentUser,
                    onAddRecipe = {
                        onAddRecipe()
                        scope.launch { drawerState.close() }
                    },
                    onCategories = {
                        showCategoriesInDrawer = true
                    },
                    onMyRecipes = {
                        onMyRecipes()
                        scope.launch { drawerState.close() }
                    },
                    onLogout = {
                        userViewModel.logout()
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        // Contenido principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // AppBar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            scope.launch { drawerState.open() }
                        }
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = orange)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Recetas",
                        style = TextStyle(color = orange, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Botón de perfil con menú desplegable
                    Box {
                        IconButton(
                            onClick = { showProfileMenu = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(lightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    tint = orange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        // Menú desplegable de perfil
                        DropdownMenu(
                            expanded = showProfileMenu,
                            onDismissRequest = { showProfileMenu = false },
                            modifier = Modifier
                                .background(white)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            // Información del usuario
                            currentUser?.let { user ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = user.name,
                                                style = TextStyle(
                                                    color = Color(0xFF444444),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            )
                                            Text(
                                                text = user.email,
                                                style = TextStyle(
                                                    color = Color(0xFF888888),
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    },
                                    onClick = { showProfileMenu = false },
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            
                            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                            
                            // Opción de cuentas de usuario SOLO para administradores
                            currentUser?.let { user ->
                                if (user.isAdmin) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFF8356),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Cuentas de Usuario",
                                                    style = TextStyle(
                                                        color = Color(0xFFFF8356),
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 14.sp
                                                    )
                                                )
                                            }
                                        },
                                        onClick = {
                                            onUserAccounts()
                                            showProfileMenu = false
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    
                                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                                }
                            }
                            
                            // Opción de cerrar sesión
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = null,
                                            tint = Color(0xFFFF8356),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Cerrar Sesión",
                                            style = TextStyle(
                                                color = Color(0xFFFF8356),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        )
                                    }
                                },
                                onClick = {
                                    userViewModel.logout()
                                    showProfileMenu = false
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
                // Barra de búsqueda
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        if (it.isNotBlank()) {
                            currentUser?.let { user ->
                                recipeViewModel.searchRecipesForUser(it, user.id)
                            } ?: recipeViewModel.searchRecipes(it)
                        } else {
                            recipeViewModel.loadAllRecipes()
                        }
                    },
                    label = { Text("Buscar receta") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(orange, Color(0xFFFFB56B))
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cambia tus hábitos\nalimenticios",
                                style = TextStyle(color = white, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { /* TODO: Acción */ },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = white,
                                    contentColor = orange
                                ),
                                border = BorderStroke(1.dp, orange)
                            ) {
                                Text(
                                    text = "Descubre como",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Categorías
                Text(
                    text = "Categorías",
                    style = TextStyle(color = white, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                CategoryGrid(categories, onCategoryClick = { categoryName ->
                    onCategoryClick(categoryName)
                })
                Spacer(modifier = Modifier.height(24.dp))
                // Popular
                Text(
                    text = "Popular",
                    style = TextStyle(color = white, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                if (searchText.isNotBlank()) {
                    if (recipes.isEmpty()) {
                        Text("No se encontraron recetas.", color = white)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            recipes.forEach { recipe ->
                                PopularRecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe.id) })
                            }
                        }
                    }
                } else {
                    PopularRecipeCard()
                }
            }
            
            // Floating Action Button
            FloatingActionButton(
                onClick = { onAddRecipe() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = orange,
                contentColor = white
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Añadir Receta",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

data class Category(val name: String, val icon: Int)

@Composable
fun CategoryGrid(categories: List<Category>, onCategoryClick: (String) -> Unit) {
    Column {
        for (row in categories.chunked(3)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (cat in row) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickable { onCategoryClick(cat.name) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = cat.icon),
                                contentDescription = cat.name,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                cat.name,
                                style = TextStyle(color = Color(0xFFFF8356), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            )
                        }
                    }
                }
                if (row.size < 3) {
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PopularRecipeCard(recipe: com.example.recetario.data.entity.Recipe? = null, onClick: (() -> Unit)? = null) {
    val orange = Color(0xFFFF8356)
    val white = Color.White
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(white)
            .let { if (onClick != null) it.clickable { onClick() } else it },
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.example.recetario.R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe?.title ?: "Deliciosos Gyoza",
                    style = TextStyle(color = Color(0xFF444444), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = recipe?.difficulty ?: "Difícil",
                        style = TextStyle(color = orange, fontSize = 13.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = recipe?.cookingTime?.toString()?.plus(" min") ?: "2h",
                        style = TextStyle(color = orange, fontSize = 13.sp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onClick?.invoke() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = white,
                        contentColor = orange
                    ),
                    border = BorderStroke(1.dp, orange)
                ) {
                    Text(
                        text = "Preparación",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    currentUser: com.example.recetario.data.entity.User?,
    onAddRecipe: () -> Unit,
    onCategories: () -> Unit,
    onMyRecipes: () -> Unit,
    onLogout: () -> Unit
) {
    val orange = Color(0xFFFF8356)
    val white = Color.White
    val lightGray = Color(0xFFE5E1DC)
    
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(white)
            .padding(16.dp)
    ) {
        // Header del drawer
        Column(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(lightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = orange,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            currentUser?.let { user ->
                Text(
                    text = user.name,
                    style = TextStyle(
                        color = Color(0xFF444444),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = user.email,
                    style = TextStyle(
                        color = Color(0xFF888888),
                        fontSize = 14.sp
                    )
                )
            }
        }
        
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Opciones del menú
        DrawerMenuItem(
            icon = Icons.Default.Home,
            text = "Inicio",
            onClick = { /* Ya estamos en inicio */ }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DrawerMenuItem(
            icon = Icons.Default.Home,
            text = "Mis Recetas",
            onClick = onMyRecipes
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DrawerMenuItem(
            icon = Icons.Default.Add,
            text = "Añadir Receta",
            onClick = onAddRecipe
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DrawerMenuItem(
            icon = Icons.Default.Menu,
            text = "Categorías",
            onClick = onCategories
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Opción de cerrar sesión
        DrawerMenuItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            text = "Cerrar Sesión",
            onClick = onLogout,
            textColor = orange
        )
    }
}

@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color(0xFF444444)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = TextStyle(
                color = textColor,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
fun CategoriesDrawerContent(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    onBackClick: () -> Unit
) {
    val orange = Color(0xFFFF8356)
    val white = Color.White
    val lightGray = Color(0xFFE5E1DC)
    
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(white)
            .padding(16.dp)
    ) {
        // Header con botón de regreso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = orange,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Categorías",
                style = TextStyle(
                    color = Color(0xFF444444),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        }
        
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Lista de categorías
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryDrawerItem(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryDrawerItem(
    category: Category,
    onClick: () -> Unit
) {
    val orange = Color(0xFFFF8356)
    val lightGray = Color(0xFFF5F5F5)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp))
            .background(lightGray)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de la categoría
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = category.icon),
                contentDescription = category.name,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Nombre de la categoría
        Text(
            text = category.name,
            style = TextStyle(
                color = Color(0xFF444444),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Icono de flecha
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = orange,
            modifier = Modifier.size(20.dp)
        )
    }
} 