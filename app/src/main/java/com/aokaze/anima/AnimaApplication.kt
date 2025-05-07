package com.aokaze.anima

import android.app.Application
import com.aokaze.anima.data.repositories.AnimeRepository
import com.aokaze.anima.data.repositories.AnimeRepositoryImpl
import com.aokaze.anima.data.repositories.EpisodeRepository
import com.aokaze.anima.data.repositories.EpisodeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class AnimaApplication : Application()

@InstallIn(SingletonComponent::class)
@Module
abstract class AnimeRepositoryModule {
    @Binds
    abstract fun bindAnimeRepository(
        animeRepositoryImpl: AnimeRepositoryImpl
    ): AnimeRepository

    @Binds
    abstract fun bindEpisodeRepository(
        episodeRepositoryImpl: EpisodeRepositoryImpl
    ): EpisodeRepository
}