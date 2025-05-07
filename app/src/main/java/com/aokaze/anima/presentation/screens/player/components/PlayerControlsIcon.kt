package com.aokaze.anima.presentation.screens.player.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface

@Composable
fun PlayerControlsIcon(
    isPlaying: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    onShowControls: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused && isPlaying) {
        if (isFocused && isPlaying && enabled) {
            onShowControls()
        }
    }

    val iconAlpha = if (enabled) 1f else 0.38f
    val currentLocalContentColor = LocalContentColor.current

    Surface(
        modifier = modifier.size(40.dp),
        onClick = { if (enabled) onClick() },
        enabled = enabled,
        shape = ClickableSurfaceDefaults.shape(shape = CircleShape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            contentColor = currentLocalContentColor.copy(alpha = iconAlpha),
            focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 0.3f else 0.2f),
            pressedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 0.4f else 0.2f)
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = if (enabled) 1.05f else 1.0f),
        interactionSource = interactionSource
    ) {
        Icon(
            icon,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentDescription = contentDescription,
            tint = currentLocalContentColor.copy(alpha = iconAlpha)
        )
    }
}
