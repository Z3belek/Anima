package com.aokaze.anima.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.repositories.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {
    val uiState: StateFlow<HomeScreenUiState> = combine(
        animeRepository.getFeaturedAnimeList(),
        animeRepository.getNewAnimeList(),
        animeRepository.getUpdatedAnimeList()
    ) { featuredAnimeList, newAnimeList, updatedAnimeList ->
        HomeScreenUiState.Ready(
            featuredAnimeList = featuredAnimeList,
            newAnimeList = newAnimeList,
            updatedAnimeList = updatedAnimeList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState
    data class Ready(
        val featuredAnimeList: List<Anime>,
        val newAnimeList: List<Anime>,
        val updatedAnimeList: List<Anime>
    ) : HomeScreenUiState
}