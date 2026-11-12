package com.example.recetario.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recetario.data.entity.Recipe
import com.example.recetario.data.entity.Ingredient
import com.example.recetario.data.repository.RecipeRepository
import com.example.recetario.data.repository.UserRepository
import com.example.recetario.data.repository.UserActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
    private val userActivityRepository: UserActivityRepository
) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _userRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val userRecipes: StateFlow<List<Recipe>> = _userRecipes.asStateFlow()

    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAllRecipes()
    }

    fun loadAllRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getAllPublicRecipes().collect { recipes ->
                    _recipes.value = recipes
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las recetas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserRecipes(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getRecipesByUser(userId).collect { recipes ->
                    _userRecipes.value = recipes
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las recetas del usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getRecipeById(recipeId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recipe = recipeRepository.getRecipeById(recipeId)
                _selectedRecipe.value = recipe
            } catch (e: Exception) {
                _error.value = "Error al cargar la receta: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createRecipe(
        title: String,
        description: String,
        ingredients: List<Ingredient>,
        instructions: String,
        cookingTime: Int,
        servings: Int,
        difficulty: String,
        category: String,
        imageUrl: String?,
        userId: Long,
        isPublic: Boolean = true
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recipeId = recipeRepository.createRecipeWithIngredients(
                    title = title,
                    description = description,
                    ingredients = ingredients,
                    instructions = instructions,
                    cookingTime = cookingTime,
                    servings = servings,
                    difficulty = difficulty,
                    category = category,
                    imageUrl = imageUrl,
                    userId = userId,
                    isPublic = isPublic
                )
                
                // Registrar actividad de creación de receta
                userActivityRepository.logActivity(
                    userId = userId,
                    activityType = "ADD_RECIPE",
                    description = "Creó la receta: $title",
                    recipeId = recipeId
                )
                
                loadUserRecipes(userId)
            } catch (e: Exception) {
                _error.value = "Error al crear la receta: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener la receta original antes de actualizar
                val originalRecipe = recipeRepository.getRecipeById(recipe.id)
                
                recipeRepository.updateRecipe(recipe)
                
                // Registrar actividad de edición de receta
                userActivityRepository.logActivity(
                    userId = recipe.userId,
                    activityType = "EDIT_RECIPE",
                    description = "Editó la receta: ${recipe.title}",
                    recipeId = recipe.id,
                    oldValue = originalRecipe?.title,
                    newValue = recipe.title
                )
                
                loadUserRecipes(recipe.userId)
            } catch (e: Exception) {
                _error.value = "Error al actualizar la receta: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Registrar actividad de eliminación de receta antes de eliminar
                userActivityRepository.logActivity(
                    userId = recipe.userId,
                    activityType = "DELETE_RECIPE",
                    description = "Eliminó la receta: ${recipe.title}",
                    recipeId = recipe.id,
                    oldValue = recipe.title
                )
                
                recipeRepository.deleteRecipe(recipe)
                loadUserRecipes(recipe.userId)
            } catch (e: Exception) {
                _error.value = "Error al eliminar la receta: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.searchRecipes(query).collect { recipes ->
                    _recipes.value = recipes
                }
            } catch (e: Exception) {
                _error.value = "Error al buscar recetas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchRecipesForUser(query: String, userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.searchRecipesForUser(query, userId).collect { recipes ->
                    _recipes.value = recipes
                }
            } catch (e: Exception) {
                _error.value = "Error al buscar recetas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getRecipesByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.insertDefaultRecipeForCategoryIfEmpty(category)
                recipeRepository.getRecipesByCategory(category).collect { recipes ->
                    _recipes.value = recipes
                }
            } catch (e: Exception) {
                _error.value = "Error al filtrar por categoría: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getRecipesByCategoryForUser(category: String, userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getRecipesByCategoryForUser(category, userId).collect { recipes ->
                    _recipes.value = recipes
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar recetas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun insertDefaultRecipesIfEmpty() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.insertDefaultRecipesIfEmpty()
                loadAllRecipes() // Recargar las recetas después de insertar las predeterminadas
            } catch (e: Exception) {
                _error.value = "Error al insertar recetas predeterminadas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 