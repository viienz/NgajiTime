package com.example.ngajitime.ui.layar.listsurah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.SesiNgaji
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.data.repository.NgajiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ListSurahViewModel @Inject constructor(
    private val repository: NgajiRepository
) : ViewModel() {

    // Data asli dari database
    private val _allSurah = MutableStateFlow<List<SurahProgress>>(emptyList())

    // Data yang ditampilkan di UI
    private val _uiSurahList = MutableStateFlow<List<SurahProgress>>(emptyList())
    val uiSurahList: StateFlow<List<SurahProgress>> = _uiSurahList.asStateFlow()

    // Kata kunci pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- FUNGSI UTAMA: SIMPAN PROGRESS ---
    fun simpanProgresSurah(surah: SurahProgress, ayatBaru: Int) {
        viewModelScope.launch {
            // 1. Validasi Input
            val ayatValid = ayatBaru.coerceIn(0, surah.totalAyat)

            // 2. Hitung Selisih
            val selisihAyat = ayatValid - surah.ayatTerakhirDibaca

            // 3. Update Tabel Surah (Bookmark)
            repository.updateProgressSurah(
                id = surah.nomorSurah,
                ayat = ayatValid,
                total = surah.totalAyat
            )

            // 4. Update Logika Lain (Hanya jika ada ayat bertambah)
            if (selisihAyat > 0) {
                val waktuSekarang = System.currentTimeMillis()

                // A. Insert Grafik (Sesi)
                val sesiManual = SesiNgaji(
                    tanggalSesi = waktuSekarang,
                    durasiDetik = 0, // Manual input tidak ada durasi
                    halamanSelesai = selisihAyat,
                    isFokus = false
                )
                repository.insertSesi(sesiManual)

                // B. Tambah Total Jejak (Agar piala terbuka)
                repository.tambahTotalAyat(selisihAyat)

                // C. [BARU] UPDATE STREAK OTOMATIS 🔥
                cekDanUpdateStreak(waktuSekarang)
            }
        }
    }

    // --- LOGIKA CERDAS UPDATE STREAK ---
    private suspend fun cekDanUpdateStreak(waktuSekarang: Long) {
        // Ambil data user terbaru (OneShot)
        // Pastikan di Repository sudah ada fungsi getUserTargetOneShot() yg kita buat sebelumnya
        val user = repository.getUserTargetOneShot() ?: return

        val calToday = Calendar.getInstance()
        calToday.timeInMillis = waktuSekarang

        val calLast = Calendar.getInstance()
        calLast.timeInMillis = user.lastStreakDate

        // Cek 1: Apakah Hari Ini sama dengan Tanggal Terakhir Streak?
        if (isSameDay(calToday, calLast)) {
            // Sudah absen hari ini, tidak perlu update streak lagi.
            // Biarkan saja.
            return
        }

        // Cek 2: Apakah Kemarin? (Berarti Streak Berlanjut)
        // Kita majukan tanggal terakhir sebanyak 1 hari, lalu cek apakah sama dengan hari ini
        calLast.add(Calendar.DAY_OF_YEAR, 1)

        if (isSameDay(calToday, calLast)) {
            // HORE! Berurutan (Consecutive) -> Streak Nambah +1
            repository.updateStreak(user.currentStreak + 1, waktuSekarang)
        } else {
            // YAH... Terputus (Atau baru mulai) -> Reset jadi 1
            repository.updateStreak(1, waktuSekarang)
        }
    }

    // Helper: Cek apakah dua kalender ada di hari yang sama (abaikan jam)
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }


    // --- INIT & FILTER DATA (TETAP SAMA) ---
    init {
        ambilDataSurah()
    }

    private fun ambilDataSurah() {
        viewModelScope.launch {
            repository.allSurah.collectLatest { daftarSurah ->
                _allSurah.value = daftarSurah
                filterSurah(_searchQuery.value)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterSurah(query)
    }

    private fun filterSurah(query: String) {
        if (query.isEmpty()) {
            _uiSurahList.value = _allSurah.value
        } else {
            _uiSurahList.value = _allSurah.value.filter {
                it.namaSurah.contains(query, ignoreCase = true) ||
                        it.artiSurah.contains(query, ignoreCase = true) ||
                        it.nomorSurah.toString() == query
            }
        }
    }
}