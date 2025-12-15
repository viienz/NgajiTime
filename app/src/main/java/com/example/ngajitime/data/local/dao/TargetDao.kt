package com.example.ngajitime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ngajitime.data.local.entity.TargetUser
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {
    // Simpan Target Baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTarget(user: TargetUser)

    // Ambil Data Profil
    @Query("SELECT * FROM tabel_target_user WHERE id = 1 LIMIT 1")
    fun getUserTarget(): Flow<TargetUser?>

    // Update Streak
    @Query("UPDATE tabel_target_user SET currentStreak = :newStreak, lastDateLog = :todayDate WHERE id = 1")
    suspend fun updateStreak(newStreak: Int, todayDate: Long)

    // --- PERBAIKAN DI SINI ---
    // Ganti 'totalHalamanDibaca' menjadi 'totalAyatDibaca'
    @Query("UPDATE tabel_target_user SET totalAyatDibaca = totalAyatDibaca + :ayatBaru WHERE id = 1")
    suspend fun addProgressAyat(ayatBaru: Int)
}