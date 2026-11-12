package com.example.recetario.data.dao

import androidx.room.*
import com.example.recetario.data.entity.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes WHERE isPublic = 1 ORDER BY createdAt DESC")
    fun getAllPublicRecipes(): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getRecipesByUser(userId: Long): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Long): Recipe?
    
    @Query("SELECT * FROM recipes WHERE category = :category AND isPublic = 1 ORDER BY createdAt DESC")
    fun getRecipesByCategory(category: String): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' AND isPublic = 1 ORDER BY createdAt DESC")
    fun searchRecipes(query: String): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' AND (isPublic = 1 OR userId = :userId) ORDER BY createdAt DESC")
    fun searchRecipesForUser(query: String, userId: Long): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE difficulty = :difficulty AND isPublic = 1 ORDER BY createdAt DESC")
    fun getRecipesByDifficulty(difficulty: String): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE cookingTime <= :maxTime AND isPublic = 1 ORDER BY createdAt DESC")
    fun getRecipesByMaxCookingTime(maxTime: Int): Flow<List<Recipe>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long
    
    @Update
    suspend fun updateRecipe(recipe: Recipe)
    
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
    
    @Query("DELETE FROM recipes WHERE userId = :userId")
    suspend fun deleteAllRecipesByUser(userId: Long)
    
    @Query("SELECT * FROM recipes WHERE category = :category AND (isPublic = 1 OR userId = :userId) ORDER BY createdAt DESC")
    fun getRecipesByCategoryForUser(category: String, userId: Long): Flow<List<Recipe>>
} 