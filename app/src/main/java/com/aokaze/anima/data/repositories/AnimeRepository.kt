package com.aokaze.anima.data.repositories

import androidx.paging.PagingData
import com.aokaze.anima.data.entities.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun getAnimeList(): Flow<PagingData<Anime>>
    fun getMovieList(): Flow<PagingData<Anime>>
    suspend fun getAnimeDetails(animeId: String): Anime?
    fun searchAnime(query: String): Flow<PagingData<Anime>>
    fun getAnimeByGenre(genre: String): Flow<PagingData<Anime>>
    fun getFeaturedAnimeList(): Flow<List<Anime>>
    fun getNewAnimeList(): Flow<List<Anime>>
    fun getUpdatedAnimeList(): Flow<List<Anime>>
}