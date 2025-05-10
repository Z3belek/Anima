package com.aokaze.anima.presentation.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.presentation.common.skeleton.AnimeCardSkeleton
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding
import com.aokaze.anima.presentation.theme.AnimaBorderWidth

enum class ItemDirection(val aspectRatio: Float) {
    Vertical(10.5f / 16f),
    Horizontal(16f / 9f);
}

private const val SKELETON_ROW_ITEMS = 5

@Composable
fun AnimesRow(
    animes: List<Anime>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    onAnimeSelected: (anime: Anime) -> Unit = {}
) {
    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (title != null) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1f)
                    .padding(start = AnimaBorderWidth, top = 16.dp, bottom = 16.dp)
            )
        }
        if (isLoading && animes.isEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(
                    start = startPadding,
                    end = endPadding,
                ),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.focusRestorer(),
                userScrollEnabled = false
            ) {
                items(SKELETON_ROW_ITEMS) {
                    AnimeCardSkeleton(
                        modifier = Modifier
                            .padding(AnimaBorderWidth)
                            .then(
                                if (itemDirection == ItemDirection.Vertical) Modifier.width(150.dp) else Modifier
                            )
                            .aspectRatio(itemDirection.aspectRatio)
                    )
                }
            }
        } else if (animes.isNotEmpty()) {
            AnimatedContent(
                targetState = animes,
                label = "AnimesRowAnimation",
            ) { animeState ->
                LazyRow(
                    contentPadding = PaddingValues(
                        start = startPadding,
                        end = endPadding,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .focusRequester(lazyRow)
                        .focusRestorer(firstItem)
                ) {
                    itemsIndexed(animeState, key = { _, anime -> anime.id }) { index, anime ->
                        val itemModifier = if (index == 0) {
                            Modifier.focusRequester(firstItem).padding(AnimaBorderWidth)
                        } else {
                            Modifier.padding(AnimaBorderWidth)
                        }
                        AnimesRowItem(
                            modifier = itemModifier.then(
                                if (itemDirection == ItemDirection.Vertical) Modifier.width(150.dp) else Modifier
                            ),
                            index = index,
                            itemDirection = itemDirection,
                            onAnimeSelected = {
                                lazyRow.saveFocusedChild()
                                onAnimeSelected(it)
                            },
                            anime = anime,
                            showItemTitle = showItemTitle,
                            showIndexOverImage = showIndexOverImage
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImmersiveListAnimesRow(
    animes: List<Anime>,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    showIndexOverImage: Boolean = false,
    onAnimeSelected: (Anime) -> Unit = {},
    onAnimeFocused: (Anime) -> Unit = {}
) {
    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (title != null) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1f)
                    .padding(start = startPadding)
                    .padding(vertical = 16.dp)
            )
        }
        AnimatedContent(
            targetState = animes,
            label = "",
        ) { animeState ->
            LazyRow(
                contentPadding = PaddingValues(start = startPadding, end = endPadding),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .focusRequester(lazyRow)
                    .focusRestorer(firstItem)
            ) {
                itemsIndexed(
                    animeState,
                    key = { _, anime -> anime.id }
                ) { index, anime ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }
                    AnimesRowItem(
                        modifier = itemModifier.then(
                            if (itemDirection == ItemDirection.Vertical) Modifier.width(150.dp) else Modifier
                        ),
                        index = index,
                        itemDirection = itemDirection,
                        onAnimeSelected = {
                            lazyRow.saveFocusedChild()
                            onAnimeSelected(it)
                        },
                        onAnimeFocused = onAnimeFocused,
                        anime = anime,
                        showItemTitle = showItemTitle,
                        showIndexOverImage = showIndexOverImage
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimesRowItem(
    index: Int,
    anime: Anime,
    onAnimeSelected: (Anime) -> Unit,
    showItemTitle: Boolean,
    showIndexOverImage: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Vertical,
    onAnimeFocused: (Anime) -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }

    AnimeCard(
        onClick = { onAnimeSelected(anime) },
        title = {
            AnimesRowItemText(
                showItemTitle = showItemTitle,
                isItemFocused = isFocused,
                anime = anime
            )
        },
        modifier = Modifier
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onAnimeFocused(anime)
                }
            }
            .focusProperties {
                left = if (index == 0) {
                    FocusRequester.Cancel
                } else {
                    FocusRequester.Default
                }
            }
            .then(modifier)
    ) {
        AnimesRowItemImage(
            modifier = Modifier.aspectRatio(itemDirection.aspectRatio),
            showIndexOverImage = showIndexOverImage,
            anime = anime,
            index = index
        )
    }
}

@Composable
private fun AnimesRowItemImage(
    anime: Anime,
    showIndexOverImage: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        PosterImage(
            anime = anime,
            modifier = modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    if (showIndexOverImage) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.1f)
                        )
                    }
                },
        )
        if (showIndexOverImage) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "#${index.inc()}",
                style = MaterialTheme.typography.displayLarge
                    .copy(
                        shadow = Shadow(
                            offset = Offset(0.5f, 0.5f),
                            blurRadius = 5f
                        ),
                        color = Color.White
                    ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AnimesRowItemText(
    showItemTitle: Boolean,
    isItemFocused: Boolean,
    anime: Anime,
    modifier: Modifier = Modifier
) {
    if (showItemTitle) {
        val animeNameAlpha by animateFloatAsState(
            targetValue = if (isItemFocused) 1f else 0f,
            label = "",
        )
        Text(
            text = anime.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = modifier
                .alpha(animeNameAlpha)
                .fillMaxWidth()
                .padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
