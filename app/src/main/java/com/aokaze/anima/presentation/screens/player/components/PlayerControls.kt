package com.aokaze.anima.presentation.screens.player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.aokaze.anima.data.util.StringConstants

@Composable
fun PlayerControls(
    player: Player,
    title: String,
    subtitle: String,
    showPreviousButton: Boolean,
    showNextButton: Boolean,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    focusRequester: FocusRequester,
    onShowControls: () -> Unit = {},
) {
    val isPlaying = player.isPlaying

    PlayerMainFrame(
        mediaTitle = {
            PlayerMediaTitle(
                title = title,
                secondaryText = subtitle,
                tertiaryText = "",
                type = PlayerMediaTitleType.DEFAULT
            )
        },
        mediaActions = {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showPreviousButton) {
                    PreviousButton(
                        player = player,
                        onClick = onPreviousClicked,
                        enabled = true,
                        onShowControls = onShowControls
                    )
                }
                if (showNextButton) {
                    NextButton(
                        player = player,
                        onClick = onNextClicked,
                        enabled = true,
                        onShowControls = onShowControls
                    )
                }
                PlayerControlsIcon(
                    icon = Icons.Default.AutoAwesomeMotion,
                    isPlaying = isPlaying,
                    contentDescription =
                        StringConstants.Composable.PlayerControlPlaylistButton,
                    onShowControls = onShowControls,
                    onClick = { /* TODO: Futura funcionalidad */ }
                )
                PlayerControlsIcon(
                    icon = Icons.Default.ClosedCaption,
                    isPlaying = isPlaying,
                    contentDescription =
                        StringConstants.Composable.PlayerControlClosedCaptionsButton,
                    onShowControls = onShowControls,
                    onClick = { /* TODO: Implementar subtítulos */ }
                )
                PlayerControlsIcon(
                    icon = Icons.Default.Settings,
                    isPlaying = isPlaying,
                    contentDescription =
                        StringConstants.Composable.PlayerControlSettingsButton,
                    onShowControls = onShowControls,
                    onClick = onSettingsClicked
                )
            }
        },
        seeker = {
            PlayerSeeker(
                player = player,
                focusRequester = focusRequester,
                onShowControls = onShowControls,
            )
        },
        more = null
    )
}