package com.aokaze.anima.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aokaze.anima.data.entities.Anime
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAnimesByGenreParams(
    @SerialName("p_genre_slug") val genreSlug: String,
    @SerialName("p_page_offset") val pageOffset: Long,
    @SerialName("p_page_limit") val pageLimit: Long
)

class GenrePagingSource(
    private val postgrest: Postgrest,
    private val genreName: String
) : PagingSource<Int, Anime>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
        try {
            val currentPage = params.key ?: 0
            val pageSize = params.loadSize
            val offset = currentPage.toLong() * pageSize.toLong()

            val rpcParams = GetAnimesByGenreParams(
                genreSlug = genreName,
                pageOffset = offset,
                pageLimit = pageSize.toLong()
            )

            val response = postgrest.rpc(
                function = "get_animes_by_genre_paginated",
                parameters = rpcParams
            ).decodeList<Anime>()

            val prevKey = if (currentPage > 0) currentPage - 1 else null
            val nextKey = if (response.size == pageSize) currentPage + 1 else null

            return LoadResult.Page(data = response, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}