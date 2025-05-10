package com.aokaze.anima.di

import android.content.Context
import androidx.room.Room
import com.aokaze.anima.data.dao.ResumeDao
import com.aokaze.anima.data.local.AnimaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAnimaDatabase(@ApplicationContext context: Context): AnimaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AnimaDatabase::class.java,
            "anima_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideResumeDao(database: AnimaDatabase): ResumeDao {
        return database.resumeDao()
    }
}