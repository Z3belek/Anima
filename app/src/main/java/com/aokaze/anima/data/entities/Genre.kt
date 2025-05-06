package com.aokaze.anima.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val slug: String,
    val name: String
)