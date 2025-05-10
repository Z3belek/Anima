package com.aokaze.anima.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.presentation.common.skeleton.AnimeCardSkeleton
import com.aokaze.anima.presentation.theme.AnimaBottomListPadding

private const val SKELETON_ROWS = 3

@Composable
fun AnimeGrid(
    items: LazyPagingItems<Anime>,
    onAnimeClick: (anime: Anime) -> Unit,
    modifier: Modifier = Modifier,
    gridColumns: Int = 6,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    header: (@Composable () -> Unit)? = null
) {
    val lazyGridState = rememberLazyGridState()
    val loadState = items.loadState
    val skeletonItemCount = gridColumns * SKELETON_ROWS

    when {
        loadState.refresh is LoadState.Loading && items.itemCount == 0 -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                contentPadding = PaddingValues(bottom = AnimaBottomListPadding),
                modifier = modifier,
                userScrollEnabled = false,
                verticalArrangement = verticalArrangement,
                horizontalArrangement = horizontalArrangement
            ) {
                if (header != null) {
                    item(span = { GridItemSpan(gridColumns) }) {
                        header()
                    }
                }
                items(count = skeletonItemCount) {
                    AnimeCardSkeleton(modifier = Modifier.aspectRatio(1f / 1.5f))
                }
            }
        }
        loadState.refresh is LoadState.Error && items.itemCount == 0 -> {
            val error = (loadState.refresh as LoadState.Error).error
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ErrorMessageWithRetry(
                    error = error,
                    onRetry = { items.retry() }
                )
            }
        }
        else -> {
            LazyVerticalGrid(
                state = lazyGridState,
                columns = GridCells.Fixed(gridColumns),
                contentPadding = PaddingValues(bottom = AnimaBottomListPadding),
                modifier = modifier,
                verticalArrangement = verticalArrangement,
                horizontalArrangement = horizontalArrangement
            ) {
                if (header != null) {
                    item(span = { GridItemSpan(gridColumns) }) {
                        header()
                    }
                }
                items(
                    count = items.itemCount,
                    key = items.itemKey { anime -> anime.id }
                ) { index ->
                    val anime = items[index]
                    if (anime != null) {
                        AnimeCard(
                            onClick = { onAnimeClick(anime) },
                            modifier = Modifier.aspectRatio(1f / 1.5f)
                        ) {
                            PosterImage(modifier = Modifier.fillMaxSize(), anime = anime)
                        }
                    } else {
                        AnimeCardSkeleton(modifier = Modifier.aspectRatio(1f / 1.5f))
                    }
                }

                if (loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(gridColumns) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                if (loadState.append is LoadState.Error) {
                    item(span = { GridItemSpan(gridColumns) }) {
                        val error = (loadState.append as LoadState.Error).error
                        ErrorMessageWithRetry(
                            error = error,
                            onRetry = { items.retry() },
                            baseMessage = "Error al cargar más: ",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorMessageWithRetry(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    baseMessage: String = "Error al cargar: "
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$baseMessage${error.localizedMessage ?: "Error desconocido"}",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}