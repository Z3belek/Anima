package com.aokaze.anima.presentation.screens.player

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aokaze.anima.data.entities.Episode
import com.aokaze.anima.data.repositories.EpisodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val episodeRepository: EpisodeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<PlayerScreenUiState>(PlayerScreenUiState.Loading)
    val uiState: StateFlow<PlayerScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>(PlayerScreen.EpisodeIdBundleKey, null)
                .collectLatest { episodeSlug ->
                    _uiState.value = PlayerScreenUiState.Loading
                    if (episodeSlug == null) {
                        _uiState.value = PlayerScreenUiState.Error("Episode ID is null")
                        return@collectLatest
                    }

                    val currentEpisode = episodeRepository.getEpisodeDetails(episodeSlug = episodeSlug)
                    if (currentEpisode == null) {
                        _uiState.value = PlayerScreenUiState.Error("Episode not found: $episodeSlug")
                        return@collectLatest
                    }

                    val firstValidSourceUrl = currentEpisode.directSources?.firstOrNull { !it.url.isNullOrBlank() }?.url
                    if (firstValidSourceUrl == null) {
                        _uiState.value = PlayerScreenUiState.Error("No valid video source found for episode: $episodeSlug. Closing player.")
                        return@collectLatest
                    }

                    val nextEpisodeSlug = currentEpisode.nextEpisode?.takeIf { it.isNotBlank() }
                    val previousEpisodeSlug = currentEpisode.previousEpisode?.takeIf { it.isNotBlank() }

                    val nextEpisode = nextEpisodeSlug?.let {
                        episodeRepository.getEpisodeDetails(it)
                    }
                    val previousEpisode = previousEpisodeSlug?.let {
                        episodeRepository.getEpisodeDetails(it)
                    }

                    _uiState.value = PlayerScreenUiState.Done(
                        currentEpisode = currentEpisode,
                        nextEpisode = nextEpisode,
                        previousEpisode = previousEpisode,
                        selectedSourceUrl = firstValidSourceUrl
                    )
                }
        }
    }

    fun loadEpisodeBySlug(slug: String) {
        if (savedStateHandle.get<String>(PlayerScreen.EpisodeIdBundleKey) != slug) {
            savedStateHandle[PlayerScreen.EpisodeIdBundleKey] = slug
        } else {
            Log.d("VideoPlayerVM", "loadEpisodeBySlug: Slug is the same as current, no change.")
        }
    }

    fun selectVideoSource(sourceUrl: String) {
        _uiState.update { currentState ->
            if (currentState is PlayerScreenUiState.Done) {
                if (currentState.selectedSourceUrl != sourceUrl) {
                    currentState.copy(selectedSourceUrl = sourceUrl)
                } else {
                    currentState
                }
            } else {
                currentState
            }
        }
    }
}

@Immutable
sealed class PlayerScreenUiState {
    data object Loading : PlayerScreenUiState()
    data class Error(val message: String? = null) : PlayerScreenUiState()
    data class Done(
        val currentEpisode: Episode,
        val nextEpisode: Episode?,
        val previousEpisode: Episode?,
        val selectedSourceUrl: String
    ) : PlayerScreenUiState()
}