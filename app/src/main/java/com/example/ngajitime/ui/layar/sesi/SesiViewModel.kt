package com.example.ngajitime.ui.layar.sesi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.SesiNgaji
import com.example.ngajitime.data.repository.NgajiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SesiViewModel @Inject constructor(
    private val repository: NgajiRepository
) : ViewModel() {

    // Ubah nama parameter biar jelas: jumlahAyat
    fun simpanSesi(durasiDetik: Long, jumlahAyat: Int, onSimpanSukses: () -> Unit) {
        viewModelScope.launch {
            // 1. Simpan ke Tabel Riwayat
            val sesiBaru = SesiNgaji(
                tanggalSesi = System.currentTimeMillis(),
                durasiDetik = durasiDetik,
                halamanSelesai = jumlahAyat, // Kita pakai kolom ini untuk simpan jumlah ayat
                isFokus = true
            )
            repository.insertSesi(sesiBaru)

            // 2. Update Progress Total (PERBAIKAN UTAMA DI SINI)
            // Panggil fungsi yang baru: addProgressAyat
            repository.addProgressAyat(jumlahAyat)

            // 3. Selesai
            onSimpanSukses()
        }
    }
}