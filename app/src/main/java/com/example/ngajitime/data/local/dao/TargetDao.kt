package com.example.ngajitime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ngajitime.data.local.entity.TargetUser
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {
    // Perhatikan: FROM target_user (Bukan tabel_target_user)
    @Query("SELECT * FROM target_user LIMIT 1")
    fun getUserTarget(): Flow<TargetUser?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTarget(user: TargetUser)

    @Query("UPDATE target_user SET currentStreak = :streak, lastStreakDate = :date WHERE id = 1")
    suspend fun updateStreak(streak: Int, date: Long)

    @Query("UPDATE target_user SET totalAyatDibaca = totalAyatDibaca + :ayat WHERE id = 1")
    suspend fun addProgressAyat(ayat: Int)

    @Query("DELETE FROM target_user")
    suspend fun deleteUser()
}