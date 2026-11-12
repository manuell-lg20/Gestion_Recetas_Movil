package com.example.recetario.data.repository

import com.example.recetario.data.dao.UserActivityDao
import com.example.recetario.data.entity.UserActivity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserActivityRepository @Inject constructor(
    private val userActivityDao: UserActivityDao
) {
    fun getUserActivities(userId: Long): Flow<List<UserActivity>> = userActivityDao.getUserActivities(userId)
    
    fun getAllActivities(): Flow<List<UserActivity>> = userActivityDao.getAllActivities()
    
    fun getUserActivitiesByType(userId: Long, activityType: String): Flow<List<UserActivity>> = 
        userActivityDao.getUserActivitiesByType(userId, activityType)
    
    suspend fun logActivity(
        userId: Long,
        activityType: String,
        description: String,
        recipeId: Long? = null,
        oldValue: String? = null,
        newValue: String? = null
    ) {
        val activity = UserActivity(
            userId = userId,
            activityType = activityType,
            description = description,
            recipeId = recipeId,
            oldValue = oldValue,
            newValue = newValue
        )
        userActivityDao.insertActivity(activity)
    }
    
    suspend fun deleteActivity(activity: UserActivity) = userActivityDao.deleteActivity(activity)
    
    suspend fun deleteUserActivities(userId: Long) = userActivityDao.deleteUserActivities(userId)
    
    suspend fun deleteUserActivitiesByType(userId: Long, activityTypes: List<String>) = 
        userActivityDao.deleteUserActivitiesByType(userId, activityTypes)
} 