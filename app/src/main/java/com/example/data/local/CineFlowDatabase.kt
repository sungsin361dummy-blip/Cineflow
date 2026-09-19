package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CharacterEntity::class,
        LocationEntity::class,
        UniverseBibleEntity::class,
        SceneShotEntity::class,
        SceneBridgeEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class CineFlowDatabase : RoomDatabase() {
    abstract fun dao(): CineFlowDao

    companion object {
        @Volatile
        private var INSTANCE: CineFlowDatabase? = null

        fun getDatabase(context: Context): CineFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CineFlowDatabase::class.java,
                    "cineflow_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
