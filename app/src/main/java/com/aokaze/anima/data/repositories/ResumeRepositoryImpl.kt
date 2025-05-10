package com.aokaze.anima.data.repositories

import com.aokaze.anima.data.dao.ResumeDao
import com.aokaze.anima.data.entities.Resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResumeRepositoryImpl @Inject constructor(
    private val dao: ResumeDao
) : ResumeRepository {

    companion object {
        private const val MIN_PROGRESS_PERCENTAGE_TO_CONSIDER_FINISHED = 0.95
        private const val MIN_TIME_LEFT_TO_CONSIDER_FINISHED_MS = 60_000L
    }

    override suspend fun saveProgress(
        episodeSlug: String,
        animeSlug: String,
        animeTitle: String,
        episodeTitle: String,
        episodeNumber: Int,
        episodeThumbnailUrl: String?,
        currentPositionMillis: Long,
        durationMillis: Long
    ) = withContext(Dispatchers.IO) {

        if (durationMillis <= 0) return@withContext

        dao.deleteOtherEpisodesForAnime(animeSlug, episodeSlug)

        val progressPercentage = currentPositionMillis.toDouble() / durationMillis.toDouble()

        val isAlmostFinished = progressPercentage > MIN_PROGRESS_PERCENTAGE_TO_CONSIDER_FINISHED ||
                (durationMillis - currentPositionMillis) < MIN_TIME_LEFT_TO_CONSIDER_FINISHED_MS

        if (isAlmostFinished) {
            dao.deleteByEpisodeSlug(episodeSlug)
            return@withContext
        }

        val item = Resume(
            episodeSlug = episodeSlug,
            animeSlug = animeSlug,
            animeTitle = animeTitle,
            episodeTitle = episodeTitle,
            episodeNumber = episodeNumber,
            episodeThumbnailUrl = episodeThumbnailUrl,
            currentPositionMillis = currentPositionMillis,
            durationMillis = durationMillis,
            lastWatchedTimestamp = System.currentTimeMillis()
        )
        dao.upsert(item)
    }

    override fun getResumeList(): Flow<List<Resume>> {
        return dao.getResumeList()
    }

    override suspend fun getSavedProgress(episodeSlug: String): Resume? = withContext(Dispatchers.IO) {
        return@withContext dao.getResumeItem(episodeSlug)
    }

    override suspend fun removeProgress(episodeSlug: String) = withContext(Dispatchers.IO) {
        dao.deleteByEpisodeSlug(episodeSlug)
    }

    override suspend fun getLatestResumeForAnime(animeSlug: String): Resume? = withContext(Dispatchers.IO) {
        return@withContext dao.getLatestResumeForAnime(animeSlug)
    }
}