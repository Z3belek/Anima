package com.aokaze.anima.presentation.common.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import com.aokaze.anima.presentation.theme.AnimaCardShape

@Composable
fun GenreCardSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "genre_skeleton_pulse_transition")
    val alphaValue by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "genre_skeleton_pulse_alpha"
    )

    val skeletonBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Surface(
        onClick = { },
        modifier = modifier.alpha(alphaValue),
        shape = ClickableSurfaceDefaults.shape(shape = AnimaCardShape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = skeletonBackgroundColor,
            focusedContainerColor = skeletonBackgroundColor,
            pressedContainerColor = skeletonBackgroundColor,
            disabledContainerColor = skeletonBackgroundColor
        )
    ) {
        Box(modifier = Modifier)
    }
}