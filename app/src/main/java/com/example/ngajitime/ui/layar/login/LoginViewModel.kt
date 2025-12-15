package com.example.ngajitime.ui.layar.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.TargetUser
import com.example.ngajitime.data.repository.NgajiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: NgajiRepository
) : ViewModel() {

    // --- STATE UNTUK WIZARD (Langkah 1-5) ---
    private val _step = MutableStateFlow(1)
    val step: StateFlow<Int> = _step

    // --- PENAMPUNG JAWABAN SEMENTARA ---
    var namaUser = ""
    var emailUser: String? = null
    var frekuensi = ""
    var levelBaca = "LANCAR" // Default
    var kendala = ""
    var tujuan = ""

    // Data Target
    var modeTarget = "WAKTU"
    var nilaiTarget = 15

    // Fungsi Pindah Langkah
    fun nextStep() { _step.value += 1 }
    fun prevStep() { if (_step.value > 1) _step.value -= 1 }

    fun setLoginGoogle(nama: String, email: String) {
        namaUser = nama
        emailUser = email
        // Langsung lompat ke Step 2 (Lewati input nama manual)
        _step.value = 2
    }

    // --- FUNGSI FINAL: SIMPAN KE DATABASE ---
    fun simpanDataUser(onSukses: () -> Unit) {
        viewModelScope.launch {
            // 1. Tentukan Kecepatan (Detik per Ayat)
            val detikPerAyat = when (levelBaca) {
                "PEMULA" -> 25 // Terbata-bata
                "LANCAR" -> 15 // Standar
                "MAHIR" -> 10  // Cepat
                else -> 15
            }

            // 2. Hitung Target Harian
            var targetHarian = 0
            if (modeTarget == "WAKTU") {
                // Rumus: (Menit * 60) / DetikPerAyat
                targetHarian = (nilaiTarget * 60) / detikPerAyat
            } else {
                // Rumus: 6236 Ayat / Hari
                targetHarian = 6236 / (if (nilaiTarget > 0) nilaiTarget else 1)
            }

            // 3. Buat Object User
            val userBaru = TargetUser(
                id = 1,
                namaUser = namaUser,
                email = emailUser,
                frekuensiAwal = frekuensi,
                kendalaUtama = kendala,
                tujuanUtama = tujuan,
                levelBaca = levelBaca,
                modeTarget = modeTarget,
                waktuLuangMenit = if (modeTarget == "WAKTU") nilaiTarget else 0,
                durasiTargetHari = if (modeTarget == "DEADLINE") nilaiTarget else 0,
                targetAyatHarian = targetHarian
            )

            // 4. Simpan
            repository.saveTarget(userBaru)
            onSukses()
        }
    }
}