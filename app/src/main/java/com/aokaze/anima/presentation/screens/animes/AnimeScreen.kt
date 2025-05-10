package com.aokaze.anima.presentation.screens.animes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.R
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.presentation.common.AnimeGrid
import com.aokaze.anima.presentation.screens.dashboard.closeDrawerWidth

@Composable
fun AnimeScreen(
    onAnimeClick: (anime: Anime) -> Unit,
    animeScreenViewModel: AnimeScreenViewModel = hiltViewModel(),
) {
    val lazyAnimeItems: LazyPagingItems<Anime> = animeScreenViewModel.animePagingFlow.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, end = closeDrawerWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.anime_screen_title),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        )

        AnimeGrid(
            items = lazyAnimeItems,
            onAnimeClick = onAnimeClick,
            gridColumns = 6,
            modifier = Modifier.fillMaxSize()
        )
    }
}