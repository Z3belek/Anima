package com.aokaze.anima.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.repositories.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val animeRepository: AnimeRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Empty)
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    @OptIn(kotlinx.coroutines.FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<Anime>> = searchQuery
        .debounce(3000)
        .filter { query ->
            if (query.isBlank()) {
                _searchState.value = SearchUiState.Empty
                return@filter false
            }
            return@filter true
        }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            _searchState.value = SearchUiState.Searching
            animeRepository.searchAnime(query)
                .map { pagingData ->
                    _searchState.value = SearchUiState.Success
                    pagingData
                }
                .onStart {}
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchState.value = SearchUiState.Empty
        }
    }
}

sealed interface SearchUiState {
    data object Empty : SearchUiState
    data object Searching : SearchUiState
    data object Success : SearchUiState
}