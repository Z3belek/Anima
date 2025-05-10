package com.aokaze.anima.presentation.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aokaze.anima.R
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.entities.Resume
import com.aokaze.anima.data.util.StringConstants
import com.aokaze.anima.presentation.common.AnimesRow
import com.aokaze.anima.presentation.common.Error
import com.aokaze.anima.presentation.common.MediaRow
import com.aokaze.anima.presentation.common.MediaRowData
import com.aokaze.anima.presentation.common.skeleton.FeaturedCarouselSkeleton
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding

@Composable
fun HomeScreen(
    onAnimeClick: (anime: Anime) -> Unit,
    onEpisodeClick: (episodeSlug: String, startTimeMillis: Long) -> Unit,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val uiState by homeScreenViewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is HomeScreenUiState.Loading

    if (uiState is HomeScreenUiState.Error) {
        Error(modifier = Modifier.fillMaxSize())
        return
    }

    val readyState = uiState as? HomeScreenUiState.Ready

    Catalog(
        isLoading = isLoading,
        featuredAnimeList = readyState?.featuredAnimeList ?: emptyList(),
        resumeList = readyState?.resumeList ?: emptyList(),
        newAnimeList = readyState?.newAnimeList ?: emptyList(),
        updatedAnimeList = readyState?.updatedAnimeList ?: emptyList(),
        onAnimeClick = onAnimeClick,
        onEpisodeClick = onEpisodeClick,
        getEpisodeProvider = { animeId ->
            homeScreenViewModel.getFirstEpisodeSlug(animeId)
        },
        modifier = Modifier.fillMaxSize(),
        uiState = uiState
    )
}

@Composable
private fun Catalog(
    isLoading: Boolean,
    featuredAnimeList: List<Anime>,
    resumeList: List<Resume>,
    newAnimeList: List<Anime>,
    updatedAnimeList: List<Anime>,
    onAnimeClick: (anime: Anime) -> Unit,
    onEpisodeClick: (episodeSlug: String, startTimeMillis: Long) -> Unit,
    getEpisodeProvider: suspend (animeId: String) -> String?,
    modifier: Modifier = Modifier,
    uiState: HomeScreenUiState
) {

    val lazyListState = rememberLazyListState()
    val childPadding = rememberChildPadding()
    val context = LocalContext.current

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 108.dp),
        modifier = modifier,
    ) {
        item(contentType = "FeaturedAnimeCarousel") {
            if (isLoading && featuredAnimeList.isEmpty()) {
                FeaturedCarouselSkeleton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                )
            } else if (featuredAnimeList.isNotEmpty()) {
                FeaturedAnimeCarousel(
                    animes = featuredAnimeList,
                    resumeList = resumeList,
                    getEpisodeProvider = getEpisodeProvider,
                    onPlayRequest = { episodeSlug, startTimeMillis ->
                        onEpisodeClick(episodeSlug, startTimeMillis)
                    },
                    onDetailsRequest = { animeId ->
                        featuredAnimeList.find { it.id == animeId }?.let { onAnimeClick(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                )
            }
        }

        if (resumeList.isNotEmpty() || (isLoading && (uiState as? HomeScreenUiState.Ready)?.resumeList?.isEmpty() != false)) {
            item(key = "resume_media_row", contentType = "ResumeMediaRow") {
                val resumeMediaRowItems = resumeList.map { resume ->
                    val progressValue = if (resume.durationMillis > 0) {
                        (resume.currentPositionMillis.toFloat() / resume.durationMillis.toFloat()).coerceIn(0f, 1f)
                    } else { 0f }
                    val episodeTitleText = resume.episodeTitle.takeIf { it.isNotBlank() }?.let { " - $it" } ?: ""

                    MediaRowData(
                        id = "resume-${resume.episodeSlug}",
                        imageUrl = resume.episodeThumbnailUrl,
                        primaryText = resume.animeTitle,
                        secondaryText = "${context.getString(R.string.episode_prefix_simple, resume.episodeNumber)}$episodeTitleText",
                        progress = progressValue,
                        onClick = { onEpisodeClick(resume.episodeSlug, resume.currentPositionMillis) }
                    )
                }
                MediaRow(
                    title = stringResource(R.string.continue_watching),
                    items = resumeMediaRowItems,
                    isLoading = isLoading && resumeList.isEmpty(),
                    modifier = Modifier.padding(top = 16.dp),
                    itemWidth = 200.dp,
                    startPadding = 0.dp,
                    endPadding = childPadding.end
                )
            }
        }
        item(contentType = "NewAnimesRow") {
            AnimesRow(
                modifier = Modifier.padding(top = 16.dp),
                animes = newAnimeList,
                isLoading = isLoading && newAnimeList.isEmpty(),
                title = StringConstants.Composable.NEW_ANIME,
                onAnimeSelected = onAnimeClick,
                startPadding = childPadding.start,
                endPadding = childPadding.end
            )
        }

        item(contentType = "UpdatedAnimesRow") {
            AnimesRow(
                modifier = Modifier.padding(top = 16.dp),
                animes = updatedAnimeList,
                isLoading = isLoading && updatedAnimeList.isEmpty(),
                title = StringConstants.Composable.UPDATE_RECENTLY,
                onAnimeSelected = onAnimeClick,
                startPadding = childPadding.start,
                endPadding = childPadding.end
            )
        }
    }
}