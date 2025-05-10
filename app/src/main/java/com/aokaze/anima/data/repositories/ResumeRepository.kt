package com.aokaze.anima.data.repositories

import com.aokaze.anima.data.entities.Resume
import kotlinx.coroutines.flow.Flow

interface ResumeRepository {
    suspend fun saveProgress(
        episodeSlug: String,
        animeSlug: String,
        animeTitle: String,
        episodeTitle: String,
        episodeNumber: Int,
        episodeThumbnailUrl: String?,
        currentPositionMillis: Long,
        durationMillis: Long
    )

    fun getResumeList(): Flow<List<Resume>>
    suspend fun getSavedProgress(episodeSlug: String): Resume?
    suspend fun removeProgress(episodeSlug: String)
    suspend fun getLatestResumeForAnime(animeSlug: String): Resume?
}