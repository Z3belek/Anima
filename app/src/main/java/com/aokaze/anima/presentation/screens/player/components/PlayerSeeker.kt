package com.aokaze.anima.presentation.screens.player.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.PlayPauseButtonState
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import com.aokaze.anima.data.util.StringConstants
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
@Composable
fun PlayerSeeker(
    player: Player,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    state: PlayPauseButtonState = rememberPlayPauseButtonState(player),
    onSeek: (Float) -> Unit = {
        player.seekTo(player.duration.times(it).toLong())
    },
    onShowControls: () -> Unit = {},
) {
    val contentDuration = player.contentDuration.milliseconds

    var currentPositionMs by remember(player) { mutableLongStateOf(0L) }
    val currentPosition = currentPositionMs.milliseconds

    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            currentPositionMs = player.currentPosition
        }
    }

    val contentProgressString =
        currentPosition.toComponents { h, m, s, _ ->
            if (h > 0) {
                "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            } else {
                "${m.padStartWith0()}:${s.padStartWith0()}"
            }
        }
    val contentDurationString =
        contentDuration.toComponents { h, m, s, _ ->
            if (h > 0) {
                "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            } else {
                "${m.padStartWith0()}:${s.padStartWith0()}"
            }
        }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerControlsIcon(
            modifier = Modifier.focusRequester(focusRequester),
            icon = if (state.showPlay) Icons.Default.PlayArrow else Icons.Default.Pause,
            onClick = state::onClick,
            isPlaying = player.isPlaying,
            contentDescription = StringConstants
                .Composable
                .PLAY_PAUSE
        )
        PlayerControllerText(text = contentProgressString)
        PlayerControllerIndicator(
            progress = (currentPosition / contentDuration).toFloat(),
            onSeek = onSeek,
            onShowControls = onShowControls
        )
        PlayerControllerText(text = contentDurationString)
    }
}

private fun Number.padStartWith0() = this.toString().padStart(2, '0')
