package com.example.recetario.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "recipes",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val ingredients: String, // JSON string of ingredients
    val instructions: String,
    val cookingTime: Int, // in minutes
    val servings: Int,
    val difficulty: String, // "Fácil", "Medio", "Difícil"
    val category: String, // "Desayuno", "Almuerzo", "Cena", "Postre", "Snack"
    val imageUrl: String? = null,
    val userId: Long,
    val isPublic: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 