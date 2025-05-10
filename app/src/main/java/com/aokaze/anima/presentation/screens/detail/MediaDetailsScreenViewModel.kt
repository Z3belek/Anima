package com.aokaze.anima.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.entities.Episode
import com.aokaze.anima.data.entities.Resume
import com.aokaze.anima.data.repositories.AnimeRepository
import com.aokaze.anima.data.repositories.EpisodeRepository
import com.aokaze.anima.data.repositories.ResumeRepository
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
    private val episodeRepository: EpisodeRepository,
    private val resumeRepository: ResumeRepository
) : ViewModel() {

    private val animeIdFlow: Flow<String> = savedStateHandle
        .getStateFlow<String?>(MediaDetailsScreen.MEDIA_ID_BUNDLE_KEY, null)
        .filterNotNull()

    val uiState: Flow<MediaDetailsScreenUiState> = animeIdFlow
        .map { id ->
            try {
                coroutineScope {
                    val animeDetailsDeferred = async { animeRepository.getAnimeDetails(animeId = id) }
                    val episodesDeferred = async { episodeRepository.getEpisodesForAnime(animeSlug = id) }
                    val resumeInfoDeferred = async { resumeRepository.getLatestResumeForAnime(animeSlug = id) }
                    val animeDetails: Anime? = animeDetailsDeferred.await()
                    val episodes: List<Episode> = episodesDeferred.await()
                    val resumeInfo: Resume? = resumeInfoDeferred.await()

                    if (animeDetails != null) {
                        val firstEpisodeSlug = episodes.firstOrNull()?.id

                        MediaDetailsScreenUiState.Done(
                            anime = animeDetails,
                            episodes = episodes,
                            resumeInfo = resumeInfo,
                            firstEpisodeSlug = firstEpisodeSlug
                        )
                    } else {
                        MediaDetailsScreenUiState.Error("Anime details not found")
                    }
                }
            } catch (e: Exception) {
                MediaDetailsScreenUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }.catch { e ->
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
    data class Done(val anime: Anime, val episodes: List<Episode>, val resumeInfo: Resume? = null, val firstEpisodeSlug: String? = null) : MediaDetailsScreenUiState()
}
