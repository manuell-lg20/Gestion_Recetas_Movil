package com.example.recetario.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String? = null,
    val profileImage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isAdmin: Boolean = false,
    val isDeleted: Boolean = false
) 