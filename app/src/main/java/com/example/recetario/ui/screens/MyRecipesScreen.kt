package com.example.recetario.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recetario.data.entity.Recipe
import com.example.recetario.ui.viewmodel.UserViewModel
import com.example.recetario.ui.viewmodel.RecipeViewModel
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesScreen(
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel,
    onBackClick: () -> Unit,
    onRecipeClick: (Long) -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val myRecipes by recipeViewModel.userRecipes.collectAsState()
    val isLoading by recipeViewModel.isLoading.collectAsState()
    val error by recipeViewModel.error.collectAsState()

    val orange = Color(0xFFFF8356)
    val backgroundColor = Color(0xFF6B655E)
    val white = Color.White

    LaunchedEffect(currentUser) {
        currentUser?.let { recipeViewModel.loadUserRecipes(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Recetas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp)
        ) {
            if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error!!, color = Color.Red)
                }
            } else if (myRecipes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No has añadido recetas todavía.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(myRecipes) { recipe ->
                        var isPublic by remember { mutableStateOf(recipe.isPublic) }
                        var showDeleteDialog by remember { mutableStateOf(false) }
                        Card(
                            onClick = { onRecipeClick(recipe.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = { showDeleteDialog = true }
                                    )
                                },
                            colors = CardDefaults.cardColors(containerColor = white)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = recipe.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = backgroundColor,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Switch(
                                        checked = isPublic,
                                        onCheckedChange = {
                                            isPublic = it
                                            val updatedRecipe = recipe.copy(isPublic = it)
                                            recipeViewModel.updateRecipe(updatedRecipe)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = orange,
                                            checkedTrackColor = orange.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recipe.description,
                                    fontSize = 14.sp,
                                    color = Color(0xFF444444),
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "⏱️ ${recipe.cookingTime} min",
                                        fontSize = 12.sp,
                                        color = orange
                                    )
                                    Text(
                                        text = "👥 ${recipe.servings} porciones",
                                        fontSize = 12.sp,
                                        color = orange
                                    )
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
                                        recipeViewModel.deleteRecipe(recipe)
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
                }
            }
        }
    }
} 