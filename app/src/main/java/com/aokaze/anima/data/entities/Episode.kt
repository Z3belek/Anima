package com.aokaze.anima.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Episode(
    @SerialName("slug")
    val id: String,
    @SerialName("anime_slug")
    val animeSlug: String?,
    @SerialName("episode_number")
    val number: Long?,
    @SerialName("episode_title")
    val title: String? = null,
    @SerialName("anime_title")
    val animeTitle: String? = null,
    @SerialName("previous_episode")
    val previousEpisode: String? = null,
    @SerialName("next_episode")
    val nextEpisode: String? = null,
    @SerialName("direct_sources")
    val directSources: List<DirectSource>? = null,
    @SerialName("iframe_players")
    val iframePlayers: List<String>? = null,
    @SerialName("thumbnail")
    val thumbnail: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
)

@Serializable
data class DirectSource(
    val url: String?,
    val type: String?
)