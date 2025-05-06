package com.aokaze.anima.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aokaze.anima.data.entities.Anime
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order

class MoviePagingSource(
    private val postgrest: Postgrest
) : PagingSource<Int, Anime>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
        try {
            val currentPage = params.key ?: 0
            val pageSize = params.loadSize

            val rangeFrom = currentPage.toLong() * pageSize.toLong()
            val rangeTo = rangeFrom + pageSize.toLong() - 1

            val response = postgrest.from("animes")
                .select {
                    filter{eq("type", "Movie")}
                    range(rangeFrom, rangeTo)
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<Anime>()

            val prevKey = if (currentPage > 0) currentPage - 1 else null
            val nextKey = if (response.size == pageSize) currentPage + 1 else null

            return LoadResult.Page(data = response, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            println("Error in MoviePagingSource: ${e.message}")
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