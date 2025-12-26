package com.example.ngajitime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ngajitime.data.local.entity.TargetUser
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {
    @Query("SELECT * FROM target_user LIMIT 1")
    fun getUserTarget(): Flow<TargetUser?>

    @Query("SELECT * FROM target_user LIMIT 1")
    suspend fun getUserTargetOneShot(): TargetUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTarget(user: TargetUser)

    // [OPSI 1] Hapus user (Nama lama)
    @Query("DELETE FROM target_user")
    suspend fun deleteUser()

    // [OPSI 2 - BARU] Hapus Total (Nama standar 'Nuke' biar sama dg Repository)
    @Query("DELETE FROM target_user")
    suspend fun nukeTable()
}