package com.aokaze.anima.presentation.screens.genres

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.entities.Genre
import com.aokaze.anima.data.repositories.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AnimesGenreListScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    animeRepository: AnimeRepository,
    application: Application
) : ViewModel() {

    private val genreId: StateFlow<String> = savedStateHandle.getStateFlow(AnimesGenreListScreen.GENRE_ID_BUNDLE_KEY, "")

    private val allGenres: List<Genre> by lazy {
        try {
            val jsonString = application.assets.open("genres.json")
                .bufferedReader()
                .use { it.readText() }
            Json.decodeFromString<List<Genre>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private val genreIdMap: Map<String, String> by lazy {
        allGenres.associate { it.slug to it.name }
    }

    val displayGenreName: StateFlow<String> = genreId
        .map { slug ->
            genreIdMap[slug] ?: slug.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val animePagingDataFlow: Flow<PagingData<Anime>> = genreId
        .filter { it.isNotBlank() }
        .flatMapLatest { slug ->
            animeRepository.getAnimeByGenre(slug)
        }
        .cachedIn(viewModelScope)
}