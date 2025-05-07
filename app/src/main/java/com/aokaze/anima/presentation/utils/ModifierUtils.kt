package com.aokaze.anima.presentation.utils

import android.view.KeyEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.onPlaced

fun Modifier.handleDPadKeyEvents(
    onLeft: (() -> Unit)? = null,
    onRight: (() -> Unit)? = null,
    onEnter: (() -> Unit)? = null
) = onPreviewKeyEvent {
    fun onActionUp(block: () -> Unit) {
        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) block()
    }

    when (it.nativeKeyEvent.keyCode) {
        KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT -> {
            onLeft?.apply {
                onActionUp(::invoke)
                return@onPreviewKeyEvent true
            }
        }

        KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT -> {
            onRight?.apply {
                onActionUp(::invoke)
                return@onPreviewKeyEvent true
            }
        }

        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
            onEnter?.apply {
                onActionUp(::invoke)
                return@onPreviewKeyEvent true
            }
        }
    }

    false
}

fun Modifier.handleDPadKeyEvents(
    onLeft: (() -> Unit)? = null,
    onRight: (() -> Unit)? = null,
    onUp: (() -> Unit)? = null,
    onDown: (() -> Unit)? = null,
    onEnter: (() -> Unit)? = null
) = onKeyEvent {

    if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
        when (it.nativeKeyEvent.keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT -> {
                onLeft?.invoke().also { return@onKeyEvent true }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT -> {
                onRight?.invoke().also { return@onKeyEvent true }
            }

            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP -> {
                onUp?.invoke().also { return@onKeyEvent true }
            }

            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_SYSTEM_NAVIGATION_DOWN -> {
                onDown?.invoke().also { return@onKeyEvent true }
            }

            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                onEnter?.invoke().also { return@onKeyEvent true }
            }
        }
    }
    false
}

fun Modifier.occupyScreenSize() = this
    .fillMaxSize()
    .wrapContentSize()

@Composable
fun Modifier.focusOnInitialVisibility(isVisible: MutableState<Boolean>): Modifier {
    val focusRequester = remember { FocusRequester() }

    return focusRequester(focusRequester)
        .onPlaced {
            if (!isVisible.value) {
                focusRequester.requestFocus()
                isVisible.value = true
            }
        }
}

data class FocusRequesterModifiers(
    val parentModifier: Modifier,
    val childModifier: Modifier
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun createInitialFocusRestorerModifiers(): FocusRequesterModifiers {
    val focusRequester = remember { FocusRequester() }
    val childFocusRequester = remember { FocusRequester() }

    val parentModifier = Modifier
        .focusRequester(focusRequester)
        .focusProperties {
            onExit = {
                focusRequester.saveFocusedChild()
                FocusRequester.Default
            }
            onEnter = {
                if (focusRequester.restoreFocusedChild()) FocusRequester.Cancel
                else childFocusRequester
            }
        }

    val childModifier = Modifier.focusRequester(childFocusRequester)

    return FocusRequesterModifiers(parentModifier, childModifier)
}

fun Modifier.ifElse(
    condition: () -> Boolean,
    ifTrueModifier: Modifier,
    ifFalseModifier: Modifier = Modifier
): Modifier = then(if (condition()) ifTrueModifier else ifFalseModifier)

fun Modifier.ifElse(
    condition: Boolean,
    ifTrueModifier: Modifier,
    ifFalseModifier: Modifier = Modifier
): Modifier = ifElse({ condition }, ifTrueModifier, ifFalseModifier)

fun Modifier.disabled(isDisabled: Boolean = true): Modifier =
    this.then(
        if (isDisabled) Modifier.alpha(0.5f)
        else Modifier
    )