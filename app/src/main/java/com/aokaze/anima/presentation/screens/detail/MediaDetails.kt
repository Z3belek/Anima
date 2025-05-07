package com.aokaze.anima.presentation.screens.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aokaze.anima.R
import com.aokaze.anima.data.entities.Anime
import com.aokaze.anima.data.util.StringConstants
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding
import com.aokaze.anima.presentation.theme.AnimaButtonShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaDetails(
    anime: Anime,
    onPlayFirstEpisode: () -> Unit
) {
    val childPadding = rememberChildPadding()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(442.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        MediaImageWithGradients(
            anime = anime,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
            Spacer(modifier = Modifier.height(108.dp))
            Column(
                modifier = Modifier.padding(start = childPadding.start)
            ) {
                MediaLargeTitle(mediaTitle = anime.title)

                Column(
                    modifier = Modifier.alpha(0.75f)
                ) {
                    MediaDescription(description = anime.synopsis ?: "")
                    DotSeparatedRow(
                        modifier = Modifier.padding(top = 20.dp),
                        texts = listOfNotNull(
                            anime.type?.takeIf { it.isNotBlank() },
                            anime.genres?.joinToString(", ")?.takeIf { it.isNotBlank() },
                            anime.duration?.takeIf { it.isNotBlank() }
                        )
                    )
                    ExtraDetailsRow(
                        studios = anime.studios?.mapNotNull { it.name }?.joinToString(", ") ?: "",
                        demography = anime.demography?.mapNotNull { it.name }?.joinToString(", ") ?: "",
                        date = anime.broadcast ?: "" // O usar anime.year si es más apropiado
                    )
                }
                PlayFirstEpisodeButton( // Renombrar botón
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    },
                    onPlayClick = onPlayFirstEpisode // Usar lambda recibida
                )
            }
        }
    }
}

@Composable
private fun PlayFirstEpisodeButton(
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit
) {
    Button(
        onClick = onPlayClick,
        modifier = modifier.padding(top = 24.dp),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = AnimaButtonShape)
    ) {
        Icon(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = null
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.watch_now),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun ExtraDetailsRow(
    studios: String,
    demography: String,
    date: String
) {
    Row(modifier = Modifier.padding(top = 32.dp)) {
        if (studios.isNotBlank()) {
            TitleValueText(
                modifier = Modifier
                    .padding(end = 32.dp)
                    .weight(1f),
                title = stringResource(R.string.studio),
                value = studios
            )
        }
        if (demography.isNotBlank()) {
            TitleValueText(
                modifier = Modifier
                    .padding(end = 32.dp)
                    .weight(1f),
                title = stringResource(R.string.demography),
                value = demography
            )
        }
        if (date.isNotBlank()) {
            TitleValueText(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.release_date),
                value = date
            )
        }
    }
}

@Composable
private fun MediaDescription(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        modifier = Modifier.padding(top = 8.dp),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun MediaLargeTitle(mediaTitle: String) {
    Text(
        text = mediaTitle,
        style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun MediaImageWithGradients(
    anime: Anime,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(anime.thumbnail.takeUnless { it.isNullOrBlank() } ?: anime.imagePoster)
            .crossfade(true).build(),
        contentDescription = StringConstants
            .Composable
            .ContentDescription
            .animePoster(anime.title),
        contentScale = ContentScale.Crop,
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, gradientColor),
                    startY = 600f
                )
            )
            drawRect(
                Brush.horizontalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    endX = 2000f,
                    startX = 600f
                )
            )
            drawRect(
                Brush.linearGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    start = Offset(x = 500f, y = 500f),
                    end = Offset(x = 1000f, y = 0f)
                )
            )
        }
    )
}