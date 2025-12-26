package com.example.ngajitime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ngajitime.data.local.entity.SurahProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    // Masukkan 114 Surah (Hanya sekali saat install)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(surahList: List<SurahProgress>)

    // Ambil Semua Surah
    @Query("SELECT * FROM tabel_surah_progress ORDER BY nomorSurah ASC")
    fun getAllSurah(): Flow<List<SurahProgress>>

    @Query("SELECT * FROM tabel_surah_progress WHERE nomorSurah = :id")
    suspend fun getSurahById(id: Int): SurahProgress?

    // Update Progres Bacaan
    @Query("UPDATE tabel_surah_progress SET ayatTerakhirDibaca = :ayatBaru, lastUpdated = :waktu, isKhatam = :khatam WHERE nomorSurah = :id")
    suspend fun updateProgress(id: Int, ayatBaru: Int, waktu: Long, khatam: Boolean)

    // Ambil Surah Terakhir Dibaca
    @Query("SELECT * FROM tabel_surah_progress WHERE lastUpdated > 0 ORDER BY lastUpdated DESC LIMIT 1")
    fun getTerakhirDibaca(): Flow<SurahProgress?>

    // [BARU] RESET SEMUA PROGRESS (Untuk Logout)
    // Kita set ayat jadi 0, khatam jadi false, dan waktu jadi 0
    @Query("UPDATE tabel_surah_progress SET ayatTerakhirDibaca = 0, isKhatam = 0, lastUpdated = 0")
    suspend fun resetProgress()
}