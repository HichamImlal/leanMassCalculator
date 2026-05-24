package com.app.leanmass.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.leanmass.model.LBMResult

@Dao
interface LeanMassDao {
    @Insert
    suspend fun insertRecord(record: LBMResult)

    @Query("SELECT * FROM lean_mass_records ORDER BY dateTimestamp DESC")
    suspend fun getAllRecords(): List<LBMResult>

    @Query("DELETE FROM lean_mass_records")
    suspend fun deleteAllRecords()
}
