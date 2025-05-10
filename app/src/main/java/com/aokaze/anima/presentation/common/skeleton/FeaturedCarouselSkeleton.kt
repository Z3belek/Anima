package com.aokaze.anima.presentation.common.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FeaturedCarouselSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "carousel_skeleton_pulse")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "carousel_skeleton_alpha"
    )
    val skeletonPulsingColor = Color.White.copy(alpha = animatedAlpha)
    val carouselBackgroundSkeletonColor = Color.White.copy(alpha = animatedAlpha * 0.05f)


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(carouselBackgroundSkeletonColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(skeletonPulsingColor)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 48.dp, bottom = 24.dp)
                .width(484.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.height(16.dp).width(100.dp).background(skeletonPulsingColor))
                Box(modifier = Modifier.height(16.dp).width(50.dp).background(skeletonPulsingColor))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.height(40.dp).fillMaxWidth(0.8f).background(skeletonPulsingColor))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.9f).background(skeletonPulsingColor))
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.7f).background(skeletonPulsingColor))
            Spacer(modifier = Modifier.height(28.dp))
            Box(modifier = Modifier.height(40.dp).width(150.dp).background(skeletonPulsingColor))
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(skeletonPulsingColor)
                )
            }
        }
    }
}