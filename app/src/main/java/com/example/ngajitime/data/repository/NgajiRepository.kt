package com.example.ngajitime.data.repository

import com.example.ngajitime.data.local.dao.SesiDao
import com.example.ngajitime.data.local.dao.SurahDao
import com.example.ngajitime.data.local.dao.TargetDao
import com.example.ngajitime.data.local.entity.SesiNgaji
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.data.local.entity.TargetUser
import com.example.ngajitime.util.DataSurah
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NgajiRepository @Inject constructor(
    private val sesiDao: SesiDao,
    private val targetDao: TargetDao,
    private val surahDao: SurahDao
) {
    // --- 1. DATA FLOW (ALIRAN DATA) ---
    val allSurah: Flow<List<SurahProgress>> = surahDao.getAllSurah()
    val allRiwayatSesi: Flow<List<SesiNgaji>> = sesiDao.getAllSesi()

    // --- 2. FUNGSI USER & TARGET ---
    // (Kita pakai fun, bukan val, agar tidak bentrok/clash)
    fun getUserTarget(): Flow<TargetUser?> = targetDao.getUserTarget()

    suspend fun saveTarget(user: TargetUser) = targetDao.saveTarget(user)

    suspend fun updateStreak(newStreak: Int, today: Long) = targetDao.updateStreak(newStreak, today)

    suspend fun addProgressAyat(ayat: Int) = targetDao.addProgressAyat(ayat)

    // --- 3. FUNGSI PROGRESS SURAH ---
    suspend fun updateProgressSurah(id: Int, ayat: Int, total: Int) {
        val isKhatam = ayat >= total
        surahDao.updateProgress(id, ayat, System.currentTimeMillis(), isKhatam)
    }

    suspend fun cekDanIsiDataSurah() {
        // Cek apakah database kosong
        val cekData = surahDao.getSurahById(1)
        if (cekData == null) {
            surahDao.insertAll(DataSurah.list114Surah)
        }
    }

    fun getAllSurahProgress(): Flow<List<SurahProgress>> = surahDao.getAllSurah()

    // --- 4. FUNGSI SESI NGAJI ---
    suspend fun insertSesi(sesi: SesiNgaji) = sesiDao.insertSesi(sesi)

    // Hitung pencapaian hari ini (untuk Beranda)
    fun getHalamanHariIni(start: Long, end: Long): Flow<Int?> = sesiDao.getTotalHalamanHariIni(start, end)

    // Logic Cerdas (Estimasi Waktu)
    suspend fun getRataRataKecepatan(): Long {
        return sesiDao.getAverageSecondsPerPage() ?: 180L
    }
    suspend fun hapusUser() {
        targetDao.deleteUser()
    }
    suspend fun setStreakFreeze(isActive: Boolean) {
        targetDao.updateStreakFreeze(isActive)
    }
}