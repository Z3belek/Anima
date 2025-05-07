package com.aokaze.anima.presentation.screens.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.entities.Episode
import com.aokaze.anima.data.repositories.AnimeRepository
import com.aokaze.anima.data.repositories.EpisodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MediaDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animeRepository: AnimeRepository,
    private val episodeRepository: EpisodeRepository
) : ViewModel() {

    private val animeIdFlow: Flow<String> = savedStateHandle
        .getStateFlow<String?>(MediaDetailsScreen.MediaIdBundleKey, null)
        .filterNotNull()

    val uiState: Flow<MediaDetailsScreenUiState> = animeIdFlow
        .map { id ->
            Log.d("MediaDetailsVM", "Fetching details for ID (slug): $id")
            try {
                coroutineScope {
                    val animeDetailsDeferred = async { animeRepository.getAnimeDetails(animeId = id) }
                    val episodesDeferred = async { episodeRepository.getEpisodesForAnime(animeSlug = id) }

                    val animeDetails: Anime? = animeDetailsDeferred.await()
                    val episodes: List<Episode> = episodesDeferred.await()

                    if (animeDetails != null) {
                        Log.d("MediaDetailsVM", "Success fetching details for ID: $id. Episodes found: ${episodes.size}")
                        MediaDetailsScreenUiState.Done(anime = animeDetails, episodes = episodes)
                    } else {
                        Log.e("MediaDetailsVM", "Failed to fetch Anime details for ID: $id")
                        MediaDetailsScreenUiState.Error("Anime details not found")
                    }
                }
            } catch (e: Exception) {
                Log.e("MediaDetailsVM", "Error fetching details for ID: $id", e)
                MediaDetailsScreenUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }.catch { e ->
            Log.e("MediaDetailsVM", "Error in UI state flow", e)
            emit(MediaDetailsScreenUiState.Error(e.localizedMessage ?: "Flow error"))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MediaDetailsScreenUiState.Loading
        )
}

sealed class MediaDetailsScreenUiState {
    data object Loading : MediaDetailsScreenUiState()
    data class Error(val message: String) : MediaDetailsScreenUiState()
    data class Done(val anime: Anime, val episodes: List<Episode>) : MediaDetailsScreenUiState()
}
