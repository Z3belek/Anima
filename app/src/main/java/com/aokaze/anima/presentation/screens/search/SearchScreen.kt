package com.aokaze.anima.presentation.screens.search

import android.view.KeyEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.aokaze.anima.R
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.presentation.common.AnimeCard
import com.aokaze.anima.presentation.common.PosterImage
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding
import com.aokaze.anima.presentation.theme.AnimaCardShape

@Composable
fun SearchScreen(
    onAnimeClick: (anime: Anime) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val gridState = rememberLazyGridState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 &&
                    gridState.firstVisibleItemScrollOffset < 100
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    val searchQuery by searchScreenViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchUiState by searchScreenViewModel.searchState.collectAsStateWithLifecycle()
    val searchResults = searchScreenViewModel.searchResults.collectAsLazyPagingItems()

    SearchResultContent(
        searchQuery = searchQuery,
        onSearchQueryChanged = searchScreenViewModel::onSearchQueryChanged,
        searchUiState = searchUiState,
        searchResults = searchResults,
        onAnimeClick = onAnimeClick,
        gridState = gridState
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultContent(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    searchUiState: SearchUiState,
    searchResults: LazyPagingItems<Anime>,
    onAnimeClick: (anime: Anime) -> Unit,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val childPadding = rememberChildPadding()
    val tfFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val tfInteractionSource = remember { MutableInteractionSource() }
    val isTfFocused by tfInteractionSource.collectIsFocusedAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            shape = ClickableSurfaceDefaults.shape(shape = AnimaCardShape),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                focusedContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
                pressedContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
                focusedContentColor = MaterialTheme.colorScheme.onSurface,
                pressedContentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = ClickableSurfaceDefaults.border(
                focusedBorder = androidx.tv.material3.Border(
                    border = BorderStroke(
                        width = if (isTfFocused) 2.dp else 1.dp,
                        color = animateColorAsState(
                            targetValue = if (isTfFocused) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.border,
                            label = ""
                        ).value
                    ),
                    shape = AnimaCardShape
                )
            ),
            tonalElevation = 2.dp,
            modifier = Modifier
                .padding(horizontal = childPadding.start)
                .padding(top = 8.dp, bottom = 8.dp),
            onClick = { tfFocusRequester.requestFocus() }
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                decorationBox = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .padding(start = 20.dp),
                    ) {
                        it()
                        if (searchQuery.isEmpty()) {
                            Text(
                                modifier = Modifier.graphicsLayer { alpha = 0.6f },
                                text = stringResource(R.string.search_screen_et_placeholder),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .focusRequester(tfFocusRequester)
                    .onKeyEvent {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_DOWN -> {
                                    focusManager.moveFocus(FocusDirection.Down)
                                    return@onKeyEvent true
                                }
                                KeyEvent.KEYCODE_DPAD_UP -> {
                                    focusManager.moveFocus(FocusDirection.Up)
                                    return@onKeyEvent true
                                }
                                KeyEvent.KEYCODE_BACK -> {
                                    focusManager.clearFocus(true)
                                    return@onKeyEvent true
                                }
                            }
                        }
                        false
                    },
                cursorBrush = Brush.verticalGradient(
                    colors = listOf(
                        LocalContentColor.current,
                        LocalContentColor.current,
                    )
                ),
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                maxLines = 1,
                interactionSource = tfInteractionSource,
                textStyle = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        when (searchUiState) {
            SearchUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ingresa un término para buscar.", style = MaterialTheme.typography.bodyLarge)
                }
            }
            SearchUiState.Searching -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            SearchUiState.Success -> {
                if (searchResults.itemCount == 0 && searchQuery.isNotBlank() && searchResults.loadState.refresh is LoadState.NotLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se encontraron resultados para \"$searchQuery\".", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        state = gridState,
                        contentPadding = PaddingValues(
                            start = childPadding.start,
                            end = childPadding.end,
                            top = 8.dp,
                            bottom = childPadding.bottom
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(searchResults.itemCount) { index ->
                            val anime = searchResults[index]
                            if (anime != null) {
                                AnimeCard(
                                    onClick = { onAnimeClick(anime) },
                                    modifier = Modifier
                                        .aspectRatio(1 / 1.5f)
                                        .padding(8.dp)
                                ) {
                                    PosterImage(anime = anime, modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                        searchResults.apply {
                            when {
                                loadState.append is LoadState.Loading -> {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
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
                                loadState.append is LoadState.Error -> {
                                    val e = searchResults.loadState.append as LoadState.Error
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Error al cargar más: ${e.error.localizedMessage}", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}