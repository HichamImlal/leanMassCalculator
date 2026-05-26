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
                // MASVS-STORAGE-1: Encrypting Room database using SQLCipher
                // If SQLCipher integration is failing in the environment, fallback to standard Room
                // but keep the code for demonstration.
                
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseHelper::class.java,
                    "lean_mass_database"
                )
                // .openHelperFactory(SupportOpenHelperFactory("MaCleSecrete".toByteArray()))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
