package com.example.ngajitime.ui.layar.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.repository.NgajiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilViewModel @Inject constructor(
    private val repository: NgajiRepository
) : ViewModel() {

    // 1. Ambil data user secara real-time
    val userTarget = repository.getUserTarget()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 2. Fungsi Logout (Hapus Data)
    fun logout(onLogoutSukses: () -> Unit) {
        viewModelScope.launch {
            repository.hapusUser()
            onLogoutSukses()
        }
    }

    // 3. Fungsi Ganti Nama (FITUR ANDA TETAP AMAN) ✅
    fun updateNama(namaBaru: String) {
        val currentUser = userTarget.value ?: return
        viewModelScope.launch {
            repository.saveTarget(currentUser.copy(namaUser = namaBaru))
        }
    }

    // 4. Fungsi Ganti Target (VERSI UPGRADE LEBIH PINTAR) 🧠
    // Menggunakan logika 'Level Baca' agar targetnya realistis
    fun updateTarget(mode: String, nilai: Int) {
        val currentUser = userTarget.value ?: return

        viewModelScope.launch {
            var targetAyatBaru = 0
            var durasiHari = 0
            var waktuMenit = 0

            if (mode == "WAKTU") {
                // Mode Santai: Input = Menit
                waktuMenit = nilai

                // Cek kecepatan baca user (data dari login awal)
                val detikPerAyat = if (currentUser.levelBaca == "PEMULA") 25 else 15

                // Rumus: (Menit * 60) / DetikPerAyat
                // Contoh: 15 menit lancar = (15*60)/15 = 60 Ayat.
                // Contoh: 15 menit pemula = (15*60)/25 = 36 Ayat.
                targetAyatBaru = (nilai * 60) / detikPerAyat

            } else {
                // Mode Khatam: Input = Hari
                // Rumus: 6236 Ayat / Hari
                durasiHari = nilai
                targetAyatBaru = if (nilai > 0) 6236 / nilai else 1
            }

            // Simpan perubahan ke database
            repository.saveTarget(
                currentUser.copy(
                    modeTarget = mode,
                    waktuLuangMenit = waktuMenit,
                    durasiTargetHari = durasiHari,
                    targetAyatHarian = targetAyatBaru
                )
            )
        }
    }
}