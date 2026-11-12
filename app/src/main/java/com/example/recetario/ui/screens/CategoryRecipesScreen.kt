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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recetario.R
import com.example.recetario.data.entity.Recipe
import com.example.recetario.ui.viewmodel.RecipeViewModel
import com.example.recetario.ui.viewmodel.UserViewModel
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun CategoryRecipesScreen(
    categoryName: String,
    recipeViewModel: RecipeViewModel,
    userViewModel: UserViewModel,
    onBackClick: () -> Unit,
    onRecipeClick: (Recipe) -> Unit = {},
    onAddRecipe: () -> Unit = {}
) {
    val backgroundColor = Color(0xFF6B655E)
    val orange = Color(0xFFFF8356)
    val white = Color.White
    val lightGray = Color(0xFFE5E1DC)
    
    val recipes by recipeViewModel.recipes.collectAsState()
    val isLoading by recipeViewModel.isLoading.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    
    // Load recipes by category when the screen is first displayed
    LaunchedEffect(categoryName, currentUser) {
        val userId = currentUser?.id ?: -1L
        recipeViewModel.getRecipesByCategoryForUser(categoryName, userId)
    }
    
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
                    onClick = onBackClick
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = orange
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = categoryName,
                    style = TextStyle(
                        color = orange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
            
            // Mostrar siempre la lista de recetas, sin loader
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recipes) { recipe ->
                    CategoryRecipeCard(
                        recipe = recipe,
                        currentUserId = currentUser?.id,
                        onClick = { onRecipeClick(recipe) },
                        onDeleteRecipe = { recipeViewModel.deleteRecipe(recipe) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryRecipeCard(
    recipe: Recipe,
    currentUserId: Long?,
    onClick: () -> Unit,
    onDeleteRecipe: () -> Unit
) {
    val orange = Color(0xFFFF8356)
    val white = Color.White
    val lightGray = Color(0xFFF5F5F5)
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (currentUserId != null && recipe.userId == currentUserId) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { showDeleteDialog = true }
                        )
                    }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recipe image placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(lightGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Recipe details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.title,
                    style = TextStyle(
                        color = Color(0xFF444444),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = recipe.description,
                    style = TextStyle(
                        color = Color(0xFF888888),
                        fontSize = 14.sp
                    ),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Recipe metadata
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.difficulty,
                        style = TextStyle(
                            color = orange,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "${recipe.cookingTime} min",
                        style = TextStyle(
                            color = orange,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "${recipe.servings} porciones",
                        style = TextStyle(
                            color = orange,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Receta") },
            text = { Text("¿Estás seguro de que quieres eliminar esta receta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteRecipe()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
} 