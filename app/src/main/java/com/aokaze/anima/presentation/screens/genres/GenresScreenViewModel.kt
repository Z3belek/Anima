package com.aokaze.anima.presentation.screens.genres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aokaze.anima.data.entities.Genre
import com.aokaze.anima.data.repositories.CachedDataReader
import com.aokaze.anima.data.repositories.readGenreData
import com.aokaze.anima.data.util.AssetsReader
import com.aokaze.anima.data.util.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GenresScreenViewModel @Inject constructor(
    private val assetsReader: AssetsReader
) : ViewModel() {

    private val genreDataReader = CachedDataReader {
        readGenreData(assetsReader, StringConstants.Assets.Genres)
    }

    val uiState: StateFlow<GenresScreenUiState> = flow {
        val genres = genreDataReader.read()
        emit(genres)
    }.map { genres ->
        GenresScreenUiState.Ready(genreList = genres)
    }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GenresScreenUiState.Loading
        )
}

sealed interface GenresScreenUiState {
    data object Loading : GenresScreenUiState
    data class Ready(val genreList: List<Genre>) : GenresScreenUiState
}