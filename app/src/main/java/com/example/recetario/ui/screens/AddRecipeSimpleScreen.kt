package com.example.recetario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recetario.data.entity.Ingredient
import com.example.recetario.ui.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Check

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeSimpleDialog(
    currentUserId: Long,
    onRecipeAdded: () -> Unit = {},
    onDismiss: () -> Unit = {},
    recipeViewModel: RecipeViewModel = hiltViewModel()
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF6B655E), shape = RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Encabezado atractivo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFF8356), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .padding(vertical = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "🍽️ Añadir Receta",
                                style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Color.White)
                            }
                        }
                    }
                    Divider(color = Color(0x33FFFFFF), thickness = 1.dp)
                    // Formulario
                    AddRecipeSimpleForm(
                        currentUserId = currentUserId,
                        onRecipeAdded = onRecipeAdded,
                        recipeViewModel = recipeViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeSimpleForm(
    currentUserId: Long,
    onRecipeAdded: () -> Unit = {},
    recipeViewModel: RecipeViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var cookingTime by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Fácil") }
    var category by remember { mutableStateOf("Platos Principales") }
    var ingredients by remember { mutableStateOf(listOf<Ingredient>()) }
    var showSuccess by remember { mutableStateOf(false) }
    var lastCategoryAdded by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    val isLoading by recipeViewModel.isLoading.collectAsState()
    val error by recipeViewModel.error.collectAsState()

    val difficulties = listOf("Fácil", "Medio", "Difícil")
    val categories = listOf(
        "Veggie",
        "Platos Principales",
        "Postres",
        "Comida Rápida",
        "Mariscos",
        "Sopas"
    )
    val orange = Color(0xFFFF8356)
    val backgroundColor = Color(0xFF6B655E)
    val white = Color.White
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título de la receta") },
            leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null, tint = orange) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orange,
                unfocusedBorderColor = Color(0xFFCCCCCC),
                focusedLabelColor = orange,
                unfocusedLabelColor = Color(0xFF888888)
            )
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null, tint = orange) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orange,
                unfocusedBorderColor = Color(0xFFCCCCCC),
                focusedLabelColor = orange,
                unfocusedLabelColor = Color(0xFF888888)
            )
        )
        // Categoría
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it },
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = { },
                readOnly = true,
                label = { Text("Categoría") },
                leadingIcon = { Icon(Icons.Filled.List, contentDescription = null, tint = orange) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = orange,
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    focusedLabelColor = orange,
                    unfocusedLabelColor = Color(0xFF888888)
                )
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false },
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            categoryExpanded = false
                        }
                    )
                }
            }
        }
        // Dificultad
        ExposedDropdownMenuBox(
            expanded = difficultyExpanded,
            onExpandedChange = { difficultyExpanded = it },
        ) {
            OutlinedTextField(
                value = difficulty,
                onValueChange = { },
                readOnly = true,
                label = { Text("Dificultad") },
                leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null, tint = orange) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = difficultyExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = orange,
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    focusedLabelColor = orange,
                    unfocusedLabelColor = Color(0xFF888888)
                )
            )
            ExposedDropdownMenu(
                expanded = difficultyExpanded,
                onDismissRequest = { difficultyExpanded = false },
            ) {
                difficulties.forEach { diff ->
                    DropdownMenuItem(
                        text = { Text(diff) },
                        onClick = {
                            difficulty = diff
                            difficultyExpanded = false
                        }
                    )
                }
            }
        }
        OutlinedTextField(
            value = cookingTime,
            onValueChange = { cookingTime = it },
            label = { Text("Tiempo de cocción (minutos)") },
            leadingIcon = { Icon(Icons.Filled.Schedule, contentDescription = null, tint = orange) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orange,
                unfocusedBorderColor = Color(0xFFCCCCCC),
                focusedLabelColor = orange,
                unfocusedLabelColor = Color(0xFF888888)
            )
        )
        OutlinedTextField(
            value = servings,
            onValueChange = { servings = it },
            label = { Text("Número de porciones") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = orange) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orange,
                unfocusedBorderColor = Color(0xFFCCCCCC),
                focusedLabelColor = orange,
                unfocusedLabelColor = Color(0xFF888888)
            )
        )
        OutlinedTextField(
            value = instructions,
            onValueChange = { instructions = it },
            label = { Text("Instrucciones") },
            leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null, tint = orange) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 8,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = orange,
                unfocusedBorderColor = Color(0xFFCCCCCC),
                focusedLabelColor = orange,
                unfocusedLabelColor = Color(0xFF888888)
            )
        )
        // Switch para receta pública
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = isPublic,
                onCheckedChange = { isPublic = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = orange,
                    checkedTrackColor = orange.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("¿Receta pública?", color = Color(0xFF444444))
        }
        if (error != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFF6B6B), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = error!!,
                    style = TextStyle(color = Color(0xFFFF6B6B), fontSize = 14.sp),
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Botón destacado
        Button(
            onClick = {
                focusManager.clearFocus(force = true)
                recipeViewModel.createRecipe(
                    title = title,
                    description = description,
                    ingredients = ingredients,
                    instructions = instructions,
                    cookingTime = cookingTime.toIntOrNull() ?: 30,
                    servings = servings.toIntOrNull() ?: 4,
                    difficulty = difficulty,
                    category = category,
                    imageUrl = imageUrl,
                    userId = currentUserId,
                    isPublic = isPublic
                )
                showSuccess = true
                lastCategoryAdded = category
            },
            enabled = title.isNotBlank() && description.isNotBlank() && instructions.isNotBlank() && cookingTime.isNotBlank() && servings.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = orange, contentColor = white)
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = white)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        if (showSuccess) {
            Text(
                text = "Receta añadida a: $lastCategoryAdded",
                style = TextStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 16.sp),
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
            )
            LaunchedEffect(showSuccess) {
                kotlinx.coroutines.delay(1000)
                showSuccess = false
                title = ""
                description = ""
                instructions = ""
                cookingTime = ""
                servings = ""
                difficulty = "Fácil"
                category = "Platos Principales"
                onRecipeAdded()
            }
        }
    }
} 