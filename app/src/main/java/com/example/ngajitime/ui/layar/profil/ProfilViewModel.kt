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

    // Ambil data user secara real-time
    val userTarget = repository.getUserTarget()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Fungsi Ganti Nama
    fun updateNama(namaBaru: String) {
        val currentUser = userTarget.value ?: return
        viewModelScope.launch {
            repository.saveTarget(currentUser.copy(namaUser = namaBaru))
        }
    }

    // Fungsi Ganti Target (PENTING)
    fun updateTarget(mode: String, nilai: Int) {
        val currentUser = userTarget.value ?: return
        viewModelScope.launch {
            var targetAyatBaru = 0
            var durasiHari = 0
            var waktuMenit = 0

            if (mode == "WAKTU") {
                // Mode Santai: Input = Menit
                // Rumus: 1 Menit = 3 Ayat (Standar)
                waktuMenit = nilai
                targetAyatBaru = nilai * 3
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