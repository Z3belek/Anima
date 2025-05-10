package com.aokaze.anima.presentation.screens.search

import android.view.KeyEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.aokaze.anima.presentation.common.AnimeGrid
import com.aokaze.anima.presentation.screens.dashboard.closeDrawerWidth
import com.aokaze.anima.presentation.theme.AnimaCardShape

@Composable
fun SearchScreen(
    onAnimeClick: (anime: Anime) -> Unit,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel(),
) {
    val searchQuery by searchScreenViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchUiState by searchScreenViewModel.searchState.collectAsStateWithLifecycle()
    val searchResults = searchScreenViewModel.searchResults.collectAsLazyPagingItems()

    SearchResultContent(
        searchQuery = searchQuery,
        onSearchQueryChanged = searchScreenViewModel::onSearchQueryChanged,
        searchUiState = searchUiState,
        searchResults = searchResults,
        onAnimeClick = onAnimeClick,
    )
}

@Composable
fun SearchResultContent(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    searchUiState: SearchUiState,
    searchResults: LazyPagingItems<Anime>,
    onAnimeClick: (anime: Anime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tfFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val tfInteractionSource = remember { MutableInteractionSource() }
    val isTfFocused by tfInteractionSource.collectIsFocusedAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 60.dp)
    ) {
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
                            label = "search_tf_border"
                        ).value
                    ),
                    shape = AnimaCardShape
                )
            ),
            tonalElevation = 2.dp,
            modifier = Modifier
                .padding(end = closeDrawerWidth)
                .padding(horizontal = 0.dp)
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
                                    // TODO: Add logic to move focus up
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = closeDrawerWidth)
                .padding(top = 8.dp)
        ) {
            when (searchUiState) {
                SearchUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.search_enter_term), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                SearchUiState.Searching -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                SearchUiState.Success -> {
                    if (searchResults.loadState.refresh is LoadState.NotLoading && searchResults.itemCount == 0 && searchQuery.isNotBlank()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.search_no_results, searchQuery), style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        AnimeGrid(
                            items = searchResults,
                            onAnimeClick = onAnimeClick,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}