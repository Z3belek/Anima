package com.aokaze.anima.presentation.screens.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.R
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.entities.Episode
import com.aokaze.anima.data.entities.Resume
import com.aokaze.anima.presentation.common.MediaRow
import com.aokaze.anima.presentation.common.MediaRowData
import com.aokaze.anima.presentation.common.skeleton.MediaDetailsHeaderSkeleton
import com.aokaze.anima.presentation.screens.dashboard.closeDrawerWidth
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding

object MediaDetailsScreen {
    const val MEDIA_ID_BUNDLE_KEY = "mediaId"
}

@Composable
fun MediaDetailsScreen(
    onEpisodeSelected: (episodeSlug: String, startTimeMillis: Long) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: MediaDetailsScreenViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        initialValue = MediaDetailsScreenUiState.Loading,
        lifecycle = lifecycleOwner.lifecycle
    )

    val isLoading = uiState is MediaDetailsScreenUiState.Loading
    val isError = uiState is MediaDetailsScreenUiState.Error

    if (isError) {
        val errorState = uiState as MediaDetailsScreenUiState.Error
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: ${errorState.message}", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onBackPressed) { Text("Volver") }
            }
        }
    } else {
        val doneState = uiState as? MediaDetailsScreenUiState.Done
        DetailsSkeletonOrContent(
            isLoading = isLoading,
            anime = doneState?.anime,
            episodes = doneState?.episodes ?: emptyList(),
            resumeData = doneState?.resumeInfo,
            firstEpisodeSlug = doneState?.firstEpisodeSlug,
            onEpisodeSelected = onEpisodeSelected,
            onBackPressed = onBackPressed,
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
        )
    }
}

@Composable
private fun DetailsSkeletonOrContent(
    isLoading: Boolean,
    anime: Anime?,
    episodes: List<Episode>,
    resumeData: Resume?,
    firstEpisodeSlug: String?,
    onEpisodeSelected: (episodeSlug: String, startTimeMillis: Long) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBackPressed)
    val context = LocalContext.current
    val childPadding = rememberChildPadding()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 135.dp),
        modifier = modifier,
    ) {
        item(key = "media_details_header") {
            if (isLoading && anime == null) {
                MediaDetailsHeaderSkeleton()
            } else if (anime != null) {
                MediaDetails(
                    anime = anime,
                    resumeData = resumeData,
                    firstEpisodeSlug = firstEpisodeSlug,
                    onPlayEpisode = { episodeSlug, startTimeMillis ->
                        onEpisodeSelected(episodeSlug, startTimeMillis)
                    }
                )
            }
        }

        if (episodes.isNotEmpty() || (isLoading && anime != null)) {
            item(key = "episodes_spacer") { Spacer(modifier = Modifier.height(32.dp)) }
            item(key = "episodes_media_row", contentType = "EpisodesMediaRow") {
                val episodeMediaRowItems = episodes.map { episode ->
                    val primaryText = if (!episode.title.isNullOrBlank()) {
                        context.getString(R.string.episode_prefix_simple, episode.number ?: 0) + ": ${episode.title}"
                    } else {
                        context.getString(R.string.episode_prefix, episode.number ?: 0)
                    }
                    MediaRowData(
                        id = "ep-${episode.id}",
                        imageUrl = episode.thumbnail,
                        primaryText = primaryText,
                        secondaryText = null,
                        progress = null,
                        onClick = { onEpisodeSelected(episode.id, 0L) }
                    )
                }
                MediaRow(
                    title = stringResource(R.string.episodes),
                    items = episodeMediaRowItems,
                    isLoading = isLoading && episodes.isEmpty(),
                    modifier = Modifier.padding(top = childPadding.top),
                    itemWidth = 260.dp,
                    startPadding = closeDrawerWidth + childPadding.start,
                    endPadding = childPadding.end
                )
            }
        }
    }
}
