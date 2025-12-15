package com.example.ngajitime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_sesi_ngaji")
data class SesiNgaji(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val tanggalSesi: Long,      // Waktu user klik "Selesai" (System.currentTimeMillis)
    val durasiDetik: Long,      // Berapa lama timer berjalan (Penting untuk Smart Estimation)
    val halamanSelesai: Int,    // Input user: "Dapat 5 halaman"
    val mulaiHalaman: Int = 0,  // Opsional: Halaman awal
    val akhirHalaman: Int = 0,  // Opsional: Halaman akhir

    val isFokus: Boolean = true, // True = Tidak keluar aplikasi, False = Terdistraksi
    val catatan: String = ""    // User note: "Alhamdulillah lancar"
)