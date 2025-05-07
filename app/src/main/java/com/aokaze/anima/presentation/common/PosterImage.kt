package com.aokaze.anima.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.util.StringConstants

@Composable
fun PosterImage(
    modifier: Modifier = Modifier,
    anime: Anime? = null
) {
    val imagePoster = anime?.imagePoster
    val displayTitle = anime?.titles?.find { it.type == "Default" || it.type == "English" }?.title
        ?: anime?.title
        ?: "Poster"

    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(imagePoster)
            .build(),
        contentDescription = StringConstants.Composable.ContentDescription.animePoster(displayTitle),
        contentScale = ContentScale.Crop
    )
}