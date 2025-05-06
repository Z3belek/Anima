package com.aokaze.anima

import android.app.Application
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class AnimaApplication : Application()

@InstallIn(SingletonComponent::class)
@Module
abstract class AnimeRepositoryModule {}