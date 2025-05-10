package com.aokaze.anima.presentation.screens.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.repositories.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MovieScreenViewModel @Inject constructor(
    animeRepository: AnimeRepository
) : ViewModel() {
    val moviePagingFlow: Flow<PagingData<Anime>> = animeRepository.getMovieList()
        .cachedIn(viewModelScope)
}