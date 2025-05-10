package com.aokaze.anima.presentation.screens.player.components

import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.aokaze.anima.data.util.StringConstants

@OptIn(UnstableApi::class)
@Composable
fun PreviousButton(
    player: Player,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onShowControls: () -> Unit = {},
) {
    PlayerControlsIcon(
        icon = Icons.Default.SkipPrevious,
        isPlaying = player.isPlaying,
        enabled = enabled,
        contentDescription =
            StringConstants.Composable.SKIP_PREVIOUS,
        onShowControls = onShowControls,
        onClick = onClick,
        modifier = modifier,
    )
}
