package com.aokaze.anima.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.paging.AnimePagingSource
import com.aokaze.anima.data.paging.GenrePagingSource
import com.aokaze.anima.data.paging.MoviePagingSource
import com.aokaze.anima.data.paging.SearchPagingSource
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

private const val ANIME_PAGE_SIZE = 20
private const val FEATURED_ANIMES_LIMIT = 5

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : AnimeRepository {

    override fun getAnimeList(): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = ANIME_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { AnimePagingSource(postgrest) }
        ).flow
    }

    override fun getMovieList(): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = ANIME_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { MoviePagingSource(postgrest) }
        ).flow
    }

    override suspend fun getAnimeDetails(animeId: String): Anime? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("animes")
                .select { filter { eq("slug", animeId) } }
                .decodeSingleOrNull<Anime>()
        } catch (e: Exception) {
            println("Error fetching anime details for $animeId: ${e.message}")
            null
        }
    }

    override fun searchAnime(query: String): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = ANIME_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(postgrest, query) }
        ).flow
    }

    override fun getAnimeByGenre(genre: String): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = ANIME_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { GenrePagingSource(postgrest, genre) }
        ).flow
    }

    override fun getFeaturedAnimeList(): Flow<List<Anime>> = flow {
        try {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            val animes = postgrest.from("animes")
                .select {
                    filter {eq("year", currentYear)}
                    order("score", Order.DESCENDING)
                    limit(FEATURED_ANIMES_LIMIT.toLong())
                }
                .decodeList<Anime>()
            emit(animes)
        } catch (e: Exception) {
            println("Error fetching featured animes: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    override fun getNewAnimeList(): Flow<List<Anime>> = flow {
        try {
            val animes = postgrest.from("animes")
                .select {
                    order("created_at", Order.DESCENDING)
                    limit(ANIME_PAGE_SIZE.toLong())
                }
                .decodeList<Anime>()
            emit(animes)
        } catch (e: Exception) {
            println("Error fetching new animes: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    override fun getUpdatedAnimeList(): Flow<List<Anime>> = flow {
        try {
            val animes = postgrest.from("animes")
                .select {
                    order("created_at", Order.DESCENDING)
                    limit(ANIME_PAGE_SIZE.toLong())
                }
                .decodeList<Anime>()
            emit(animes)
        } catch (e: Exception) {
            println("Error fetching new animes: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
}