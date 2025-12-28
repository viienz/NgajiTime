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

    val userTarget = repository.getUserTarget()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout(onLogoutSukses: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllLocalData()

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

    fun toggleStreakFreeze(isAktif: Boolean) {
        val currentUser = userTarget.value ?: return

        viewModelScope.launch {
            repository.saveTarget(
                currentUser.copy(isStreakFreeze = isAktif)
            )
        }
    }

    //Edit nama
    fun updateNama(namaBaru: String) {
        val currentUser = userTarget.value ?: return
        viewModelScope.launch {
            repository.saveTarget(
                currentUser.copy(namaUser = namaBaru)
            )
        }
    }
}