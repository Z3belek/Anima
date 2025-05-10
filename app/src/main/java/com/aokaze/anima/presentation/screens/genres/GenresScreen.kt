package com.aokaze.anima.presentation.screens.genres


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.R
import com.aokaze.anima.presentation.common.GenreGrid
import com.aokaze.anima.presentation.screens.dashboard.closeDrawerWidth

@Composable
fun GenresScreen(
    gridColumns: Int = 4,
    onGenreClick: (genreName: String) -> Unit,
    viewModel: GenresScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, end = closeDrawerWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.genres_screen_title),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        )

        val isLoading = uiState is GenresScreenUiState.Loading
        val genreList = if (uiState is GenresScreenUiState.Ready) {
            (uiState as GenresScreenUiState.Ready).genreList
        } else {
            emptyList()
        }

        GenreGrid(
            genreList = genreList,
            isLoading = isLoading,
            onGenreClick = onGenreClick,
            gridColumns = gridColumns,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp)
        )
    }
}