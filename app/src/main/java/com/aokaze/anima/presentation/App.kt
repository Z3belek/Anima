package com.aokaze.anima.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aokaze.anima.presentation.screens.Screens
import com.aokaze.anima.presentation.screens.dashboard.DashboardScreen
import com.aokaze.anima.presentation.screens.detail.MediaDetailsScreen
import com.aokaze.anima.presentation.screens.genres.AnimesGenreListScreen
import com.aokaze.anima.presentation.screens.player.PlayerScreen

@Composable
fun App(onBackPressed: () -> Unit) {

    val navController = rememberNavController()
    var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screens.Dashboard(),
        builder = {
            composable(
                route = Screens.AnimesGenreList(),
                arguments = listOf(
                    navArgument(AnimesGenreListScreen.GENRE_ID_BUNDLE_KEY) {
                        type = NavType.StringType
                    }
                )
            ) {
                AnimesGenreListScreen(
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    },
                    onAnimeSelected = { anime ->
                        navController.navigate(
                            Screens.AnimeDetails.withArgs(anime.id)
                        )
                    }
                )
            }
            composable(
                route = Screens.AnimeDetails(),
                arguments = listOf(
                    navArgument(MediaDetailsScreen.MEDIA_ID_BUNDLE_KEY) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                MediaDetailsScreen(
                    onEpisodeSelected = { episodeSlug, startTimeMillis ->
                        var playerRoute = Screens.Player.withArgs(episodeSlug)
                        if (startTimeMillis > 0L) {
                            playerRoute += "?${PlayerScreen.INITIAL_SEEK_TIME_MILLIS_KEY}=${startTimeMillis}"
                        }
                        navController.navigate(playerRoute)
                    },
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    }
                )
            }
            composable(route = Screens.Dashboard()) {
                DashboardScreen(
                    openGenreList = { genreName ->
                        navController.navigate(
                            Screens.AnimesGenreList.withArgs(genreName)
                        )
                    },
                    openAnimeDetail = { animeId ->
                        navController.navigate(
                            Screens.AnimeDetails.withArgs(animeId)
                        )
                    },
                    openPlayer = { episodeSlug, startTimeMillis ->
                        var playerRoute = Screens.Player.withArgs(episodeSlug)
                        if (startTimeMillis > 0L) {
                            playerRoute += "?${PlayerScreen.INITIAL_SEEK_TIME_MILLIS_KEY}=${startTimeMillis}"
                        }
                        navController.navigate(playerRoute)
                    },
                    onBackPressed = onBackPressed,
                    isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                    resetIsComingBackFromDifferentScreen = {
                        isComingBackFromDifferentScreen = false
                    }
                )
            }
            composable(
                route = Screens.Player.name + "/{${PlayerScreen.EPISODE_ID_BUNDLE_KEY}}" +
                        "?${PlayerScreen.INITIAL_SEEK_TIME_MILLIS_KEY}={${PlayerScreen.INITIAL_SEEK_TIME_MILLIS_KEY}}",
                arguments = listOf(
                    navArgument(PlayerScreen.EPISODE_ID_BUNDLE_KEY) {
                        type = NavType.StringType
                    },
                    navArgument(PlayerScreen.INITIAL_SEEK_TIME_MILLIS_KEY) {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                PlayerScreen(
                    onBackPressed = {
                        if (!navController.popBackStack()) {
                            onBackPressed()
                        }
                    }
                )
            }
        }
    )
}
