package com.aokaze.anima.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "continue_watching")
data class Resume(
    @PrimaryKey val episodeSlug: String,
    val animeSlug: String,
    val animeTitle: String,
    val episodeTitle: String,
    val episodeNumber: Int,
    val episodeThumbnailUrl: String?,
    var currentPositionMillis: Long,
    var durationMillis: Long,
    var lastWatchedTimestamp: Long
)