package com.example.ngajitime.domain

import com.example.ngajitime.data.repository.NgajiRepository
import javax.inject.Inject

class EstimasiWaktuUseCase @Inject constructor(
    private val repository: NgajiRepository
) {
    suspend fun hitungTarget(menitTersedia: Int): String {
        // 1. Ambil data kecepatan user dari database
        // (Repository mengembalikan "Detik per Unit". Sekarang unitnya adalah Ayat)
        val kecepatanUser = repository.getRataRataKecepatan() // Detik/Ayat

        // 2. Tentukan kecepatan yang masuk akal
        // Jika user baru (kecepatan default 180 detik/3 menit dari kode lama),
        // itu terlalu lama untuk 1 ayat. Kita koreksi jadi 20 detik/ayat.
        val detikPerAyat = if (kecepatanUser > 60) 20L else kecepatanUser

        // Hindari pembagian nol
        val speedValid = if (detikPerAyat < 1) 20L else detikPerAyat

        // 3. Hitung Estimasi
        val totalDetikPunya = menitTersedia * 60
        val dapatAyat = totalDetikPunya / speedValid

        return "Insya Allah dapat ±$dapatAyat Ayat"
    }
}