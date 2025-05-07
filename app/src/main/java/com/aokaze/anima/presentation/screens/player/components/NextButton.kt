package com.aokaze.anima.presentation.screens.player.components

import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.aokaze.anima.data.util.StringConstants

@OptIn(UnstableApi::class)
@Composable
fun NextButton(
    player: Player,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onShowControls: () -> Unit = {},
) {
    PlayerControlsIcon(
        icon = Icons.Default.SkipNext,
        isPlaying = player.isPlaying,
        enabled = enabled,
        contentDescription =
            StringConstants.Composable.PlayerControlSkipNextButton,
        onShowControls = onShowControls,
        onClick = onClick,
        modifier = modifier
    )
}
