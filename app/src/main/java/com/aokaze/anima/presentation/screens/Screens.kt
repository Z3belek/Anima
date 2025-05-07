package com.aokaze.anima.presentation.screens

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.aokaze.anima.presentation.screens.detail.MediaDetailsScreen
import com.aokaze.anima.presentation.screens.genres.AnimesGenreListScreen
import com.aokaze.anima.presentation.screens.player.PlayerScreen

enum class Screens(
    private val args: List<String>? = null,
    val isTabItem: Boolean = false,
    val tabIcon: ImageVector? = null
) {
    Inicio(isTabItem = true),
    Generos(isTabItem = true),
    Search(isTabItem = true, tabIcon = Icons.Default.Search),
    AnimesGenreList(listOf(AnimesGenreListScreen.GenreIdBundleKey)),
    AnimeDetails(listOf(MediaDetailsScreen.MediaIdBundleKey)),
    Player(listOf(PlayerScreen.EpisodeIdBundleKey)),
    Dashboard;

    operator fun invoke(): String {
        val argList = StringBuilder()
        args?.let { nnArgs ->
            nnArgs.forEach { arg -> argList.append("/{$arg}") }
        }
        return name + argList
    }

    fun withArgs(vararg argsValues: Any): String {
        val destinationPath = StringBuilder()
        argsValues.forEach { value ->
            destinationPath.append("/${Uri.encode(value.toString())}")
        }
        return name + destinationPath
    }
}