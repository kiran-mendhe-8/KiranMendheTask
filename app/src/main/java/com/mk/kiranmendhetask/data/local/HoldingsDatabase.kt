package com.mk.kiranmendhetask.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [HoldingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HoldingsDatabase : RoomDatabase() {
    abstract fun holdingsDao(): HoldingsDao
    
    companion object {
        @Volatile
        private var INSTANCE: HoldingsDatabase? = null
        
        fun getDatabase(context: Context): HoldingsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HoldingsDatabase::class.java,
                    "holdings_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}