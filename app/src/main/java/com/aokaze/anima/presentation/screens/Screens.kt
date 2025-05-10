package com.aokaze.anima.presentation.screens

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.aokaze.anima.presentation.screens.detail.MediaDetailsScreen
import com.aokaze.anima.presentation.screens.genres.AnimesGenreListScreen
import com.aokaze.anima.presentation.screens.player.PlayerScreen

enum class Screens(
    private val args: List<String>? = null,
    val isTabItem: Boolean = false,
    val tabIcon: ImageVector? = null
) {
    Inicio(isTabItem = true, tabIcon = Icons.Outlined.Home),
    Animes(isTabItem = true, tabIcon = Icons.Outlined.Tv),
    Peliculas(isTabItem = true, tabIcon = Icons.Outlined.Movie),
    Generos(isTabItem = true, tabIcon = Icons.Outlined.Category),
    Search(isTabItem = true, tabIcon = Icons.Default.Search),
    AnimesGenreList(listOf(AnimesGenreListScreen.GENRE_ID_BUNDLE_KEY)),
    AnimeDetails(listOf(MediaDetailsScreen.MEDIA_ID_BUNDLE_KEY)),
    Player(listOf(PlayerScreen.EPISODE_ID_BUNDLE_KEY)),
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