package com.example.recetario.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_activities")
data class UserActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val activityType: String, // "LOGIN", "LOGOUT", "ADD_RECIPE", "EDIT_RECIPE", "DELETE_RECIPE", "UPDATE_PROFILE"
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val recipeId: Long? = null,
    val oldValue: String? = null,
    val newValue: String? = null
) 