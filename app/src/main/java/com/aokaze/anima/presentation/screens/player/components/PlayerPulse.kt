package com.aokaze.anima.presentation.screens.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import com.aokaze.anima.presentation.screens.player.components.PlayerPulse.Type.NONE
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce

object PlayerPulse {
    enum class Type { FORWARD, BACK, NONE }
}

@Composable
fun PlayerPulse(
    state: PlayerPulseState = rememberPlayerPulseState()
) {
    val icon = when (state.type) {
        PlayerPulse.Type.FORWARD -> Icons.Default.Forward10
        PlayerPulse.Type.BACK -> Icons.Default.Replay10
        NONE -> null
    }
    if (icon != null) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .size(88.dp)
                .wrapContentSize()
                .size(48.dp)
        )
    }
}

class PlayerPulseState {
    private var _type by mutableStateOf(NONE)
    val type: PlayerPulse.Type get() = _type

    private val channel = Channel<Unit>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow()
            .debounce(2.seconds)
            .collect { _type = NONE }
    }

    fun setType(type: PlayerPulse.Type) {
        _type = type
        channel.trySend(Unit)
    }
}

@Composable
fun rememberPlayerPulseState() =
    remember { PlayerPulseState() }.also { LaunchedEffect(it) { it.observe() } }
