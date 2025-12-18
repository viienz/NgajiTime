package com.example.ngajitime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "target_user")
data class TargetUser(
    @PrimaryKey val id: Int = 1,
    val namaUser: String,
    val email: String? = null,


    val frekuensiAwal: String,
    val kendalaUtama: String,
    val tujuanUtama: String,

    // --- DATA TEKNIS ---
    val levelBaca: String,
    val modeTarget: String,
    val waktuLuangMenit: Int,
    val durasiTargetHari: Int,
    val targetAyatHarian: Int,
    val isStreakFreeze: Boolean = false,

    // --- PROGRESS ---
    val totalAyatDibaca: Int = 0,
    val currentStreak: Int = 0,
    val lastStreakDate: Long = 0L
)