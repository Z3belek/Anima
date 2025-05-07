package com.aokaze.anima.data.util

object StringConstants {
    object Assets {
        const val Genres = "genres.json"
    }
    object Composable {
        object ContentDescription {
            fun animePoster(animeName: String) = "Poster de $animeName"
            const val AnimesCarousel = "Animes Carousel"
            const val SearchButton = "Boton de busqueda"
        }

        const val HomeScreenNewTitle = "Agregados Recientemente"
        const val HomeScreenUpdatedAnimeTitle = "Actualizaciones Recientes"

        const val PlayerControlClosedCaptionsButton = "Subtítulos"
        const val PlayerControlSettingsButton = "Configuración"
        const val PlayerControlSkipNextButton = "Siguiente episodio"
        const val PlayerControlSkipPreviousButton = "Episodio anterior"
        const val PlayerControlPlaylistButton = "Lista de reproducción"
        const val PlayerControlPlayPauseButton = "Reproducir/Pausar"
    }
}