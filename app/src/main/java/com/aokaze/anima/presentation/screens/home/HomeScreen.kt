package com.aokaze.anima.presentation.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.util.StringConstants
import com.aokaze.anima.presentation.common.AnimesRow
import com.aokaze.anima.presentation.common.Error
import com.aokaze.anima.presentation.common.Loading
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding

@Composable
fun HomeScreen(
    onAnimeClick: (anime: Anime) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val uiState by homeScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            Catalog(
                featuredAnimeList = s.featuredAnimeList,
                newAnimeList = s.newAnimeList,
                updatedAnimeList = s.updatedAnimeList,
                onAnimeClick = onAnimeClick,
                onScroll = onScroll,
                isTopBarVisible = isTopBarVisible,
                modifier = Modifier.fillMaxSize(),
            )
        }
        is HomeScreenUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())
        is HomeScreenUiState.Error -> Error(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun Catalog(
    featuredAnimeList: List<Anime>,
    newAnimeList: List<Anime>,
    updatedAnimeList: List<Anime>,
    onAnimeClick: (anime: Anime) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isTopBarVisible: Boolean = true,
) {

    val lazyListState = rememberLazyListState()
    val childPadding = rememberChildPadding()
    var immersiveListHasFocus by remember { mutableStateOf(false) }

    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset < 300
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }
    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) lazyListState.animateScrollToItem(0)
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 108.dp),
        modifier = modifier,
    ) {

        item(contentType = "FeaturedAnimeCarousel") {
            FeaturedAnimeCarousel(
                animes = featuredAnimeList,
                padding = childPadding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(324.dp)
            )
        }
        item(contentType = "AnimesRow") {
            AnimesRow(
                modifier = Modifier.padding(top = 16.dp),
                animes = newAnimeList,
                title = StringConstants.Composable.HomeScreenNewTitle,
                onAnimeSelected = onAnimeClick
            )
        }
        item(contentType = "AnimesRow") {
            AnimesRow(
                modifier = Modifier.padding(top = 16.dp),
                animes = updatedAnimeList,
                title = StringConstants.Composable.HomeScreenUpdatedAnimeTitle,
                onAnimeSelected = onAnimeClick
            )
        }
    }
}
