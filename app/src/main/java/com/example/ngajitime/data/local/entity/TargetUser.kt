package com.example.ngajitime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_target_user")
data class TargetUser(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,

    val namaUser: String = "Fulan",

    // --- Data Target Dinamis ---
    val modeTarget: String = "WAKTU", // "WAKTU" (Santai) atau "DEADLINE" (Kejar Khatam)
    val durasiTargetHari: Int = 0,    // Jika pilih deadline (misal 30 hari)
    val waktuLuangMenit: Int = 15,    // Jika pilih waktu luang

    // --- Target Utama (AYAT) ---
    val targetAyatHarian: Int = 50,   // GANTI DARI HALAMAN KE AYAT

    // --- Statistik ---
    val totalAyatDibaca: Int = 0,     // Total seumur hidup
    val currentStreak: Int = 0,
    val lastDateLog: Long = 0
)