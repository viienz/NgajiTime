package com.example.ngajitime.ui.layar.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.TargetUser
import com.example.ngajitime.data.repository.NgajiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.filter

// Model data (Tetap sama)
data class DataGrafik(
    val hari: String,
    val totalAyat: Int
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: NgajiRepository
) : ViewModel() {

    // 1. DATA USER
    val userTarget: StateFlow<TargetUser?> = repository.getUserTarget()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 2. NAVIGASI MINGGUAN (0 = Minggu Ini, -1 = Minggu Lalu, dst)
    private val _offsetMinggu = MutableStateFlow(0)
    val offsetMinggu: StateFlow<Int> = _offsetMinggu

    // 3. LOGIKA GRAFIK
    val dataMingguan: StateFlow<List<DataGrafik>> = combine(repository.allRiwayatSesi, _offsetMinggu) { semuaSesi, offset ->
        val listGrafik = mutableListOf<DataGrafik>()
        val calendar = Calendar.getInstance()
        val formatHari = SimpleDateFormat("EEE", Locale("id", "ID")) // Sen, Sel, Rab

        // A. Tentukan Hari Acuan
        calendar.add(Calendar.WEEK_OF_YEAR, offset)

        // B. Mundur 6 hari ke belakang (Start Date)
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        // C. Loop 7 Hari ke depan
        for (i in 0..6) {
            // PERBAIKAN DI SINI (Memastikan tipe datanya Long/Millis)

            // 1. Set Awal Hari (00:00:00)
            val calStart = calendar.clone() as Calendar
            calStart.set(Calendar.HOUR_OF_DAY, 0)
            calStart.set(Calendar.MINUTE, 0)
            calStart.set(Calendar.SECOND, 0)
            calStart.set(Calendar.MILLISECOND, 0)
            val startOfDay = calStart.timeInMillis // Tipe: Long

            // 2. Set Akhir Hari (23:59:59)
            val calEnd = calendar.clone() as Calendar
            calEnd.set(Calendar.HOUR_OF_DAY, 23)
            calEnd.set(Calendar.MINUTE, 59)
            calEnd.set(Calendar.SECOND, 59)
            val endOfDay = calEnd.timeInMillis // Tipe: Long

            val namaHari = formatHari.format(calendar.time)

            // D. Filter Sesi (Gunakan 'tanggalSesi' bukan 'waktuMulai')
            val totalAyatHariIni = semuaSesi
                .filter { it.tanggalSesi in startOfDay..endOfDay } // <--- PERBAIKAN NAMA VARIABEL
                .sumOf { it.halamanSelesai } // (Asumsi halamanSelesai = Ayat)

            listGrafik.add(DataGrafik(namaHari, totalAyatHariIni))

            // Lanjut ke hari besoknya
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        listGrafik

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // 4. TOTAL AYAT
    val totalAyatMingguIni: StateFlow<Int> = dataMingguan.map { list ->
        list.sumOf { it.totalAyat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    // 5. TEXT RENTANG TANGGAL (LOGIKA PINTAR)
    val rentangTanggal: StateFlow<String> = _offsetMinggu.map { offset ->
        val endCal = Calendar.getInstance()
        endCal.add(Calendar.WEEK_OF_YEAR, offset) // Tanggal Akhir (Kanan)

        val startCal = endCal.clone() as Calendar
        startCal.add(Calendar.DAY_OF_YEAR, -6)   // Tanggal Awal (Kiri)

        val fmtFull = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) // "05 Nov 2025"
        val fmtDayMonth = SimpleDateFormat("dd MMM", Locale("id", "ID"))  // "30 Okt"
        val fmtDayOnly = SimpleDateFormat("dd", Locale("id", "ID"))       // "10"

        // CEK: Apakah Bulan Awal & Akhir berbeda?
        if (startCal.get(Calendar.MONTH) != endCal.get(Calendar.MONTH)) {
            // KASUS BEDA BULAN (Contoh: 30 Okt - 05 Nov 2025)
            // Kita tampilkan Bulan di bagian kiri juga
            "${fmtDayMonth.format(startCal.time)} - ${fmtFull.format(endCal.time)}"
        } else {
            // KASUS SATU BULAN (Contoh: 10 - 16 Nov 2025)
            // Cukup tanggal saja di kiri
            "${fmtDayOnly.format(startCal.time)} - ${fmtFull.format(endCal.time)}"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")


    // 6. FUNGSI GESER
    fun geserMinggu(arah: Int) {
        _offsetMinggu.value += arah
    }
}