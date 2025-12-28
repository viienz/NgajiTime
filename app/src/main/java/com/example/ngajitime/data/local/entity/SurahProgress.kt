package com.example.ngajitime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_surah_progress")
data class SurahProgress(
    @PrimaryKey(autoGenerate = false)
    val nomorSurah: Int,

    val namaSurah: String,
    val artiSurah: String,
    val totalAyat: Int,

    //Progres User
    val ayatTerakhirDibaca: Int = 0,
    val isKhatam: Boolean = false,
    val lastUpdated: Long = 0
)