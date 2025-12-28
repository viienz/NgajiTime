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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class KalenderItemData(
    val hari: String,
    val tanggal: String,
    val isAdaSesi: Boolean,
    val isHariIni: Boolean
)

@HiltViewModel
class BerandaViewModel @Inject constructor(
    private val repository: NgajiRepository,
    private val estimasiWaktuUseCase: EstimasiWaktuUseCase
) : ViewModel() {

    //DATA USER
    val userTarget: StateFlow<TargetUser?> = repository.getUserTarget()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    //PROGRESS HARIAN
    private val _halamanHariIni = MutableStateFlow(0)
    val halamanHariIni: StateFlow<Int> = _halamanHariIni

    val progressPersen: StateFlow<Int> = combine(_halamanHariIni, userTarget) { totalAyatHariIni, user ->
        if (user == null || user.targetAyatHarian == 0) {
            0
        } else {
            val persen = (totalAyatHariIni.toFloat() / user.targetAyatHarian.toFloat()) * 100
            persen.toInt().coerceIn(0, 100)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    //TERAKHIR DIBACA
    val lastRead: StateFlow<SurahProgress?> = repository.allSurah // <--- Ganti ini (Hapus getAllSurahProgress())
        .combine(MutableStateFlow(true)) { list, _ ->
            list.maxByOrNull { it.lastUpdated }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    //mendeteksi riwayat ngaji minggu ini
    val listKalender: StateFlow<List<KalenderItemData>> = repository.allRiwayatSesi
        .map { listSesi ->
            val calendar = Calendar.getInstance()
            val todayDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val todayYear = calendar.get(Calendar.YEAR)

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

            val hasilList = mutableListOf<KalenderItemData>()
            val formatHari = SimpleDateFormat("EEE", Locale("id", "ID")) // "Min", "Sen"
            val formatTanggal = SimpleDateFormat("dd", Locale("id", "ID")) // "21"

            for (i in 0..6) {
                val calStart = calendar.clone() as Calendar
                calStart.set(Calendar.HOUR_OF_DAY, 0)
                calStart.set(Calendar.MINUTE, 0)
                calStart.set(Calendar.SECOND, 0)
                calStart.set(Calendar.MILLISECOND, 0)
                val startMillis = calStart.timeInMillis

                val calEnd = calendar.clone() as Calendar
                calEnd.set(Calendar.HOUR_OF_DAY, 23)
                calEnd.set(Calendar.MINUTE, 59)
                calEnd.set(Calendar.SECOND, 59)
                val endMillis = calEnd.timeInMillis


                val adaSesi = listSesi.any { it.tanggalSesi in startMillis..endMillis }


                val isToday = (calendar.get(Calendar.DAY_OF_YEAR) == todayDayOfYear) &&
                        (calendar.get(Calendar.YEAR) == todayYear)

                hasilList.add(
                    KalenderItemData(
                        hari = formatHari.format(calendar.time),
                        tanggal = formatTanggal.format(calendar.time),
                        isAdaSesi = adaSesi, // Ini kunci warnanya (True = Hijau)
                        isHariIni = isToday
                    )
                )

                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            hasilList
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        hitungProgressHariIni()
        viewModelScope.launch {
            repository.cekDanIsiDataSurah()
        }
    }

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

    fun hitungEstimasi(menitTersedia: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val pesan = estimasiWaktuUseCase.hitungTarget(menitTersedia)
            onResult(pesan)
        }
    }
}