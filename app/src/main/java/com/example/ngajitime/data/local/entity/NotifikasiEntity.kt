package com.example.ngajitime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifikasi")
data class NotifikasiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val pesan: String,
    val tipe: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)