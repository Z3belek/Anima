package com.aokaze.anima.data.repositories

import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : AnimeRepository {}