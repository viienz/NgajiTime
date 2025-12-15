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

    // 2. Ambil Semua History (Reactive Flow untuk UI)
    @Query("SELECT * FROM tabel_sesi_ngaji ORDER BY tanggalSesi DESC")
    fun getAllSesi(): Flow<List<SesiNgaji>>

    // 3. FITUR UTAMA: Ambil Total Halaman Hari Ini (Untuk Progress Bar Beranda)
    @Query("SELECT SUM(halamanSelesai) FROM tabel_sesi_ngaji WHERE tanggalSesi BETWEEN :startOfDay AND :endOfDay")
    fun getTotalHalamanHariIni(startOfDay: Long, endOfDay: Long): Flow<Int?>
    // Return nullable Int karena kalau belum baca hasilnya null

    // 4. SMART ESTIMATION: Hitung Rata-Rata Kecepatan User (Detik per Halaman)
    // Rumus: Total Durasi Seluruhnya / Total Halaman Seluruhnya
    @Query("SELECT (SUM(durasiDetik) / SUM(halamanSelesai)) FROM tabel_sesi_ngaji WHERE halamanSelesai > 0")
    suspend fun getAverageSecondsPerPage(): Long?
}