package com.example.ngajitime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ngajitime.data.local.entity.SesiNgaji
import kotlinx.coroutines.flow.Flow

@Dao
interface SesiDao {
    // 1. Simpan Sesi Baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSesi(sesi: SesiNgaji)

    // 2. Ambil Semua History
    @Query("SELECT * FROM tabel_sesi_ngaji ORDER BY tanggalSesi DESC")
    fun getAllSesi(): Flow<List<SesiNgaji>>

    // 3. Ambil Total Halaman Hari Ini
    @Query("SELECT SUM(halamanSelesai) FROM tabel_sesi_ngaji WHERE tanggalSesi BETWEEN :startOfDay AND :endOfDay")
    fun getTotalHalamanHariIni(startOfDay: Long, endOfDay: Long): Flow<Int?>

    // 4. Hitung Rata-Rata Kecepatan
    @Query("SELECT (SUM(durasiDetik) / SUM(halamanSelesai)) FROM tabel_sesi_ngaji WHERE halamanSelesai > 0")
    suspend fun getAverageSecondsPerPage(): Long?

    // [BARU] HAPUS SEMUA RIWAYAT (Untuk Logout)
    @Query("DELETE FROM tabel_sesi_ngaji")
    suspend fun nukeTable()
}