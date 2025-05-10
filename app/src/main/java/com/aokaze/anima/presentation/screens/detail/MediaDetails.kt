package com.aokaze.anima.presentation.screens.detail

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
import androidx.compose.material.icons.outlined.Replay
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.aokaze.anima.data.entities.Genre
import com.aokaze.anima.data.entities.Resume
import com.aokaze.anima.data.util.StringConstants
import com.aokaze.anima.presentation.screens.dashboard.closeDrawerWidth
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding
import com.aokaze.anima.presentation.theme.AnimaButtonShape
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

private fun loadGenreDisplayMap(context: android.content.Context): Map<String, String> {
    return try {
        val jsonString = context.assets.open("genres.json").bufferedReader().use { it.readText() }
        val genreList = Json.decodeFromString<List<Genre>>(jsonString)
        genreList.associate { it.slug to it.name }
    } catch (e: IOException) {
        emptyMap()
    } catch (e: SerializationException) {
        emptyMap()
    }
}

@Composable
fun MediaDetails(
    anime: Anime,
    resumeData: Resume?,
    firstEpisodeSlug: String?,
    onPlayEpisode: (episodeSlug: String, startTimeMillis: Long) -> Unit
) {
    val childPadding = rememberChildPadding()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val genreDisplayMap = remember { loadGenreDisplayMap(context) }

    val buttonText: String
    val buttonIcon: ImageVector
    val playAction: () -> Unit
    var buttonEnabled = true

    if (resumeData != null) {
        buttonText = stringResource(R.string.continue_watching)
        buttonIcon = Icons.Outlined.Replay
        playAction = { onPlayEpisode(resumeData.episodeSlug, resumeData.currentPositionMillis) }
    } else {
        if (firstEpisodeSlug != null) {
            buttonText = stringResource(R.string.watch_now)
            buttonIcon = Icons.Outlined.PlayArrow
            playAction = { onPlayEpisode(firstEpisodeSlug, 0L) }
        } else {
            buttonText = stringResource(R.string.play_unavailable)
            buttonIcon = Icons.Outlined.PlayArrow
            playAction = { /* TODO: ? */ }
            buttonEnabled = false
        }
    }

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
                modifier = Modifier.padding(start = childPadding.start + closeDrawerWidth)
            ) {
                MediaLargeTitle(mediaTitle = anime.title)

                Column(
                    modifier = Modifier.alpha(0.75f)
                ) {
                    MediaDescription(description = anime.synopsis ?: "")
                    val displayGenres = anime.genres?.mapNotNull { slug ->
                        genreDisplayMap[slug]
                    }?.joinToString(", ")?.takeIf { it.isNotBlank() }

                    DotSeparatedRow(
                        modifier = Modifier.padding(top = 20.dp),
                        texts = listOfNotNull(
                            anime.type?.takeIf { it.isNotBlank() },
                            displayGenres,
                            anime.duration?.takeIf { it.isNotBlank() }
                        )
                    )
                    ExtraDetailsRow(
                        studios = anime.studios?.mapNotNull { it.name }?.joinToString(", ") ?: "",
                        demography = anime.demography?.mapNotNull { it.name }?.joinToString(", ") ?: "",
                        date = anime.broadcast ?: ""
                    )
                }
                PlayEpisodeButtonComposable(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                    },
                    buttonText = buttonText,
                    buttonIcon = buttonIcon,
                    onPlayClick = playAction,
                    enabled = buttonEnabled
                )
            }
        }
    }
}

@Composable
private fun PlayEpisodeButtonComposable(
    modifier: Modifier = Modifier,
    buttonText: String,
    buttonIcon: ImageVector,
    onPlayClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onPlayClick,
        enabled = enabled,
        modifier = modifier.padding(top = 24.dp),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = AnimaButtonShape),
        scale = ButtonDefaults.scale(focusedScale = 1f)
    ) {
        Icon(
            imageVector = buttonIcon,
            contentDescription = null
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = buttonText,
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