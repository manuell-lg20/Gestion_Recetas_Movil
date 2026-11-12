package com.example.recetario.data.repository

import com.example.recetario.data.dao.RecipeDao
import com.example.recetario.data.entity.Recipe
import com.example.recetario.data.entity.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) {
    private val gson = Gson()
    
    fun getAllPublicRecipes(): Flow<List<Recipe>> = recipeDao.getAllPublicRecipes()
    
    fun getRecipesByUser(userId: Long): Flow<List<Recipe>> = recipeDao.getRecipesByUser(userId)
    
    suspend fun getRecipeById(recipeId: Long): Recipe? = recipeDao.getRecipeById(recipeId)
    
    fun getRecipesByCategory(category: String): Flow<List<Recipe>> = recipeDao.getRecipesByCategory(category)
    
    fun searchRecipes(query: String): Flow<List<Recipe>> = recipeDao.searchRecipes(query)
    
    fun searchRecipesForUser(query: String, userId: Long): Flow<List<Recipe>> = recipeDao.searchRecipesForUser(query, userId)
    
    fun getRecipesByDifficulty(difficulty: String): Flow<List<Recipe>> = recipeDao.getRecipesByDifficulty(difficulty)
    
    fun getRecipesByMaxCookingTime(maxTime: Int): Flow<List<Recipe>> = recipeDao.getRecipesByMaxCookingTime(maxTime)
    
    suspend fun createRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)
    
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
    
    suspend fun deleteAllRecipesByUser(userId: Long) = recipeDao.deleteAllRecipesByUser(userId)
    
    // Helper functions for ingredients
    fun ingredientsToString(ingredients: List<Ingredient>): String {
        return gson.toJson(ingredients)
    }
    
    fun stringToIngredients(ingredientsString: String): List<Ingredient> {
        val type = object : TypeToken<List<Ingredient>>() {}.type
        return try {
            gson.fromJson(ingredientsString, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun createRecipeWithIngredients(
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
    ): Long {
        val recipe = Recipe(
            title = title,
            description = description,
            ingredients = ingredientsToString(ingredients),
            instructions = instructions,
            cookingTime = cookingTime,
            servings = servings,
            difficulty = difficulty,
            category = category,
            imageUrl = imageUrl,
            userId = userId,
            isPublic = isPublic
        )
        return createRecipe(recipe)
    }

    suspend fun insertDefaultRecipesIfEmpty() {
        // Check if database is empty
        val allRecipes = recipeDao.getAllPublicRecipes().first()
        if (allRecipes.isNotEmpty()) {
            return // Database already has recipes
        }

        // Default recipes for each category
        val defaultRecipes = listOf(
            // Veggie
            Recipe(
                title = "Ensalada César Vegana",
                description = "Una deliciosa ensalada César sin productos animales",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Lechuga romana", 1.0, "cabeza"),
                    Ingredient("Crutones caseros", 1.0, "taza"),
                    Ingredient("Parmesano vegano", 0.5, "taza"),
                    Ingredient("Aderezo César vegano", 0.25, "taza")
                )),
                instructions = "1. Lavar y cortar la lechuga\n2. Preparar los crutones\n3. Mezclar todos los ingredientes\n4. Agregar el aderezo y servir",
                cookingTime = 15,
                servings = 4,
                difficulty = "Fácil",
                category = "Veggie",
                userId = 1,
                isPublic = true
            ),
            
            // Platos Principales
            Recipe(
                title = "Pollo a la Plancha con Hierbas",
                description = "Pechuga de pollo marinada con hierbas frescas y especias",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Pechuga de pollo", 4.0, "unidades"),
                    Ingredient("Romero fresco", 2.0, "ramitas"),
                    Ingredient("Tomillo", 1.0, "cucharadita"),
                    Ingredient("Aceite de oliva", 2.0, "cucharadas"),
                    Ingredient("Sal y pimienta", 1.0, "al gusto")
                )),
                instructions = "1. Marinar el pollo con hierbas y especias\n2. Calentar la plancha\n3. Cocinar 6-8 minutos por lado\n4. Reposar 5 minutos antes de cortar",
                cookingTime = 25,
                servings = 4,
                difficulty = "Fácil",
                category = "Platos Principales",
                userId = 1,
                isPublic = true
            ),
            
            // Postres
            Recipe(
                title = "Tiramisú Clásico",
                description = "El postre italiano más famoso con café y mascarpone",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Huevos", 6.0, "unidades"),
                    Ingredient("Azúcar", 0.75, "taza"),
                    Ingredient("Queso mascarpone", 500.0, "g"),
                    Ingredient("Café espresso", 1.0, "taza"),
                    Ingredient("Bizcochos de soletilla", 200.0, "g"),
                    Ingredient("Cacao en polvo", 2.0, "cucharadas")
                )),
                instructions = "1. Separar yemas y claras\n2. Batir yemas con azúcar\n3. Mezclar con mascarpone\n4. Montar claras y incorporar\n5. Armar capas con bizcochos mojados en café",
                cookingTime = 45,
                servings = 8,
                difficulty = "Medio",
                category = "Postres",
                userId = 1,
                isPublic = true
            ),
            
            // Comida Rápida
            Recipe(
                title = "Hamburguesa Gourmet",
                description = "Hamburguesa casera con ingredientes premium",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Carne molida", 500.0, "g"),
                    Ingredient("Pan de hamburguesa", 4.0, "unidades"),
                    Ingredient("Queso cheddar", 4.0, "rebanadas"),
                    Ingredient("Lechuga", 4.0, "hojas"),
                    Ingredient("Tomate", 1.0, "unidad"),
                    Ingredient("Cebolla", 0.5, "unidad")
                )),
                instructions = "1. Formar hamburguesas de 120g\n2. Sazonar con sal y pimienta\n3. Cocinar en plancha 4-5 minutos por lado\n4. Agregar queso al final\n5. Armar con vegetales",
                cookingTime = 20,
                servings = 4,
                difficulty = "Fácil",
                category = "Comida Rápida",
                userId = 1,
                isPublic = true
            ),
            
            // Mariscos
            Recipe(
                title = "Camarones al Ajillo",
                description = "Camarones salteados con ajo, limón y perejil",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Camarones pelados", 500.0, "g"),
                    Ingredient("Ajo", 6.0, "dientes"),
                    Ingredient("Aceite de oliva", 3.0, "cucharadas"),
                    Ingredient("Limón", 1.0, "unidad"),
                    Ingredient("Perejil fresco", 0.25, "taza"),
                    Ingredient("Sal y pimienta", 1.0, "al gusto")
                )),
                instructions = "1. Pelar y desvenar camarones\n2. Picar ajo finamente\n3. Calentar aceite y dorar ajo\n4. Agregar camarones y cocinar 2-3 minutos\n5. Agregar limón y perejil",
                cookingTime = 15,
                servings = 4,
                difficulty = "Fácil",
                category = "Mariscos",
                userId = 1,
                isPublic = true
            ),
            
            // Sopas
            Recipe(
                title = "Sopa de Tomate Casera",
                description = "Sopa cremosa de tomate con hierbas aromáticas",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Tomates maduros", 1.0, "kg"),
                    Ingredient("Cebolla", 1.0, "unidad"),
                    Ingredient("Ajo", 3.0, "dientes"),
                    Ingredient("Caldo de verduras", 1.0, "litro"),
                    Ingredient("Crema", 0.5, "taza"),
                    Ingredient("Albahaca", 0.25, "taza")
                )),
                instructions = "1. Asar tomates en horno\n2. Sofreír cebolla y ajo\n3. Agregar tomates y caldo\n4. Cocinar 20 minutos\n5. Licuar y agregar crema",
                cookingTime = 40,
                servings = 6,
                difficulty = "Medio",
                category = "Sopas",
                userId = 1,
                isPublic = true
            )
        )

        // Insert all default recipes
        defaultRecipes.forEach { recipe ->
            createRecipe(recipe)
        }
    }

    suspend fun insertDefaultRecipeForCategoryIfEmpty(category: String) {
        val recipesInCategory = recipeDao.getRecipesByCategory(category).first()
        if (recipesInCategory.isNotEmpty()) return

        val defaultRecipe = when (category) {
            "Veggie" -> Recipe(
                title = "Ensalada César Vegana",
                description = "Una deliciosa ensalada César sin productos animales",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Lechuga romana", 1.0, "cabeza"),
                    Ingredient("Crutones caseros", 1.0, "taza"),
                    Ingredient("Parmesano vegano", 0.5, "taza"),
                    Ingredient("Aderezo César vegano", 0.25, "taza")
                )),
                instructions = "1. Lavar y cortar la lechuga\n2. Preparar los crutones\n3. Mezclar todos los ingredientes\n4. Agregar el aderezo y servir",
                cookingTime = 15,
                servings = 4,
                difficulty = "Fácil",
                category = "Veggie",
                userId = 1,
                isPublic = true
            )
            "Platos Principales" -> Recipe(
                title = "Pollo a la Plancha con Hierbas",
                description = "Pechuga de pollo marinada con hierbas frescas y especias",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Pechuga de pollo", 4.0, "unidades"),
                    Ingredient("Romero fresco", 2.0, "ramitas"),
                    Ingredient("Tomillo", 1.0, "cucharadita"),
                    Ingredient("Aceite de oliva", 2.0, "cucharadas"),
                    Ingredient("Sal y pimienta", 1.0, "al gusto")
                )),
                instructions = "1. Marinar el pollo con hierbas y especias\n2. Calentar la plancha\n3. Cocinar 6-8 minutos por lado\n4. Reposar 5 minutos antes de cortar",
                cookingTime = 25,
                servings = 4,
                difficulty = "Fácil",
                category = "Platos Principales",
                userId = 1,
                isPublic = true
            )
            "Postres" -> Recipe(
                title = "Tiramisú Clásico",
                description = "El postre italiano más famoso con café y mascarpone",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Huevos", 6.0, "unidades"),
                    Ingredient("Azúcar", 0.75, "taza"),
                    Ingredient("Queso mascarpone", 500.0, "g"),
                    Ingredient("Café espresso", 1.0, "taza"),
                    Ingredient("Bizcochos de soletilla", 200.0, "g"),
                    Ingredient("Cacao en polvo", 2.0, "cucharadas")
                )),
                instructions = "1. Separar yemas y claras\n2. Batir yemas con azúcar\n3. Mezclar con mascarpone\n4. Montar claras y incorporar\n5. Armar capas con bizcochos mojados en café",
                cookingTime = 45,
                servings = 8,
                difficulty = "Medio",
                category = "Postres",
                userId = 1,
                isPublic = true
            )
            "Comida Rápida" -> Recipe(
                title = "Hamburguesa Gourmet",
                description = "Hamburguesa casera con ingredientes premium",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Carne molida", 500.0, "g"),
                    Ingredient("Pan de hamburguesa", 4.0, "unidades"),
                    Ingredient("Queso cheddar", 4.0, "rebanadas"),
                    Ingredient("Lechuga", 4.0, "hojas"),
                    Ingredient("Tomate", 1.0, "unidad"),
                    Ingredient("Cebolla", 0.5, "unidad")
                )),
                instructions = "1. Formar hamburguesas de 120g\n2. Sazonar con sal y pimienta\n3. Cocinar en plancha 4-5 minutos por lado\n4. Agregar queso al final\n5. Armar con vegetales",
                cookingTime = 20,
                servings = 4,
                difficulty = "Fácil",
                category = "Comida Rápida",
                userId = 1,
                isPublic = true
            )
            "Mariscos" -> Recipe(
                title = "Camarones al Ajillo",
                description = "Camarones salteados con ajo, limón y perejil",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Camarones pelados", 500.0, "g"),
                    Ingredient("Ajo", 6.0, "dientes"),
                    Ingredient("Aceite de oliva", 3.0, "cucharadas"),
                    Ingredient("Limón", 1.0, "unidad"),
                    Ingredient("Perejil fresco", 0.25, "taza"),
                    Ingredient("Sal y pimienta", 1.0, "al gusto")
                )),
                instructions = "1. Pelar y desvenar camarones\n2. Picar ajo finamente\n3. Calentar aceite y dorar ajo\n4. Agregar camarones y cocinar 2-3 minutos\n5. Agregar limón y perejil",
                cookingTime = 15,
                servings = 4,
                difficulty = "Fácil",
                category = "Mariscos",
                userId = 1,
                isPublic = true
            )
            "Sopas" -> Recipe(
                title = "Sopa de Tomate Casera",
                description = "Sopa cremosa de tomate con hierbas aromáticas",
                ingredients = ingredientsToString(listOf(
                    Ingredient("Tomates maduros", 1.0, "kg"),
                    Ingredient("Cebolla", 1.0, "unidad"),
                    Ingredient("Ajo", 3.0, "dientes"),
                    Ingredient("Caldo de verduras", 1.0, "litro"),
                    Ingredient("Crema", 0.5, "taza"),
                    Ingredient("Albahaca", 0.25, "taza")
                )),
                instructions = "1. Asar tomates en horno\n2. Sofreír cebolla y ajo\n3. Agregar tomates y caldo\n4. Cocinar 20 minutos\n5. Licuar y agregar crema",
                cookingTime = 40,
                servings = 6,
                difficulty = "Medio",
                category = "Sopas",
                userId = 1,
                isPublic = true
            )
            else -> null
        }
        if (defaultRecipe != null) {
            createRecipe(defaultRecipe)
        }
    }

    fun getRecipesByCategoryForUser(category: String, userId: Long): Flow<List<Recipe>> = recipeDao.getRecipesByCategoryForUser(category, userId)
} 