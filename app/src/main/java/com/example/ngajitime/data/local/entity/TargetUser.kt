package com.example.ngajitime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "target_user")
data class TargetUser(
    @PrimaryKey val id: Int = 1,
    // --- DATA USER ---
    val namaUser: String = "",
    val email: String? = null,

    val frekuensiAwal: String = "",
    val kendalaUtama: String = "",
    val tujuanUtama: String = "",

    // --- DATA TEKNIS ---
    val levelBaca: String = "PEMULA",
    val modeTarget: String = "WAKTU",
    val waktuLuangMenit: Int = 15,
    val durasiTargetHari: Int = 30, // Default 30 menit
    val targetAyatHarian: Int = 10, // Default 10 ayat
    val isStreakFreeze: Boolean = false,

    // --- PROGRESS ---
    val totalAyatDibaca: Int = 0,
    val currentStreak: Int = 0,
    val lastStreakDate: Long = 0L
) {

    constructor() : this(1, "", null, "", "", "", "PEMULA", "WAKTU", 15, 30, 10, false, 0, 0, 0L)
}