package com.example.ngajitime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_surah_progress")
data class SurahProgress(
    @PrimaryKey(autoGenerate = false)
    val nomorSurah: Int, // 1 sampai 114

    val namaSurah: String,      // "Al-Fatihah"
    val artiSurah: String,      // "Pembukaan"
    val totalAyat: Int,         // 7

    // --- Progres User ---
    val ayatTerakhirDibaca: Int = 0, // User input: "Sampai ayat 5"
    val isKhatam: Boolean = false,
    val lastUpdated: Long = 0
)