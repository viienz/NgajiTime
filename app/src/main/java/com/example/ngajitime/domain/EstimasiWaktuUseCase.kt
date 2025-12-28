package com.example.ngajitime.domain

import com.example.ngajitime.data.repository.NgajiRepository
import javax.inject.Inject

class EstimasiWaktuUseCase @Inject constructor(
    private val repository: NgajiRepository
) {
    suspend fun hitungTarget(menitTersedia: Int): String {
        val kecepatanUser = repository.getRataRataKecepatan()
        val detikPerAyat = if (kecepatanUser > 60) 20L else kecepatanUser
        val speedValid = if (detikPerAyat < 1) 20L else detikPerAyat
        val totalDetikPunya = menitTersedia * 60
        val dapatAyat = totalDetikPunya / speedValid

        return "Insya Allah dapat ±$dapatAyat Ayat"
    }
}