package com.aokaze.anima.presentation

import android.util.Log
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
                    navArgument(AnimesGenreListScreen.GenreIdBundleKey) {
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
                    navArgument(MediaDetailsScreen.MediaIdBundleKey) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                MediaDetailsScreen(
                    onEpisodeSelected = { episodeId ->
                        Log.d("AppNavigation", "Navigating to VideoPlayer with episodeId: $episodeId")
                        navController.navigate(Screens.Player.withArgs(episodeId))
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
                    onBackPressed = onBackPressed,
                    isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                    resetIsComingBackFromDifferentScreen = {
                        isComingBackFromDifferentScreen = false
                    }
                )
            }
            composable(
                route = Screens.Player(),
                arguments = listOf(
                    navArgument(PlayerScreen.EpisodeIdBundleKey) {
                        type = NavType.StringType
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
