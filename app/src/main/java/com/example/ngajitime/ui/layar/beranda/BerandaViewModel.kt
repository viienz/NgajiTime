package com.example.ngajitime.ui.layar.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.TargetUser
import com.example.ngajitime.data.repository.NgajiRepository
import com.example.ngajitime.domain.EstimasiWaktuUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BerandaViewModel @Inject constructor(
    private val repository: NgajiRepository,
    private val estimasiWaktuUseCase: EstimasiWaktuUseCase
) : ViewModel() {

    // 1. DATA USER (Perbaikan di sini)
    // Dulu: repository.userTarget (variabel)
    // Sekarang: repository.getUserTarget() (fungsi)
    val userTarget: StateFlow<TargetUser?> = repository.getUserTarget()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 2. DATA PROGRESS HARIAN
    private val _halamanHariIni = MutableStateFlow(0)
    val halamanHariIni: StateFlow<Int> = _halamanHariIni

    init {
        hitungProgressHariIni()

        // Cek data surah otomatis saat beranda dimuat
        viewModelScope.launch {
            repository.cekDanIsiDataSurah()
        }
    }

    // Logic Hitung Progress Harian (Mulai jam 00:00 sampai 23:59)
    private fun hitungProgressHariIni() {
        val calendar = Calendar.getInstance()

        // Set ke Jam 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis

        // Set ke Jam 23:59:59
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis

        viewModelScope.launch {
            repository.getHalamanHariIni(startOfDay, endOfDay).collect { jumlah ->
                _halamanHariIni.value = jumlah ?: 0
            }
        }
    }

    // Fitur Smart Coach: Hitung estimasi waktu
    fun hitungEstimasi(menitTersedia: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val pesan = estimasiWaktuUseCase.hitungTarget(menitTersedia)
            onResult(pesan)
        }
    }
}