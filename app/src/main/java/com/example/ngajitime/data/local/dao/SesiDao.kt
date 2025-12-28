package com.example.ngajitime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ngajitime.data.local.entity.SesiNgaji
import kotlinx.coroutines.flow.Flow

@Dao
interface SesiDao {
    //menyimpan Sesi Baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSesi(sesi: SesiNgaji)

    //mengambil Semua History
    @Query("SELECT * FROM tabel_sesi_ngaji ORDER BY tanggalSesi DESC")
    fun getAllSesi(): Flow<List<SesiNgaji>>

    //Total Halaman Hari Ini
    @Query("SELECT SUM(halamanSelesai) FROM tabel_sesi_ngaji WHERE tanggalSesi BETWEEN :startOfDay AND :endOfDay")
    fun getTotalHalamanHariIni(startOfDay: Long, endOfDay: Long): Flow<Int?>

    //menghitung Rata-Rata Kecepatan
    @Query("SELECT (SUM(durasiDetik) / SUM(halamanSelesai)) FROM tabel_sesi_ngaji WHERE halamanSelesai > 0")
    suspend fun getAverageSecondsPerPage(): Long?

    @Query("DELETE FROM tabel_sesi_ngaji")
    suspend fun nukeTable()
}