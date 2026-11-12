package com.example.recetario.data.repository

import com.example.recetario.data.dao.UserDao
import com.example.recetario.data.entity.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getAllUsers(): List<User> = userDao.getAllUsers()
    
    suspend fun getDeletedUsers(): List<User> = userDao.getDeletedUsers()
    
    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)
    
    suspend fun getUserByIdIncludeDeleted(userId: Long): User? = userDao.getUserByIdIncludeDeleted(userId)
    
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    
    suspend fun createUser(user: User): Long = userDao.insertUser(user)
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    
    suspend fun softDeleteUser(userId: Long) = userDao.softDeleteUser(userId)
    
    suspend fun restoreUser(userId: Long) = userDao.restoreUser(userId)
    
    suspend fun createUserIfNotExists(name: String, email: String): User {
        val existingUser = getUserByEmail(email)
        return if (existingUser != null) {
            existingUser
        } else {
            val newUser = User(name = name, email = email)
            val userId = createUser(newUser)
            newUser.copy(id = userId)
        }
    }
    
    suspend fun createUserWithPassword(name: String, email: String, password: String): User {
        val newUser = User(name = name, email = email, password = password)
        val userId = createUser(newUser)
        return newUser.copy(id = userId)
    }
    
    suspend fun authenticateUser(email: String, password: String): User? {
        val user = getUserByEmail(email)
        return if (user != null && user.password == password) {
            user
        } else {
            null
        }
    }
    
    suspend fun authenticateAdmin(email: String, password: String): User? {
        val user = getUserByEmail(email)
        return if (user != null && user.password == password && user.isAdmin) {
            user
        } else {
            null
        }
    }
    
    suspend fun createAdminUserIfNotExists(): User {
        val adminEmail = "admin@recetario.com"
        val existingAdmin = getUserByEmail(adminEmail)
        return if (existingAdmin != null) {
            existingAdmin
        } else {
            val adminUser = User(
                name = "Administrador",
                email = adminEmail,
                password = "admin123",
                isAdmin = true
            )
            val userId = createUser(adminUser)
            adminUser.copy(id = userId)
        }
    }
} 