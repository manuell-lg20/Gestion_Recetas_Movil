package com.example.recetario.di

import android.content.Context
import com.example.recetario.data.AppDatabase
import com.example.recetario.data.dao.RecipeDao
import com.example.recetario.data.dao.UserDao
import com.example.recetario.data.dao.UserActivityDao
import com.example.recetario.data.repository.RecipeRepository
import com.example.recetario.data.repository.UserRepository
import com.example.recetario.data.repository.UserActivityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: AppDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    @Singleton
    fun provideUserActivityDao(database: AppDatabase): UserActivityDao {
        return database.userActivityDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(recipeDao: RecipeDao): RecipeRepository {
        return RecipeRepository(recipeDao)
    }

    @Provides
    @Singleton
    fun provideUserActivityRepository(userActivityDao: UserActivityDao): UserActivityRepository {
        return UserActivityRepository(userActivityDao)
    }
} 