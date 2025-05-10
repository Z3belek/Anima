package com.aokaze.anima.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.aokaze.anima.presentation.screens.Screens
import com.aokaze.anima.presentation.screens.animes.AnimeScreen
import com.aokaze.anima.presentation.screens.genres.GenresScreen
import com.aokaze.anima.presentation.screens.home.HomeScreen
import com.aokaze.anima.presentation.screens.movies.MovieScreen
import com.aokaze.anima.presentation.screens.search.SearchScreen
import com.aokaze.anima.presentation.utils.Padding
import kotlinx.coroutines.launch

val ParentPadding = PaddingValues(vertical = 0.dp, horizontal = 0.dp)

@Composable
fun rememberChildPadding(direction: LayoutDirection = LocalLayoutDirection.current): Padding {
    return remember {
        Padding(
            start = ParentPadding.calculateStartPadding(direction),
            top = ParentPadding.calculateTopPadding(),
            end = ParentPadding.calculateEndPadding(direction),
            bottom = ParentPadding.calculateBottomPadding()
        )
    }
}

val DrawerScreens = Screens.entries.toList().filter { it.isTabItem }

val closeDrawerWidth = 80.dp

@Composable
fun DashboardScreen(
    openGenreList: (genreId: String) -> Unit,
    openAnimeDetail: (animeId: String) -> Unit,
    openPlayer: (episodeSlug: String, startTimeMillis: Long) -> Unit,
    isComingBackFromDifferentScreen: Boolean,
    resetIsComingBackFromDifferentScreen: () -> Unit,
    onBackPressed: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/")

    var selectedIndex by remember(currentRoute) {
        mutableIntStateOf(DrawerScreens.indexOfFirst { it.name == currentRoute }.coerceAtLeast(0))
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(navController) {
        if (currentRoute == null && DrawerScreens.isNotEmpty()) {
            navController.navigate(DrawerScreens[0]()) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val layoutDirection = LocalLayoutDirection.current
    val bodyContentPadding = remember(closeDrawerWidth, ParentPadding, layoutDirection) {
        PaddingValues(
            start = closeDrawerWidth + ParentPadding.calculateStartPadding(layoutDirection),
            top = ParentPadding.calculateTopPadding(),
            end = ParentPadding.calculateEndPadding(layoutDirection),
            bottom = ParentPadding.calculateBottomPadding()
        )
    }

    val customNavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
        focusedContainerColor = Color.White.copy(alpha = 0.05f),
        selectedContainerColor = Color.Transparent,
    )

    BackPressHandledArea(
        onBackPressed = {
            if (drawerState.currentValue == DrawerValue.Open) {
                scope.launch { drawerState.setValue(DrawerValue.Closed) }
            } else {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    onBackPressed()
                }
            }
        }
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = { drawerValue ->
                Column(
                    Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.0f)
                                )
                            )
                        )
                        .fillMaxHeight()
                        .padding(12.dp)
                        .selectableGroup(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    DrawerScreens.forEachIndexed { index, screen ->
                        NavigationDrawerItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                navController.navigate(screen()) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch { drawerState.setValue(DrawerValue.Closed) }
                            },
                            colors = customNavigationDrawerItemColors,
                            leadingContent = {
                                screen.tabIcon?.let { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = screen.name,
                                        tint = if (selectedIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        ) {
                            Text(
                                text = screen.name,
                                color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            scrimBrush = Brush.horizontalGradient(listOf(Color.Black, Color.Transparent)),
            content = {
                Body(
                    openGenreList = openGenreList,
                    openAnimeDetail = openAnimeDetail,
                    openPlayer = openPlayer,
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = bodyContentPadding
                )
            }
        )
    }
}

@Composable
private fun BackPressHandledArea(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) = Box(
    modifier = modifier.onPreviewKeyEvent {
        if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
            onBackPressed()
            true
        } else {
            false
        }
    },
    content = content
)

@Composable
private fun Body(
    openGenreList: (categoryId: String) -> Unit,
    openAnimeDetail: (movieId: String) -> Unit,
    openPlayer: (episodeSlug: String, startTimeMillis: Long) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    contentPadding: PaddingValues
) {
    NavHost(
        modifier = modifier.padding(contentPadding),
        navController = navController,
        startDestination = DrawerScreens.firstOrNull()?.invoke() ?: Screens.Inicio(),
    ) {
        composable(Screens.Inicio()) {
            HomeScreen(
                onAnimeClick = { anime -> openAnimeDetail(anime.id) },
                onEpisodeClick = openPlayer,
            )
        }
        composable(Screens.Animes()) {
            AnimeScreen(
                onAnimeClick = { anime -> openAnimeDetail(anime.id) },
            )
        }
        composable(Screens.Peliculas()) {
            MovieScreen(
                onAnimeClick = { anime -> openAnimeDetail(anime.id) },
            )
        }
        composable(Screens.Generos()) {
            GenresScreen(
                onGenreClick = openGenreList,
            )
        }
        composable(Screens.Search()) {
            SearchScreen(
                onAnimeClick = { anime -> openAnimeDetail(anime.id) },
            )
        }
    }
}