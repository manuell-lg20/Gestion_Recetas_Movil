package com.example.recetario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recetario.ui.screens.LoginScreen
import com.example.recetario.ui.screens.RegisterScreen
import com.example.recetario.ui.screens.HomeScreen
import com.example.recetario.ui.screens.CategoryRecipesScreen
import com.example.recetario.ui.screens.AddRecipeSimpleDialog
import com.example.recetario.ui.screens.UserAccountsScreen
import com.example.recetario.ui.screens.AdminScreen
import com.example.recetario.ui.screens.RecipeDetailScreen
import com.example.recetario.ui.screens.MyRecipesScreen
import com.example.recetario.ui.theme.RecetarioTheme
import com.example.recetario.ui.viewmodel.UserViewModel
import com.example.recetario.ui.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecetarioTheme {
                RecetarioApp()
            }
        }
    }
}

@Composable
fun RecetarioApp() {
    val userViewModel: UserViewModel = hiltViewModel()
    val recipeViewModel: RecipeViewModel = hiltViewModel()
    val currentUser by userViewModel.currentUser.collectAsState()
    val isAdmin by userViewModel.isAdmin.collectAsState()
    
    var showRegister by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showAddRecipe by remember { mutableStateOf(false) }
    var showUserAccounts by remember { mutableStateOf(false) }
    var showAdminScreen by remember { mutableStateOf(false) }
    var selectedRecipeId by remember { mutableStateOf<Long?>(null) }
    var showEditRecipe by remember { mutableStateOf(false) }
    var showMyRecipes by remember { mutableStateOf(false) }
    
    // Insert default recipes when user logs in
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            recipeViewModel.insertDefaultRecipesIfEmpty()
        }
    }
    
    if (currentUser == null) {
        if (showRegister) {
            RegisterScreen(
                onRegisterSuccess = {
                    // El usuario se registró exitosamente, no necesitamos hacer nada aquí
                    // porque el estado se actualiza automáticamente
                },
                onBackToLogin = {
                    showRegister = false
                }
            )
        } else {
            LoginScreen(
                onLoginSuccess = {
                    // El usuario se logueó exitosamente, no necesitamos hacer nada aquí
                    // porque el estado se actualiza automáticamente
                },
                onRegisterClick = {
                    showRegister = true
                }
            )
        }
    } else {
        // Check if user is admin and show admin screen
        if (isAdmin) {
            AdminScreen(
                userViewModel = userViewModel,
                recipeViewModel = recipeViewModel,
                onBackClick = {
                    userViewModel.logout()
                }
            )
        }
        // Check if we should show user accounts screen
        else if (showUserAccounts) {
            UserAccountsScreen(
                userViewModel = userViewModel,
                onBackClick = {
                    showUserAccounts = false
                }
            )
        }
        // Check if we should show add recipe screen
        else if (showAddRecipe) {
            AddRecipeSimpleDialog(
                currentUserId = currentUser!!.id,
                onRecipeAdded = {
                    showAddRecipe = false
                    recipeViewModel.loadAllRecipes()
                },
                onDismiss = {
                    showAddRecipe = false
                },
                recipeViewModel = recipeViewModel
            )
        } else if (selectedCategory != null && selectedRecipeId == null) {
            CategoryRecipesScreen(
                categoryName = selectedCategory!!,
                recipeViewModel = recipeViewModel,
                userViewModel = userViewModel,
                onBackClick = {
                    selectedCategory = null
                },
                onRecipeClick = { recipe ->
                    selectedRecipeId = recipe.id
                },
                onAddRecipe = {
                    showAddRecipe = true
                }
            )
        } else if (selectedRecipeId != null && !showEditRecipe) {
            RecipeDetailScreen(
                recipeId = selectedRecipeId!!,
                recipeViewModel = recipeViewModel,
                onBackClick = { selectedRecipeId = null },
                onEditClick = { showEditRecipe = true }
            )
        } else if (selectedRecipeId != null && showEditRecipe) {
            AddRecipeSimpleDialog(
                currentUserId = currentUser!!.id,
                onRecipeAdded = {
                    showEditRecipe = false
                    recipeViewModel.loadAllRecipes()
                },
                onDismiss = {
                    showEditRecipe = false
                },
                recipeViewModel = recipeViewModel
            )
        } else if (showMyRecipes) {
            MyRecipesScreen(
                userViewModel = userViewModel,
                recipeViewModel = recipeViewModel,
                onBackClick = { showMyRecipes = false },
                onRecipeClick = { recipeId -> selectedRecipeId = recipeId }
            )
        } else {
            HomeScreen(
                userViewModel = userViewModel,
                recipeViewModel = recipeViewModel,
                onAddRecipe = {
                    showAddRecipe = true
                },
                onCategories = {
                    // This is handled within the drawer
                },
                onCategoryClick = { categoryName ->
                    selectedCategory = categoryName
                },
                onUserAccounts = {
                    showUserAccounts = true
                },
                onRecipeClick = { recipeId ->
                    selectedRecipeId = recipeId
                },
                onMyRecipes = {
                    showMyRecipes = true
                }
            )
        }
    }
}