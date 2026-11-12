package com.example.recetario.data.dao

import androidx.room.*
import com.example.recetario.data.entity.UserActivity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserActivityDao {
    @Query("SELECT * FROM user_activities WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserActivities(userId: Long): Flow<List<UserActivity>>
    
    @Query("SELECT * FROM user_activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<UserActivity>>
    
    @Query("SELECT * FROM user_activities WHERE userId = :userId AND activityType = :activityType ORDER BY timestamp DESC")
    fun getUserActivitiesByType(userId: Long, activityType: String): Flow<List<UserActivity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: UserActivity): Long
    
    @Delete
    suspend fun deleteActivity(activity: UserActivity)
    
    @Query("DELETE FROM user_activities WHERE userId = :userId")
    suspend fun deleteUserActivities(userId: Long)
    
    @Query("DELETE FROM user_activities WHERE userId = :userId AND activityType IN (:activityTypes)")
    suspend fun deleteUserActivitiesByType(userId: Long, activityTypes: List<String>)
} 