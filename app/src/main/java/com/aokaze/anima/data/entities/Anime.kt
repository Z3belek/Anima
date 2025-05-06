package com.aokaze.anima.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Anime(
    @SerialName("slug")
    val id: String,
    @SerialName("id_mal")
    val malId: Long? = null,
    val synopsis: String? = null,
    @SerialName("image_poster")
    val imagePoster: String? = null,
    val trailer: String? = null,
    val genres: List<String>? = null,
    val quality: String? = null,
    val broadcast: String? = null,
    val languages: String? = null,
    val type: String? = null,
    @SerialName("episode_counter")
    val episodeCounter: Long? = null,
    val duration: String? = null,
    val status: String? = null,
    val studios: List<MalInfo>? = null,
    val demography: List<MalInfo>? = null,
    val title: String,
    val titles: List<TitleInfo>? = null,
    val airing: Boolean? = null,
    val score: Double? = null,
    val year: Long? = null,
    val season: String? = null,
    val rating: String? = null,
    val thumbnail: String? = null,
    @SerialName("is_adult")
    val isAdult: Boolean? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
)

@Serializable
data class MalInfo(
    val name: String?,
    @SerialName("id_mal")
    val malId: Long?
)

@Serializable
data class TitleInfo(
    val type: String?,
    val title: String?
)