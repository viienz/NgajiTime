package com.example.ngajitime.ui.layar.stats

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.TargetUser
import com.example.ngajitime.data.repository.NgajiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// --- DATA CLASS PIALA BARU 🏆 ---
data class Piala(
    val id: Int,
    val judul: String,
    val syarat: String,
    val icon: ImageVector,
    val warna: Color,
    val isUnlocked: Boolean
)

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

    // 2. LOGIKA PIALA (HALL OF FAME) 🏆 [BARU]
    val koleksiPiala: StateFlow<List<Piala>> = userTarget.map { user ->
        val totalAyat = user?.totalAyatDibaca ?: 0
        val streak = user?.currentStreak ?: 0

        listOf(
            // --- TIER 1: PERUNGGU (BRONZE) ---
            Piala(1, "Langkah Awal", "Baca 1 Ayat Pertama", Icons.Rounded.Star, Color(0xFFCD7F32), totalAyat >= 1),
            Piala(2, "Si Konsisten", "Streak 3 Hari", Icons.Rounded.LocalFireDepartment, Color(0xFFCD7F32), streak >= 3),

            // --- TIER 2: PERAK (SILVER) ---
            Piala(3, "Kutu Buku", "Total 100 Ayat", Icons.Rounded.MenuBook, Color(0xFFC0C0C0), totalAyat >= 100),
            Piala(4, "Istiqomah", "Streak 7 Hari", Icons.Rounded.Timer, Color(0xFFC0C0C0), streak >= 7),

            // --- TIER 3: EMAS (GOLD) ---
            Piala(5, "Sultan Ngaji", "Total 1.000 Ayat", Icons.Rounded.EmojiEvents, Color(0xFFFFD700), totalAyat >= 1000),
            Piala(6, "Legenda", "Streak 30 Hari", Icons.Rounded.WorkspacePremium, Color(0xFFFFD700), streak >= 30)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // 3. LOGIKA GRAFIK (KODE LAMA ANDA - TETAP AMAN) ✅
    private val _offsetMinggu = MutableStateFlow(0)
    val offsetMinggu: StateFlow<Int> = _offsetMinggu

    val dataMingguan: StateFlow<List<DataGrafik>> = combine(repository.allRiwayatSesi, _offsetMinggu) { semuaSesi, offset ->
        val listGrafik = mutableListOf<DataGrafik>()
        val calendar = Calendar.getInstance()
        val formatHari = SimpleDateFormat("EEE", Locale("id", "ID"))

        calendar.add(Calendar.WEEK_OF_YEAR, offset)
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        for (i in 0..6) {
            val calStart = calendar.clone() as Calendar
            calStart.set(Calendar.HOUR_OF_DAY, 0)
            calStart.set(Calendar.MINUTE, 0)
            calStart.set(Calendar.SECOND, 0)
            calStart.set(Calendar.MILLISECOND, 0)
            val startOfDay = calStart.timeInMillis

            val calEnd = calendar.clone() as Calendar
            calEnd.set(Calendar.HOUR_OF_DAY, 23)
            calEnd.set(Calendar.MINUTE, 59)
            calEnd.set(Calendar.SECOND, 59)
            val endOfDay = calEnd.timeInMillis

            val namaHari = formatHari.format(calendar.time)

            val totalAyatHariIni = semuaSesi
                .filter { it.tanggalSesi in startOfDay..endOfDay }
                .sumOf { it.halamanSelesai }

            listGrafik.add(DataGrafik(namaHari, totalAyatHariIni))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        listGrafik
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAyatMingguIni: StateFlow<Int> = dataMingguan.map { list ->
        list.sumOf { it.totalAyat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val rentangTanggal: StateFlow<String> = _offsetMinggu.map { offset ->
        val endCal = Calendar.getInstance()
        endCal.add(Calendar.WEEK_OF_YEAR, offset)
        val startCal = endCal.clone() as Calendar
        startCal.add(Calendar.DAY_OF_YEAR, -6)

        val fmtFull = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val fmtDayMonth = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        val fmtDayOnly = SimpleDateFormat("dd", Locale("id", "ID"))

        if (startCal.get(Calendar.MONTH) != endCal.get(Calendar.MONTH)) {
            "${fmtDayMonth.format(startCal.time)} - ${fmtFull.format(endCal.time)}"
        } else {
            "${fmtDayOnly.format(startCal.time)} - ${fmtFull.format(endCal.time)}"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun geserMinggu(arah: Int) {
        _offsetMinggu.value += arah
    }
}