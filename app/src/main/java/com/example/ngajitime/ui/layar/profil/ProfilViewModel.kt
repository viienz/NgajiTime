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

    // 1. AMBIL DATA USER (Real-time)
    val userTarget = repository.getUserTarget()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout(onLogoutSukses: () -> Unit) {
        viewModelScope.launch {
            repository.hapusUser()
            onLogoutSukses()
        }
    }
    fun updateTarget(mode: String, nilai: Int) {
        val currentUser = userTarget.value ?: return

        viewModelScope.launch {
            var targetAyatBaru = 0
            var durasiHari = 0
            var waktuMenit = 0

            if (mode == "WAKTU") {
                // Mode Santai
                waktuMenit = nilai

                // Rumus Kecepatan Baca
                val detikPerAyat = 20

                targetAyatBaru = (nilai * 60) / detikPerAyat
            } else {
                // Mode Khatam
                durasiHari = nilai
                targetAyatBaru = if (nilai > 0) 6236 / nilai else 1
            }
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

    // 4. [BARU] FUNGSI STREAK FREEZE ❄️
    fun toggleStreakFreeze(isAktif: Boolean) {
        // 1. Ambil data user yang sedang tampil di layar
        val currentUser = userTarget.value ?: return

        viewModelScope.launch {
            // 2. Kita "Copy" data user tersebut, tapi ubah bagian isStreakFreeze saja
            // 3. Lalu Simpan ulang (Menimpa data lama dengan ID yang sama)
            repository.saveTarget(
                currentUser.copy(isStreakFreeze = isAktif)
            )
        }
    }
}