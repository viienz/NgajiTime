package com.example.ngajitime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ngajitime.data.local.entity.NotifikasiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifikasiDao {
    // Ambil semua notifikasi, urutkan dari yang terbaru
    @Query("SELECT * FROM notifikasi ORDER BY timestamp DESC")
    fun getAllNotifikasi(): Flow<List<NotifikasiEntity>>

    // Hitung berapa yang belum dibaca
    @Query("SELECT COUNT(*) FROM notifikasi WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    // Masukkan notifikasi baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifikasi(notif: NotifikasiEntity)

    // Tandai semua sudah dibaca
    @Query("UPDATE notifikasi SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()

    // Hapus notifikasi lama
    @Query("DELETE FROM notifikasi")
    suspend fun clearAll()
}