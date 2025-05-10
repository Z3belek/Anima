package com.aokaze.anima.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aokaze.anima.data.entities.Resume
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Upsert
    suspend fun upsert(item: Resume)

    @Query("SELECT * FROM continue_watching ORDER BY lastWatchedTimestamp DESC LIMIT :limit")
    fun getResumeList(limit: Int = 20): Flow<List<Resume>>

    @Query("SELECT * FROM continue_watching WHERE episodeSlug = :episodeSlug")
    suspend fun getResumeItem(episodeSlug: String): Resume?

    @Query("DELETE FROM continue_watching WHERE episodeSlug = :episodeSlug")
    suspend fun deleteByEpisodeSlug(episodeSlug: String)

    @Query("DELETE FROM continue_watching WHERE animeSlug = :animeSlug AND episodeSlug != :currentEpisodeSlug")
    suspend fun deleteOtherEpisodesForAnime(animeSlug: String, currentEpisodeSlug: String)

    @Query("SELECT * FROM continue_watching WHERE animeSlug = :animeSlug ORDER BY lastWatchedTimestamp DESC LIMIT 1")
    suspend fun getLatestResumeForAnime(animeSlug: String): Resume?
}