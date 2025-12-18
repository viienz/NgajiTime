package com.example.ngajitime.ui.layar.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.data.local.entity.TargetUser
import com.example.ngajitime.data.repository.NgajiRepository
import com.example.ngajitime.domain.EstimasiWaktuUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BerandaViewModel @Inject constructor(
    private val repository: NgajiRepository,
    private val estimasiWaktuUseCase: EstimasiWaktuUseCase
) : ViewModel() {

    // 1. DATA USER (TETAP SAMA) ✅
    val userTarget: StateFlow<TargetUser?> = repository.getUserTarget()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 2. PROGRESS HARIAN (TETAP SAMA + UPGRADE) ✅
    private val _halamanHariIni = MutableStateFlow(0)
    val halamanHariIni: StateFlow<Int> = _halamanHariIni

    // [BARU] Hitung Persentase (0-100%) untuk Lingkaran Progress
    // Kita gabungkan data 'halamanHariIni' dengan 'userTarget'
    val progressPersen: StateFlow<Int> = combine(_halamanHariIni, userTarget) { totalAyatHariIni, user ->
        if (user == null || user.targetAyatHarian == 0) {
            0
        } else {
            // JANGAN DIKALI 15 LAGI. 'totalAyatHariIni' itu sudah berupa Ayat.
            // Rumus: (Total Ayat / Target) * 100
            val persen = (totalAyatHariIni.toFloat() / user.targetAyatHarian.toFloat()) * 100
            persen.toInt().coerceIn(0, 100)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    val lastRead: StateFlow<SurahProgress?> = repository.getAllSurahProgress()
        .combine(MutableStateFlow(true)) { list, _ ->
            // GANTI 'lastReadTimestamp' JADI 'lastUpdated'
            list.maxByOrNull { it.lastUpdated }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    init {
        hitungProgressHariIni()

        // Cek data surah (TETAP SAMA)
        viewModelScope.launch {
            repository.cekDanIsiDataSurah()
        }
    }

    // Logic Hitung Progress (TETAP SAMA) ✅
    private fun hitungProgressHariIni() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis

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

    // Fitur Estimasi (TETAP SAMA) ✅
    fun hitungEstimasi(menitTersedia: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val pesan = estimasiWaktuUseCase.hitungTarget(menitTersedia)
            onResult(pesan)
        }
    }
}