package com.example.ngajitime.ui.layar.sesi

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.SesiNgaji
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.data.repository.NgajiRepository
import com.example.ngajitime.service.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SesiViewModel @Inject constructor(
    private val repository: NgajiRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val waktuTersisa: StateFlow<Long> = TimerService.waktuTersisa
    val statusTimer: StateFlow<TimerService.TimerStatus> = TimerService.statusTimer

    val listSurah: StateFlow<List<SurahProgress>> = repository.allSurah
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun mulaiTimer(menit: Int) {
        val durasiDetik = menit * 60L
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_DURASI, durasiDetik)
        }
        context.startService(intent)
    }

    fun batalkanTimer() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun simpanSesiTerintegrasi(
        durasiDetik: Long,
        surah: SurahProgress,
        ayatTerakhirBaru: Int,
        onSimpanSukses: () -> Unit
    ) {
        viewModelScope.launch {

            val ayatValid = ayatTerakhirBaru.coerceIn(0, surah.totalAyat)

            val selisihAyatDibaca = (ayatValid - surah.ayatTerakhirDibaca).coerceAtLeast(0)

            val waktuSekarang = System.currentTimeMillis()

            repository.updateProgressSurah(surah.nomorSurah, ayatValid, surah.totalAyat)

            if (durasiDetik > 0 || selisihAyatDibaca > 0) {
                val sesiBaru = SesiNgaji(
                    tanggalSesi = waktuSekarang,
                    durasiDetik = durasiDetik,
                    halamanSelesai = selisihAyatDibaca,
                    isFokus = true
                )
                repository.insertSesi(sesiBaru)

                if (selisihAyatDibaca > 0) {
                    repository.tambahTotalAyat(selisihAyatDibaca)
                    cekDanUpdateStreak(waktuSekarang)
                }
            }
            onSimpanSukses()
        }
    }

    private suspend fun cekDanUpdateStreak(waktuSekarang: Long) {
        val user = repository.getUserTargetOneShot() ?: return
        val calToday = Calendar.getInstance().apply { timeInMillis = waktuSekarang }
        val calLast = Calendar.getInstance().apply { timeInMillis = user.lastStreakDate }

        if (isSameDay(calToday, calLast)) return

        calLast.add(Calendar.DAY_OF_YEAR, 1)
        if (isSameDay(calToday, calLast)) {
            repository.updateStreak(user.currentStreak + 1, waktuSekarang)
        } else {
            repository.updateStreak(1, waktuSekarang)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}