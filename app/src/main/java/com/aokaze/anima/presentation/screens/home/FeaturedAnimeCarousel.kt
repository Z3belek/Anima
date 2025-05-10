package com.aokaze.anima.presentation.screens.home

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Carousel
import androidx.tv.material3.CarouselDefaults
import androidx.tv.material3.CarouselState
import androidx.tv.material3.ExperimentalTvMaterial3Api
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
import com.aokaze.anima.presentation.theme.AnimaBorderWidth
import com.aokaze.anima.presentation.theme.AnimaButtonShape
import java.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException

private fun loadGenreDisplayMap(context: Context): Map<String, String> {
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

@OptIn(ExperimentalTvMaterial3Api::class)
val CarouselSaver = Saver<CarouselState, Int>(
    save = { it.activeItemIndex },
    restore = { CarouselState(it) }
)

@Composable
fun Modifier.onFirstGainingVisibility(onGainingVisibility: () -> Unit): Modifier {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) { if (isVisible) onGainingVisibility() }
    return onPlaced { isVisible = true }
}

@Composable
fun Modifier.requestFocusOnFirstGainingVisibility(): Modifier {
    val focusRequester = remember { FocusRequester() }
    return focusRequester(focusRequester).onFirstGainingVisibility {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeaturedAnimeCarousel(
    animes: List<Anime>,
    resumeList: List<Resume>,
    getEpisodeProvider: suspend (animeId: String) -> String?,
    onPlayRequest: (episodeSlug: String, startTimeMillis: Long) -> Unit,
    onDetailsRequest: (animeId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (animes.isEmpty()) {
        return
    }

    val context = LocalContext.current
    val genreDisplayMap = remember { loadGenreDisplayMap(context) }

    val carouselState = rememberSaveable(saver = CarouselSaver) { CarouselState(0) }

    val activeAnime by remember(carouselState.activeItemIndex) {
        derivedStateOf { animes[carouselState.activeItemIndex] }
    }

    Box(
        modifier = modifier
    ) {
        Crossfade(
            targetState = activeAnime,
            animationSpec = tween(durationMillis = 700),
            modifier = Modifier.fillMaxSize(),
            label = "FeaturedAnimeBackgroundCrossfade"
        ) { animeToShow ->
            CarouselItemBackground(
                anime = animeToShow,
                modifier = Modifier.fillMaxSize(),
                applyScrim = true
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = StringConstants.Composable.ContentDescription.ANIME_CAROUSEL
                }
        ) {
            Carousel(
                itemCount = animes.size,
                carouselState = carouselState,
                modifier = Modifier.fillMaxSize(),
                contentTransformStartToEnd = fadeIn(tween(durationMillis = 700))
                    .togetherWith(fadeOut(tween(durationMillis = 700))),
                contentTransformEndToStart = fadeIn(tween(durationMillis = 700))
                    .togetherWith(fadeOut(tween(durationMillis = 700))),
                carouselIndicator = {
                    CarouselIndicator(
                        itemCount = animes.size,
                        activeItemIndex = carouselState.activeItemIndex,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 32.dp)
                    )
                },
                content = { index ->
                    AnimatedContent(
                        targetState = animes[index],
                        transitionSpec = {
                            fadeIn(animationSpec = tween(700)) togetherWith fadeOut(animationSpec = tween(700))
                        },
                        modifier = Modifier.fillMaxSize(),
                        label = "CarouselItemContentAnimation"
                    ) { animeInSlide ->
                        if (animeInSlide.id == activeAnime.id) {
                            val currentResumeInfo = remember(activeAnime.id, resumeList) {
                                resumeList.find { it.animeSlug == activeAnime.id }
                            }
                            CarouselItemForeground(
                                anime = activeAnime,
                                resumeInfo = currentResumeInfo,
                                genreDisplayMap = genreDisplayMap,
                                getEpisodeProvider = getEpisodeProvider,
                                onPlayRequest = onPlayRequest,
                                onDetailsRequest = onDetailsRequest,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(Modifier.fillMaxSize())
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CarouselIndicator(
    itemCount: Int,
    activeItemIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        CarouselDefaults.IndicatorRow(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            itemCount = itemCount,
            activeItemIndex = activeItemIndex,
            indicator = { isActive ->
                val activeColor = MaterialTheme.colorScheme.primary
                val inactiveColor = Color.White.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier.size(8.dp)
                        .background(
                            color = if (isActive) activeColor else inactiveColor,
                            shape = CircleShape,
                        ),
                )
            }
        )
    }
}

private data class ButtonStateData(
    val text: String,
    val icon: ImageVector,
    val enabled: Boolean,
    val action: () -> Unit
)

@Composable
private fun CarouselItemForeground(
    anime: Anime,
    resumeInfo: Resume?,
    genreDisplayMap: Map<String, String>,
    getEpisodeProvider: suspend (animeId: String) -> String?,
    onPlayRequest: (episodeSlug: String, startTimeMillis: Long) -> Unit,
    onDetailsRequest: (animeId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var firstEpisodeSlugForCurrentAnime by remember(anime.id) { mutableStateOf<String?>(null) }
    var isLoadingFirstEpisode by remember(anime.id) { mutableStateOf(false) }

    LaunchedEffect(anime.id, resumeInfo) {
        if (resumeInfo == null) {
            isLoadingFirstEpisode = true
            firstEpisodeSlugForCurrentAnime = getEpisodeProvider(anime.id)
            isLoadingFirstEpisode = false
        } else {
            firstEpisodeSlugForCurrentAnime = null
            isLoadingFirstEpisode = false
        }
    }

    val buttonStateData = remember(anime.id, resumeInfo, firstEpisodeSlugForCurrentAnime, isLoadingFirstEpisode) {
        when {
            resumeInfo != null -> ButtonStateData(
                text = context.getString(R.string.continue_watching_episode_number, resumeInfo.episodeNumber),
                icon = Icons.Outlined.Replay,
                enabled = true,
                action = { onPlayRequest(resumeInfo.episodeSlug, resumeInfo.currentPositionMillis) }
            )
            isLoadingFirstEpisode -> ButtonStateData(
                text = context.getString(R.string.message_loading),
                icon = Icons.Outlined.PlayArrow,
                enabled = false,
                action = {}
            )
            firstEpisodeSlugForCurrentAnime != null -> ButtonStateData(
                text = context.getString(R.string.watch_now),
                icon = Icons.Outlined.PlayArrow,
                enabled = true,
                action = { onPlayRequest(firstEpisodeSlugForCurrentAnime!!, 0L) }
            )
            else -> ButtonStateData(
                text = context.getString(R.string.details),
                icon = Icons.Outlined.Info,
                enabled = true,
                action = { onDetailsRequest(anime.id) }
            )
        }
    }

    Box(
        modifier = modifier
            .padding(start = AnimaBorderWidth, end = 48.dp, bottom = 24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column(
            modifier = Modifier.width(484.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val displayGenres = anime.genres?.mapNotNull { slug ->
                    genreDisplayMap[slug]
                }?.take(2)?.joinToString(" • ") ?: ""

                if (displayGenres.isNotEmpty()) {
                    Text(
                        text = displayGenres,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                anime.year?.let { year ->
                    if (displayGenres.isNotEmpty()) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = anime.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = Offset(x = 1f, y = 2f),
                        blurRadius = 4f
                    )
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            anime.synopsis?.let { synopsis ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = synopsis,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp,
                    modifier = Modifier.heightIn(max = 40.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            DynamicCarouselPlayButton(
                text = buttonStateData.text,
                icon = buttonStateData.icon,
                onClick = buttonStateData.action,
                enabled = buttonStateData.enabled,
                modifier = Modifier.height(40.dp)
            )
        }
    }
}

@Composable
private fun CarouselItemBackground(
    anime: Anime,
    modifier: Modifier = Modifier,
    applyScrim: Boolean = true,
    gradientColor: Color = MaterialTheme.colorScheme.surface
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(anime.thumbnail.takeUnless { it.isNullOrBlank() } ?: anime.imagePoster)
            .crossfade(true)
            .build(),
        contentDescription = StringConstants.Composable.ContentDescription.animePoster(anime.title),
        modifier = modifier.then(
            if (applyScrim) {
                Modifier.drawWithContent {
                    drawContent()
                    drawRect(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, gradientColor.copy(alpha = 0.8f), gradientColor),
                            startY = size.height * 0.3f,
                            endY = size.height
                        )
                    )
                    drawRect(
                        Brush.horizontalGradient(
                            colors = listOf(gradientColor.copy(alpha = 0.95f), gradientColor.copy(alpha = 0.7f), Color.Transparent),
                            startX = 0f,
                            endX = size.width * 0.75f
                        )
                    )
                }
            } else Modifier
        ),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun DynamicCarouselPlayButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = AnimaButtonShape),
        colors = ButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface,
            focusedContentColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        scale = ButtonDefaults.scale(focusedScale = 1f)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}