package com.aokaze.anima.presentation.screens.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aokaze.anima.R
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.entities.Episode
import com.aokaze.anima.presentation.common.Loading
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding
import com.aokaze.anima.presentation.theme.AnimaBorderWidth
import com.aokaze.anima.presentation.theme.AnimaCardShape

object MediaDetailsScreen {
    const val MediaIdBundleKey = "mediaId"
}

@Composable
fun MediaDetailsScreen(
    onEpisodeSelected: (episodeId: String) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: MediaDetailsScreenViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        initialValue = MediaDetailsScreenUiState.Loading,
        lifecycle = lifecycleOwner.lifecycle
    )

    when (val s = uiState) {
        is MediaDetailsScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is MediaDetailsScreenUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onBackPressed) { Text("Volver") }
                }
            }
        }

        is MediaDetailsScreenUiState.Done -> {
            Details(
                anime = s.anime,
                episodes = s.episodes,
                onEpisodeSelected = onEpisodeSelected,
                onBackPressed = onBackPressed,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            )
        }
    }
}

@Composable
private fun Details(
    anime: Anime,
    episodes: List<Episode>,
    onEpisodeSelected: (episodeId: String) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBackPressed)
    LazyColumn(
        contentPadding = PaddingValues(bottom = 135.dp),
        modifier = modifier,
    ) {
        item {
            MediaDetails(
                anime = anime,
                onPlayFirstEpisode = {
                    episodes.firstOrNull()?.id?.let { firstEpisodeId ->
                        onEpisodeSelected(firstEpisodeId)
                    }
                }
            )
        }

        if (episodes.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
                EpisodeList(
                    episodes = episodes,
                    onEpisodeSelected = onEpisodeSelected
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EpisodeList(
    episodes: List<Episode>,
    onEpisodeSelected: (episodeId: String) -> Unit,
) {
    val childPadding = rememberChildPadding()
    Column(
        modifier = Modifier.padding(top = childPadding.top),
    ) {
        Text(
            text = stringResource(R.string.episodes),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(start = childPadding.start)
        )
        // ToDo: specify the pivot offset
        LazyRow(
            modifier = Modifier
                .padding(top = 16.dp)
                .focusRestorer(),
            contentPadding = PaddingValues(start = childPadding.start)
        ) {
            items(
                items = episodes,
                key = { episode -> episode.id }
            ) { episode ->
                EpisodeCard(
                    episode = episode,
                    onClick = { onEpisodeSelected(episode.id) },
                    modifier = Modifier.width(260.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeCard(
    episode: Episode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StandardCardContainer(
        modifier = modifier
            .padding(end = 20.dp, bottom = 16.dp),
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 8.dp),
                text = stringResource(R.string.episode_prefix, episode.number ?: 0),
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        imageCard = {
            Surface(
                onClick = onClick,
                shape = ClickableSurfaceDefaults.shape(AnimaCardShape),
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = AnimaBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = AnimaCardShape
                    )
                ),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(episode.thumbnail)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(
                        R.string.episode_thumbnail_content_description,
                        episode.number ?: 0,
                    ),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}
