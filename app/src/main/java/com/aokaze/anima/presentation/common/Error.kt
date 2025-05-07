package com.aokaze.anima.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Text
import com.aokaze.anima.R

@Composable
fun Error(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.message_error),
        modifier = modifier
    )
}
