package com.example.recetario.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.recetario.data.dao.RecipeDao
import com.example.recetario.data.dao.UserDao
import com.example.recetario.data.dao.UserActivityDao
import com.example.recetario.data.entity.Recipe
import com.example.recetario.data.entity.User
import com.example.recetario.data.entity.UserActivity

@Database(
    entities = [User::class, Recipe::class, UserActivity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
    abstract fun userActivityDao(): UserActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recetario_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 