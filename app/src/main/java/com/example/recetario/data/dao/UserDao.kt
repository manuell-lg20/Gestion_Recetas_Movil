package com.example.recetario.data.dao

import androidx.room.*
import com.example.recetario.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isDeleted = 0")
    suspend fun getAllUsers(): List<User>
    
    @Query("SELECT * FROM users WHERE id = :userId AND isDeleted = 0")
    suspend fun getUserById(userId: Long): User?
    
    @Query("SELECT * FROM users WHERE email = :email AND isDeleted = 0")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("SELECT * FROM users WHERE isDeleted = 1")
    suspend fun getDeletedUsers(): List<User>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserByIdIncludeDeleted(userId: Long): User?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("UPDATE users SET isDeleted = 1 WHERE id = :userId")
    suspend fun softDeleteUser(userId: Long)
    
    @Query("UPDATE users SET isDeleted = 0 WHERE id = :userId")
    suspend fun restoreUser(userId: Long)
} 