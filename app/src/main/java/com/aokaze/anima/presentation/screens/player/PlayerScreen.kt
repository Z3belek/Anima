package com.aokaze.anima.presentation.screens.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import com.aokaze.anima.R
import com.aokaze.anima.data.entities.DirectSource
import com.aokaze.anima.data.entities.Episode
import com.aokaze.anima.presentation.screens.player.components.PlayerControls
import com.aokaze.anima.presentation.screens.player.components.PlayerOverlay
import com.aokaze.anima.presentation.screens.player.components.PlayerPulse
import com.aokaze.anima.presentation.screens.player.components.PlayerPulse.Type.BACK
import com.aokaze.anima.presentation.screens.player.components.PlayerPulse.Type.FORWARD
import com.aokaze.anima.presentation.screens.player.components.PlayerPulseState
import com.aokaze.anima.presentation.screens.player.components.PlayerState
import com.aokaze.anima.presentation.screens.player.components.rememberPlayer
import com.aokaze.anima.presentation.screens.player.components.rememberPlayerPulseState
import com.aokaze.anima.presentation.screens.player.components.rememberPlayerState
import com.aokaze.anima.presentation.utils.handleDPadKeyEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

object PlayerScreen {
    const val EPISODE_ID_BUNDLE_KEY = "episodeSlug"
    const val INITIAL_SEEK_TIME_MILLIS_KEY = "startTimeMillis"
}

@Composable
fun PlayerScreen(
    onBackPressed: () -> Unit,
    playerScreenViewModel: PlayerScreenViewModel = hiltViewModel()
) {
    val uiState by playerScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is PlayerScreenUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PlayerScreenUiState.Error -> {
            if (s.message?.contains("Closing player", ignoreCase = true) == true) {
                LaunchedEffect(Unit) {
                    onBackPressed()
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                    Text(text = s.message ?: stringResource(R.string.player_error_unknown), color = Color.White)
                }
            }
        }
        is PlayerScreenUiState.Done -> {
            PlayerScreenContent(
                uiState = s,
                viewModel = playerScreenViewModel,
                onBackPressed = onBackPressed
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun PlayerScreenContent(
    uiState: PlayerScreenUiState.Done,
    viewModel: PlayerScreenViewModel,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = rememberPlayer(context)
    val playerState = rememberPlayerState(hideSeconds = 4)
    val pulseState = rememberPlayerPulseState()

    var showSourceSelectorDialog by remember { mutableStateOf(false) }
    var initialSeekPerformedForCurrentMedia by remember(uiState.currentEpisode.id, uiState.selectedSourceUrl) { mutableStateOf(false) }
    val currentInitialSeekTime by remember(uiState.currentEpisode.id, uiState.selectedSourceUrl) { derivedStateOf { viewModel.initialSeekTimeMillis } }


    LaunchedEffect(uiState.currentEpisode.id, uiState.selectedSourceUrl, exoPlayer) {
        val mediaItem = uiState.currentEpisode.intoMediaItem(uiState.selectedSourceUrl)
        if (mediaItem != null) {
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            initialSeekPerformedForCurrentMedia = false
        } else {
            viewModel.playerIsClosing(exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
            onBackPressed()
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val duration = exoPlayer.duration.coerceAtLeast(0L)
                    viewModel.onPlayerReady(duration)

                    if (!initialSeekPerformedForCurrentMedia && currentInitialSeekTime > 0) {
                        exoPlayer.seekTo(currentInitialSeekTime)
                        initialSeekPerformedForCurrentMedia = true
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playerState.showControls(isPlaying)
                if (!isPlaying && exoPlayer.playbackState == Player.STATE_READY && exoPlayer.currentPosition > 0) {
                    viewModel.saveCurrentPlayerState(exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    LaunchedEffect(exoPlayer, viewModel) {
        while (isActive) {
            if (exoPlayer.isPlaying) {
                viewModel.updateCurrentPlayerPosition(exoPlayer.currentPosition)
            }
            delay(1000L)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, exoPlayer, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else if (exoPlayer.playbackState == Player.STATE_READY && exoPlayer.currentPosition > 0) {
                        viewModel.saveCurrentPlayerState(exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.playerIsClosing(exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (exoPlayer.playbackState == Player.STATE_READY && !exoPlayer.playWhenReady && exoPlayer.currentMediaItem != null) {
                        exoPlayer.play()
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            viewModel.playerIsClosing(exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    BackHandler {
        viewModel.playerIsClosing(exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
        onBackPressed()
    }

    Box(
        Modifier
            .dPadEvents(exoPlayer, playerState, pulseState)
            .focusable()
            .fillMaxSize()
            .background(Color.Black)
    ) {
        PlayerSurface(
            player = exoPlayer,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = Modifier.fillMaxSize()
        )

        val focusRequester = remember { FocusRequester() }
        PlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            isPlaying = exoPlayer.isPlaying,
            isControlsVisible = playerState.isControlsVisible,
            centerButton = { PlayerPulse(pulseState) },
            subtitles = { /* TODO: Add subtitles */ },
            showControls = { playerState.showControls(exoPlayer.isPlaying) },
            controls = {
                PlayerControls(
                    player = exoPlayer,
                    title = uiState.currentEpisode.animeTitle ?: stringResource(R.string.default_anime_title),
                    subtitle = buildString {
                        append(stringResource(R.string.episode_prefix_simple, uiState.currentEpisode.number ?: 0))
                        uiState.currentEpisode.title?.takeIf { it.isNotBlank() }?.let {
                            append(" - ")
                            append(it)
                        }
                    },
                    showPreviousButton = uiState.previousEpisode != null,
                    showNextButton = uiState.nextEpisode != null,
                    onPreviousClicked = {
                        uiState.previousEpisode?.id?.let {
                            viewModel.loadEpisodeBySlug(it, exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
                        }
                    },
                    onNextClicked = {
                        uiState.nextEpisode?.id?.let {
                            viewModel.loadEpisodeBySlug(it, exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
                        }
                    },
                    onSettingsClicked = { showSourceSelectorDialog = true },
                    focusRequester = focusRequester,
                    onShowControls = { playerState.showControls(exoPlayer.isPlaying) },
                )
            }
        )

        if (showSourceSelectorDialog) {
            SourceSelectionDialog(
                availableSources = uiState.currentEpisode.directSources ?: emptyList(),
                selectedSourceUrl = uiState.selectedSourceUrl,
                onSourceSelected = { selectedSource ->
                    selectedSource.url?.let { newSourceUrl ->
                        if (newSourceUrl.isNotBlank() && newSourceUrl != uiState.selectedSourceUrl) {
                            viewModel.selectVideoSource(newSourceUrl, exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
                        }
                    }
                    showSourceSelectorDialog = false
                },
                onDismiss = { showSourceSelectorDialog = false }
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
private fun Episode.intoMediaItem(sourceUrl: String): MediaItem? {
    if (sourceUrl.isBlank()) {
        return null
    }
    return MediaItem.Builder()
        .setUri(sourceUrl)
        .setMediaId(this.id)
        .setTag(this)
        .build()
}

@Composable
fun SourceSelectionDialog(
    availableSources: List<DirectSource>,
    selectedSourceUrl: String,
    onSourceSelected: (DirectSource) -> Unit,
    onDismiss: () -> Unit
) {
    if (availableSources.none { !it.url.isNullOrBlank() }) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.player_source_dialog_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(availableSources.filter { !it.url.isNullOrBlank() }) { index, source ->
                    val isSelected = source.url == selectedSourceUrl
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                            .clickable { onSourceSelected(source) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.player_source_dialog_item_prefix, index + 1),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.player_source_dialog_cancel), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private fun Modifier.dPadEvents(
    exoPlayer: Player,
    playerState: PlayerState,
    pulseState: PlayerPulseState
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        if (!playerState.isControlsVisible) {
            exoPlayer.seekBack()
            pulseState.setType(BACK)
        }
    },
    onRight = {
        if (!playerState.isControlsVisible) {
            exoPlayer.seekForward()
            pulseState.setType(FORWARD)
        }
    },
    onUp = { playerState.showControls(exoPlayer.isPlaying) },
    onDown = { playerState.showControls(exoPlayer.isPlaying) },
    onEnter = {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
        playerState.showControls(exoPlayer.isPlaying)
    }
)