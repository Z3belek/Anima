package com.aokaze.anima.presentation.screens.genres

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.presentation.common.AnimeCard
import com.aokaze.anima.presentation.common.Error
import com.aokaze.anima.presentation.common.Loading
import com.aokaze.anima.presentation.common.PosterImage
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding
import com.aokaze.anima.presentation.theme.AnimaBottomListPadding

object AnimesGenreListScreen {
    const val GenreIdBundleKey = "genreId"
}

@Composable
fun AnimesGenreListScreen(
    onBackPressed: () -> Unit,
    onAnimeSelected: (anime: Anime) -> Unit,
    viewModel: AnimesGenreListScreenViewModel = hiltViewModel()
) {
    val animePagingItems: LazyPagingItems<Anime> = viewModel.animePagingDataFlow.collectAsLazyPagingItems()
    val displayGenreName by viewModel.displayGenreName.collectAsStateWithLifecycle()

    when (animePagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }
        is LoadState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }
        is LoadState.NotLoading -> {
            CategoryDetails(
                genreNameToDisplay = displayGenreName,
                animePagingItems = animePagingItems,
                onBackPressed = onBackPressed,
                onAnimeSelected = onAnimeSelected
            )
        }
    }
}

@Composable
private fun CategoryDetails(
    genreNameToDisplay: String,
    animePagingItems: LazyPagingItems<Anime>,
    onBackPressed: () -> Unit,
    onAnimeSelected: (Anime) -> Unit,
    modifier: Modifier = Modifier
) {
    val childPadding = rememberChildPadding()
    BackHandler(onBack = onBackPressed)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = genreNameToDisplay,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(
                top = childPadding.top.times(1.5f),
                bottom = childPadding.bottom.times(1.5f)
            )
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(
                start = childPadding.start,
                end = childPadding.end,
                bottom = AnimaBottomListPadding
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = animePagingItems.itemCount,
                key = { index -> animePagingItems.peek(index)?.id ?: index }
            ) { index ->
                val anime = animePagingItems[index]
                if (anime != null) {
                    AnimeCard(
                        onClick = { onAnimeSelected(anime) },
                        modifier = Modifier
                            .aspectRatio(1 / 1.5f)
                            .padding(8.dp)
                    ) {
                        PosterImage(anime = anime, modifier = Modifier.fillMaxSize())
                    }
                } else {
                    // Placeholder = status loading
                }
            }

            item {
                if (animePagingItems.loadState.append is LoadState.Loading) {
                    Loading(modifier = Modifier.padding(vertical = 16.dp))
                }
            }
        }
    }
}