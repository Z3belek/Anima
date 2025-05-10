package com.aokaze.anima.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aokaze.anima.data.dao.ResumeDao
import com.aokaze.anima.data.entities.Resume

@Database(entities = [Resume::class], version = 1, exportSchema = false)
abstract class AnimaDatabase : RoomDatabase() {
    abstract fun resumeDao(): ResumeDao
}