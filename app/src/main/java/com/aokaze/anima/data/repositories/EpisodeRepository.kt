package com.aokaze.anima.data.repositories

import com.aokaze.anima.data.entities.Episode

interface EpisodeRepository {
    suspend fun getEpisodesForAnime(animeSlug: String): List<Episode>
    suspend fun getEpisodeDetails(episodeSlug: String): Episode?
}