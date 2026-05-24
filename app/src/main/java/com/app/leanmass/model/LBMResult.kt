package com.app.leanmass.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lean_mass_records")
data class LBMResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val poids: Double = 0.0,
    val taille: Double = 0.0,
    val sexe: String = "",
    val lbmResultat: Double = 0.0,
    val dateTimestamp: Long = System.currentTimeMillis()
)
