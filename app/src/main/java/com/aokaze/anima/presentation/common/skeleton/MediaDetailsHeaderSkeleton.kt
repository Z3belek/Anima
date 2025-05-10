package com.aokaze.anima.presentation.common.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aokaze.anima.presentation.screens.dashboard.closeDrawerWidth
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding

@Composable
fun MediaDetailsHeaderSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "media_details_header_pulse")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "media_details_header_alpha"
    )
    val skeletonPulsingColor = Color.White.copy(alpha = animatedAlpha)
    val imageBackgroundSkeletonColor = Color.White.copy(alpha = animatedAlpha * 0.7f)
    val childPadding = rememberChildPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(442.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(imageBackgroundSkeletonColor)
        )

        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
            Spacer(modifier = Modifier.height(108.dp))
            Column(
                modifier = Modifier.padding(start = childPadding.start + closeDrawerWidth)
            ) {
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(0.7f)
                        .background(skeletonPulsingColor)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(0.5f)
                        .background(skeletonPulsingColor)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .height(15.dp)
                        .fillMaxWidth(0.9f)
                        .background(skeletonPulsingColor)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(15.dp)
                        .fillMaxWidth(0.95f)
                        .background(skeletonPulsingColor)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(15.dp)
                        .fillMaxWidth(0.8f)
                        .background(skeletonPulsingColor)
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row {
                    Box(modifier = Modifier.height(14.dp).width(80.dp).background(skeletonPulsingColor))
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.height(14.dp).width(120.dp).background(skeletonPulsingColor))
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.height(14.dp).width(60.dp).background(skeletonPulsingColor))
                }
                Spacer(modifier = Modifier.height(32.dp))

                Row {
                    Box(modifier = Modifier.height(30.dp).weight(1f).padding(end = 16.dp).background(skeletonPulsingColor))
                    Box(modifier = Modifier.height(30.dp).weight(1f).padding(end = 16.dp).background(skeletonPulsingColor))
                    Box(modifier = Modifier.height(30.dp).weight(1f).background(skeletonPulsingColor))
                }
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(200.dp)
                        .background(skeletonPulsingColor)
                )
            }
        }
    }
}