package com.app.leanmass.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.leanmass.model.LBMResult

@Database(entities = [LBMResult::class], version = 1, exportSchema = false)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun leanMassDao(): LeanMassDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getDatabase(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseHelper::class.java,
                    "lean_mass_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
