package com.example.ngajitime.ui.layar.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.repository.NgajiRepository
import com.example.ngajitime.data.local.entity.TargetUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

// Model data sederhana untuk grafik
data class DataGrafik(
    val hari: String, // "Sen", "Sel", "Rab"
    val totalAyat: Int
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: NgajiRepository
) : ViewModel() {

    private val _dataMingguan = MutableStateFlow<List<DataGrafik>>(emptyList())
    val dataMingguan: StateFlow<List<DataGrafik>> = _dataMingguan.asStateFlow()

    private val _totalAyatMingguIni = MutableStateFlow(0)
    val totalAyatMingguIni: StateFlow<Int> = _totalAyatMingguIni.asStateFlow()
    private val _userTarget = MutableStateFlow<TargetUser?>(null)
    val userTarget: StateFlow<TargetUser?> = _userTarget.asStateFlow()

    init {
        muatDataMingguan()
        muatDataUser()
    }

    private fun muatDataUser() {
        viewModelScope.launch {
            repository.getUserTarget().collectLatest { user ->
                _userTarget.value = user
            }
        }
    }

    private fun muatDataMingguan() {
        viewModelScope.launch {
            // Ambil semua sesi (Idealnya query DB dibatasi tanggal, tapi untuk demo kita filter di sini)
            repository.allRiwayatSesi.collectLatest { semuaSesi ->

                // 1. Siapkan Kalender 7 Hari Terakhir
                val listGrafik = mutableListOf<DataGrafik>()
                val kalender = Calendar.getInstance()
                val formatHari = SimpleDateFormat("EEE", Locale("id", "ID")) // Sen, Sel, Rab

                // Geser ke 6 hari lalu
                kalender.add(Calendar.DAY_OF_YEAR, -6)

                var totalMinggu = 0

                // 2. Loop 7 hari (Dari 6 hari lalu sampai Hari Ini)
                for (i in 0..6) {
                    val tanggalCek = kalender.time
                    val namaHari = formatHari.format(tanggalCek)

                    // Filter sesi yang terjadi pada tanggalCek
                    val sesiHariIni = semuaSesi.filter { sesi ->
                        isSameDay(sesi.tanggalSesi, kalender.timeInMillis)
                    }

                    val jumlahAyat = sesiHariIni.sumOf { it.halamanSelesai } // Ingat: kolom ini isinya Ayat
                    totalMinggu += jumlahAyat

                    listGrafik.add(DataGrafik(namaHari, jumlahAyat))

                    // Geser ke hari berikutnya (Besok)
                    kalender.add(Calendar.DAY_OF_YEAR, 1)
                }

                _dataMingguan.value = listGrafik
                _totalAyatMingguIni.value = totalMinggu
            }
        }
    }

    // Helper: Cek apakah dua timestamp ada di hari yang sama
    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}