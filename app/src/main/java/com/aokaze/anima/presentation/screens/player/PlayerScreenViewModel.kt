package com.aokaze.anima.presentation.screens.player

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aokaze.anima.data.entities.Episode
import com.aokaze.anima.data.repositories.EpisodeRepository
import com.aokaze.anima.data.repositories.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val episodeRepository: EpisodeRepository,
    private val resumeRepository: ResumeRepository
) : ViewModel() {
    companion object {
        private const val PLAYBACK_START_THRESHOLD_MILLIS = 15_000L
        private const val MIN_PLAYBACK_TO_SAVE_ON_EXIT_MILLIS = 5_000L
        private const val PERIODIC_SAVE_INTERVAL_MILLIS = 15_000L
        private const val ALMOST_FINISHED_THRESHOLD_SECONDS = 45L
        private const val ALMOST_FINISHED_PERCENTAGE = 0.97
    }

    val initialSeekTimeMillis: Long = savedStateHandle[PlayerScreen.INITIAL_SEEK_TIME_MILLIS_KEY] ?: 0L

    private val _uiState = MutableStateFlow<PlayerScreenUiState>(PlayerScreenUiState.Loading)
    val uiState: StateFlow<PlayerScreenUiState> = _uiState.asStateFlow()

    private var progressUpdateJob: Job? = null
    private var currentEpisodeDurationMillisFromPlayer: Long = 0L

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>(PlayerScreen.EPISODE_ID_BUNDLE_KEY, null)
                .collectLatest { episodeSlug ->
                    _uiState.value = PlayerScreenUiState.Loading
                    if (episodeSlug == null) {
                        _uiState.value = PlayerScreenUiState.Error("Episode ID is null. Closing player.")
                        return@collectLatest
                    }

                    val currentEpisodeData = episodeRepository.getEpisodeDetails(episodeSlug = episodeSlug)
                    if (currentEpisodeData == null) {
                        _uiState.value = PlayerScreenUiState.Error("Episode not found: $episodeSlug. Closing player.")
                        return@collectLatest
                    }

                    val firstValidSourceUrl = currentEpisodeData.directSources?.firstOrNull { !it.url.isNullOrBlank() }?.url
                    if (firstValidSourceUrl == null) {
                        _uiState.value = PlayerScreenUiState.Error("No valid video source found for episode: $episodeSlug. Closing player.")
                        return@collectLatest
                    }

                    val nextEpisodeData = currentEpisodeData.nextEpisode?.takeIf { it.isNotBlank() }?.let { episodeRepository.getEpisodeDetails(it) }
                    val previousEpisodeData = currentEpisodeData.previousEpisode?.takeIf { it.isNotBlank() }?.let { episodeRepository.getEpisodeDetails(it) }

                    _uiState.value = PlayerScreenUiState.Done(
                        currentEpisode = currentEpisodeData,
                        nextEpisode = nextEpisodeData,
                        previousEpisode = previousEpisodeData,
                        selectedSourceUrl = firstValidSourceUrl
                    )
                    currentEpisodeDurationMillisFromPlayer = 0L
                }
        }
    }

    fun onPlayerReady(durationMillis: Long) {
        currentEpisodeDurationMillisFromPlayer = durationMillis.coerceAtLeast(0L)
    }

    fun updateCurrentPlayerPosition(positionMillis: Long) {
        val currentState = _uiState.value
        if (currentState !is PlayerScreenUiState.Done) return

        val episode = currentState.currentEpisode
        val totalDuration = currentEpisodeDurationMillisFromPlayer.takeIf { it > 0 } ?: return

        if (positionMillis < PLAYBACK_START_THRESHOLD_MILLIS) {
            return
        }

        if (progressUpdateJob == null || progressUpdateJob?.isCompleted == true) {
            progressUpdateJob = viewModelScope.launch {
                delay(PERIODIC_SAVE_INTERVAL_MILLIS)
                if (isActive) {
                    saveProgressInternal(positionMillis, totalDuration, episode, currentState.nextEpisode, isExiting = false)
                }
            }
        }
    }

    fun saveCurrentPlayerState(currentPositionMillis: Long, durationMillisFromPlayer: Long) {
        progressUpdateJob?.cancel()
        val currentState = _uiState.value
        if (currentState !is PlayerScreenUiState.Done) return

        val episode = currentState.currentEpisode
        val effectiveDuration = (durationMillisFromPlayer.takeIf { it > 0 }
            ?: currentEpisodeDurationMillisFromPlayer.takeIf { it > 0 }) ?: 0L

        saveProgressInternal(currentPositionMillis, effectiveDuration, episode, currentState.nextEpisode, isExiting = false)
    }

    fun playerIsClosing(currentPositionMillis: Long, durationMillisFromPlayer: Long) {
        progressUpdateJob?.cancel()
        val currentState = _uiState.value
        if (currentState !is PlayerScreenUiState.Done) return

        val episode = currentState.currentEpisode
        val effectiveDuration = (durationMillisFromPlayer.takeIf { it > 0 }
            ?: currentEpisodeDurationMillisFromPlayer.takeIf { it > 0 }) ?: 0L

        saveProgressInternal(currentPositionMillis, effectiveDuration, episode, currentState.nextEpisode, isExiting = true)
    }

    private fun saveProgressInternal(
        currentPositionMillis: Long,
        totalDurationMillis: Long,
        currentEpisode: Episode,
        nextEpisodeData: Episode?,
        isExiting: Boolean
    ) {
        val minimumPlaybackToSave = if (isExiting) MIN_PLAYBACK_TO_SAVE_ON_EXIT_MILLIS else PLAYBACK_START_THRESHOLD_MILLIS

        if (currentPositionMillis in 1 until minimumPlaybackToSave) {
            return
        }
        if (totalDurationMillis <= 0 && currentPositionMillis > 0) {
            return
        }

        viewModelScope.launch {
            val animeSlug = currentEpisode.animeSlug ?: return@launch
            val animeTitle = currentEpisode.animeTitle ?: ""

            val progressPercentage = if (totalDurationMillis > 0) currentPositionMillis.toDouble() / totalDurationMillis.toDouble() else 0.0
            val remainingMillis = if (totalDurationMillis > 0) totalDurationMillis - currentPositionMillis else Long.MAX_VALUE
            val isAlmostFinished = !isExiting && totalDurationMillis > 0 &&
                    currentPositionMillis >= PLAYBACK_START_THRESHOLD_MILLIS &&
                    (remainingMillis < ALMOST_FINISHED_THRESHOLD_SECONDS * 1000 || progressPercentage > ALMOST_FINISHED_PERCENTAGE)

            if (isAlmostFinished) {
                resumeRepository.removeProgress(currentEpisode.id)

                if (nextEpisodeData != null) {
                    resumeRepository.saveProgress(
                        episodeSlug = nextEpisodeData.id,
                        animeSlug = nextEpisodeData.animeSlug ?: animeSlug,
                        animeTitle = nextEpisodeData.animeTitle ?: animeTitle,
                        episodeTitle = nextEpisodeData.title ?: "",
                        episodeNumber = nextEpisodeData.number?.toInt() ?: 0,
                        episodeThumbnailUrl = nextEpisodeData.thumbnail,
                        currentPositionMillis = 0L,
                        durationMillis = 1L
                    )
                }
            } else {
                resumeRepository.saveProgress(
                    episodeSlug = currentEpisode.id,
                    animeSlug = animeSlug,
                    animeTitle = animeTitle,
                    episodeTitle = currentEpisode.title ?: "",
                    episodeNumber = currentEpisode.number?.toInt() ?: 0,
                    episodeThumbnailUrl = currentEpisode.thumbnail,
                    currentPositionMillis = currentPositionMillis,
                    durationMillis = totalDurationMillis.coerceAtLeast(1L)
                )
            }
        }
    }

    fun loadEpisodeBySlug(slug: String, currentPlayPositionBeforeSwitch: Long, currentDurationBeforeSwitch: Long) {
        val currentUiState = _uiState.value
        if (currentUiState is PlayerScreenUiState.Done) {
            playerIsClosing(currentPlayPositionBeforeSwitch, currentDurationBeforeSwitch)
        }

        if (savedStateHandle.get<String>(PlayerScreen.EPISODE_ID_BUNDLE_KEY) != slug) {
            savedStateHandle[PlayerScreen.EPISODE_ID_BUNDLE_KEY] = slug
            savedStateHandle[PlayerScreen.INITIAL_SEEK_TIME_MILLIS_KEY] = 0L
        }
    }

    fun selectVideoSource(sourceUrl: String, currentPlayPositionBeforeSwitch: Long, currentDurationBeforeSwitch: Long) {
        val currentUiState = _uiState.value
        if (currentUiState is PlayerScreenUiState.Done) {
            saveCurrentPlayerState(currentPlayPositionBeforeSwitch, currentDurationBeforeSwitch)
        }

        _uiState.update { currentState ->
            if (currentState is PlayerScreenUiState.Done) {
                if (currentState.selectedSourceUrl != sourceUrl) {
                    savedStateHandle[PlayerScreen.INITIAL_SEEK_TIME_MILLIS_KEY] = currentPlayPositionBeforeSwitch
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