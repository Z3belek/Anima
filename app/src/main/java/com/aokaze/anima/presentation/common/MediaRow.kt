package com.aokaze.anima.presentation.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aokaze.anima.R
import com.aokaze.anima.presentation.common.skeleton.MediaCardSkeleton
import com.aokaze.anima.presentation.screens.dashboard.rememberChildPadding
import com.aokaze.anima.presentation.theme.AnimaBorderWidth

data class MediaRowData(
    val id: String,
    val imageUrl: String?,
    @DrawableRes val imagePlaceholder: Int = R.drawable.default_placeholder,
    val primaryText: String,
    val secondaryText: String? = null,
    val progress: Float? = null,
    val onClick: () -> Unit
)

private const val SKELETON_MEDIA_ROW_ITEMS = 3

@Composable
fun MediaRow(
    title: String,
    items: List<MediaRowData>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    itemWidth: Dp = 200.dp,
    startPadding: Dp = rememberChildPadding().start,
    endPadding: Dp = rememberChildPadding().end,
    rowArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp)
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .alpha(1f)
                .padding(start = AnimaBorderWidth + startPadding, top = 24.dp, bottom = 16.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth().focusRestorer(),
            contentPadding = PaddingValues(start = startPadding, end = endPadding),
            horizontalArrangement = rowArrangement,
            userScrollEnabled = !isLoading
        ) {
            if (isLoading) {
                items(SKELETON_MEDIA_ROW_ITEMS, key = { "skeleton_media_card_$it" }) {
                    MediaCardSkeleton(
                        modifier = Modifier.width(itemWidth)
                    )
                }
            } else {
                itemsIndexed(items, key = { _, item -> item.id }) { _, item ->
                    MediaCard(
                        imageUrl = item.imageUrl,
                        imagePlaceholder = item.imagePlaceholder,
                        primaryText = item.primaryText,
                        secondaryText = item.secondaryText,
                        progress = item.progress,
                        onClick = item.onClick,
                        modifier = Modifier.width(itemWidth).padding(AnimaBorderWidth)
                    )
                }
            }
        }
    }
}