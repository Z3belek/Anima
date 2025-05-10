package com.aokaze.anima.presentation.screens.genres

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.presentation.common.AnimeGrid
import com.aokaze.anima.presentation.common.Error
import com.aokaze.anima.presentation.screens.dashboard.closeDrawerWidth

object AnimesGenreListScreen {
    const val GENRE_ID_BUNDLE_KEY = "genreId"
}

@Composable
fun AnimesGenreListScreen(
    onBackPressed: () -> Unit,
    onAnimeSelected: (anime: Anime) -> Unit,
    viewModel: AnimesGenreListScreenViewModel = hiltViewModel()
) {
    val animePagingItems: LazyPagingItems<Anime> = viewModel.animePagingDataFlow.collectAsLazyPagingItems()
    val displayGenreName by viewModel.displayGenreName.collectAsStateWithLifecycle()

    BackHandler(onBack = onBackPressed)

    val refreshLoadState = animePagingItems.loadState.refresh

    if (refreshLoadState is LoadState.Error && animePagingItems.itemCount == 0) {
        Error(modifier = Modifier.fillMaxSize())
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, start = closeDrawerWidth, end = closeDrawerWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = displayGenreName,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        )

        AnimeGrid(
            items = animePagingItems,
            onAnimeClick = onAnimeSelected,
            modifier = Modifier.fillMaxSize()
        )
    }
}