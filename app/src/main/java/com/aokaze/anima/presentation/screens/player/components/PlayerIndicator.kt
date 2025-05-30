package com.aokaze.anima.presentation.screens.player.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import com.aokaze.anima.presentation.utils.handleDPadKeyEvents
import com.aokaze.anima.presentation.utils.ifElse

@Composable
fun RowScope.PlayerControllerIndicator(
    progress: Float,
    onSeek: (seekProgress: Float) -> Unit,
    onShowControls: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isSelected by remember { mutableStateOf(false) }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val color by rememberUpdatedState(
        newValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface
    )
    val animatedIndicatorHeight by animateDpAsState(
        targetValue = 4.dp.times((if (isFocused) 2.5f else 1f))
    )
    var seekProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isSelected) {
        onShowControls()
    }

    val handleSeekEventModifier = Modifier.handleDPadKeyEvents(
        onEnter = {
            isSelected = !isSelected
            onSeek(seekProgress)
        },
        onLeft = {
            seekProgress = (seekProgress - 0.1f).coerceAtLeast(0f)
        },
        onRight = {
            seekProgress = (seekProgress + 0.1f).coerceAtMost(1f)
        }
    )

    val handleDpadCenterClickModifier = Modifier.handleDPadKeyEvents(
        onEnter = {
            seekProgress = progress
            isSelected = !isSelected
        }
    )

    Canvas(
        modifier = Modifier
            .weight(1f)
            .height(animatedIndicatorHeight)
            .padding(horizontal = 4.dp)
            .ifElse(
                condition = isSelected,
                ifTrueModifier = handleSeekEventModifier,
                ifFalseModifier = handleDpadCenterClickModifier
            )
            .focusable(interactionSource = interactionSource),
        onDraw = {
            val yOffset = size.height.div(2)
            drawLine(
                color = color.copy(alpha = 0.24f),
                start = Offset(x = 0f, y = yOffset),
                end = Offset(x = size.width, y = yOffset),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(x = 0f, y = yOffset),
                end = Offset(
                    x = size.width.times(if (isSelected) seekProgress else progress),
                    y = yOffset
                ),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )
        }
    )
}
