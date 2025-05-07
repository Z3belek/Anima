package com.aokaze.anima.data.repositories

import com.aokaze.anima.data.entities.Episode
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : EpisodeRepository {

    override suspend fun getEpisodesForAnime(animeSlug: String): List<Episode> = withContext(
        Dispatchers.IO) {
        try {
            postgrest.from("episodes")
                .select {
                    filter { eq("anime_slug", animeSlug) }
                    order("episode_number", Order.ASCENDING)
                }
                .decodeList<Episode>()
        } catch (e: Exception) {
            println("Error fetching episodes for $animeSlug: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getEpisodeDetails(episodeSlug: String): Episode? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("episodes")
                .select { filter { eq("slug", episodeSlug) } }
                .decodeSingleOrNull<Episode>()
        } catch (e: Exception) {
            println("Error fetching episode details for $episodeSlug: ${e.message}")
            null
        }
    }
}