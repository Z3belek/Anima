package com.aokaze.anima.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.entities.Resume
import com.aokaze.anima.data.repositories.AnimeRepository
import com.aokaze.anima.data.repositories.EpisodeRepository
import com.aokaze.anima.data.repositories.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    animeRepository: AnimeRepository,
    resumeRepository: ResumeRepository,
    private val episodeRepository: EpisodeRepository
) : ViewModel() {
    val uiState: StateFlow<HomeScreenUiState> = combine(
        animeRepository.getFeaturedAnimeList(),
        resumeRepository.getResumeList(),
        animeRepository.getNewAnimeList(),
        animeRepository.getUpdatedAnimeList()
    ) { featuredAnimeList, resumeList, newAnimeList, updatedAnimeList ->
        HomeScreenUiState.Ready(
            featuredAnimeList = featuredAnimeList,
            resumeList = resumeList,
            newAnimeList = newAnimeList,
            updatedAnimeList = updatedAnimeList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeScreenUiState.Loading
    )
    suspend fun getFirstEpisodeSlug(animeId: String): String? {
        return try {
            episodeRepository.getEpisodesForAnime(animeId).firstOrNull()?.id
        } catch (e: Exception) {
            null
        }
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState
    data class Ready(
        val featuredAnimeList: List<Anime>,
        val resumeList: List<Resume>,
        val newAnimeList: List<Anime>,
        val updatedAnimeList: List<Anime>
    ) : HomeScreenUiState
}