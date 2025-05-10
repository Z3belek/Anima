package com.aokaze.anima.data.util

object StringConstants {
    object Assets {
        const val GENRES = "genres.json"
    }
    object Composable {
        object ContentDescription {
            fun animePoster(animeName: String) = "Poster de $animeName"
            const val ANIME_CAROUSEL = "Animes Carousel"
            const val SEARCH_BUTTON = "Boton de busqueda"
        }

        const val NEW_ANIME = "Agregados Recientemente"
        const val UPDATE_RECENTLY = "Actualizaciones Recientes"

        const val CLOSED_CAPTIONS = "Subtítulos"
        const val SETTINGS = "Configuración"
        const val SKIP_NEXT = "Siguiente episodio"
        const val SKIP_PREVIOUS = "Episodio anterior"
        const val PLAYLIST = "Lista de reproducción"
        const val PLAY_PAUSE = "Reproducir/Pausar"
    }
}