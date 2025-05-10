package com.aokaze.anima.di

import com.aokaze.anima.data.repositories.ResumeRepository
import com.aokaze.anima.data.repositories.ResumeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindResumeRepository(
        resumeRepositoryImpl: ResumeRepositoryImpl
    ): ResumeRepository
}