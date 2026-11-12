package com.example.recetario.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recetario.data.entity.Recipe
import com.example.recetario.data.entity.Ingredient
import com.example.recetario.ui.viewmodel.RecipeViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeViewModel: RecipeViewModel,
    onBackClick: () -> Unit,
    onEditClick: (Recipe) -> Unit
) {
    val recipe by recipeViewModel.selectedRecipe.collectAsState()
    val isLoading by recipeViewModel.isLoading.collectAsState()
    val error by recipeViewModel.error.collectAsState()

    val orange = Color(0xFFFF8356)
    val backgroundColor = Color(0xFF6B655E)
    val white = Color.White

    LaunchedEffect(recipeId) {
        recipeViewModel.getRecipeById(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.title ?: "Detalle de Receta") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (recipe != null) {
                        IconButton(onClick = { onEditClick(recipe!!) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar receta", tint = orange)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null && error is String) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error!!, color = Color.Red)
                }
            } else if (recipe != null) {
                val scrollState = rememberScrollState()
                val recipeVal = recipe!!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Imagen
                            if (!recipeVal.imageUrl.isNullOrEmpty()) {
                                // Aquí podrías usar Coil o similar para cargar la imagen
                            } else {
                                Image(
                                    painter = painterResource(id = com.example.recetario.R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )
                            }
                            // Título
                            Text(
                                text = recipeVal.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = backgroundColor
                            )
                            // Descripción
                            Text(
                                text = recipeVal.description,
                                fontSize = 16.sp,
                                color = Color(0xFF444444)
                            )
                            // Categoría, dificultad, tiempo, porciones
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(text = "Categoría: ${recipeVal.category}", fontSize = 14.sp, color = Color(0xFF444444))
                                Text(text = "Dificultad: ${recipeVal.difficulty}", fontSize = 14.sp, color = Color(0xFF444444))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(text = "⏱️ ${recipeVal.cookingTime} min", fontSize = 14.sp, color = orange)
                                Text(text = "👥 ${recipeVal.servings} porciones", fontSize = 14.sp, color = orange)
                            }
                            // Ingredientes
                            Text(
                                text = "Ingredientes:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = backgroundColor
                            )
                            val ingredients = remember(recipeVal.ingredients) {
                                val type = object : TypeToken<List<com.example.recetario.data.entity.Ingredient>>() {}.type
                                try {
                                    Gson().fromJson<List<com.example.recetario.data.entity.Ingredient>>(recipeVal.ingredients, type) ?: emptyList()
                                } catch (e: Exception) {
                                    emptyList()
                                }
                            }
                            if (ingredients.isEmpty()) {
                                Text("No hay ingredientes registrados.", color = Color(0xFF444444))
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    ingredients.forEach { ing ->
                                        Text("- ${ing.amount} ${ing.unit} ${ing.name}", color = Color(0xFF444444))
                                    }
                                }
                            }
                            // Instrucciones
                            Text(
                                text = "Instrucciones:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = backgroundColor
                            )
                            Text(
                                text = recipeVal.instructions,
                                fontSize = 16.sp,
                                color = Color(0xFF444444)
                            )
                        }
                    }
                }
            }
        }
    }
} 