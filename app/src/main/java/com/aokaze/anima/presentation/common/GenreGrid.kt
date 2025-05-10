package com.aokaze.anima.presentation.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.data.entities.Genre
import com.aokaze.anima.presentation.common.skeleton.GenreCardSkeleton
import com.aokaze.anima.presentation.theme.AnimaBottomListPadding
import com.aokaze.anima.presentation.utils.GradientBg

private const val SKELETON_GENRE_ROWS = 3

@Composable
fun GenreGrid(
    genreList: List<Genre>,
    isLoading: Boolean,
    onGenreClick: (genreName: String) -> Unit,
    modifier: Modifier = Modifier,
    gridColumns: Int = 4,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(0.dp)
) {
    val lazyGridState = rememberLazyGridState()
    val skeletonItemCount = gridColumns * SKELETON_GENRE_ROWS

    val finalContentPadding = PaddingValues(
        start = contentPadding.calculateLeftPadding(LocalLayoutDirection.current),
        top = contentPadding.calculateTopPadding(),
        end = contentPadding.calculateRightPadding(LocalLayoutDirection.current),
        bottom = AnimaBottomListPadding
    )

    if (isLoading && genreList.isEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            modifier = modifier.fillMaxSize(),
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            userScrollEnabled = false,
            contentPadding = finalContentPadding,
        ) {
            items(count = skeletonItemCount) {
                GenreCardSkeleton(
                    modifier = Modifier
                        .padding(8.dp)
                        .aspectRatio(16 / 9f)
                )
            }
        }
    } else {
        AnimatedContent(
            targetState = genreList,
            modifier = modifier.fillMaxSize(),
            label = "GenreListAnimation",
        ) { currentGenreList ->
            if (currentGenreList.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No hay géneros disponibles.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Fixed(gridColumns),
                    verticalArrangement = verticalArrangement,
                    horizontalArrangement = horizontalArrangement,
                    contentPadding = finalContentPadding
                ) {
                    items(
                        items = currentGenreList,
                        key = { genre -> genre.slug }
                    ) { genre ->
                        var isFocused by remember { mutableStateOf(false) }
                        val index = currentGenreList.indexOf(genre)

                        AnimeCard(
                            onClick = {
                                onGenreClick(genre.slug)
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .aspectRatio(16 / 9f)
                                .onFocusChanged {
                                    isFocused = it.isFocused || it.hasFocus
                                }
                                .focusProperties {
                                    if (index != -1 && index % gridColumns == 0) {
                                        left = FocusRequester.Cancel
                                    }
                                },
                        ) {
                            val itemAlpha by animateFloatAsState(
                                targetValue = if (isFocused) 0.6f else 0.2f,
                                label = "GenreItemAlphaAnimation"
                            )
                            val textColor = if (isFocused) Color.White else Color.White

                            Box(contentAlignment = Alignment.Center) {
                                Box(modifier = Modifier.alpha(itemAlpha)) {
                                    GradientBg()
                                }
                                Text(
                                    text = genre.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = textColor,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}