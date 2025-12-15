package com.example.ngajitime.ui.layar.listsurah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.data.repository.NgajiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.ngajitime.data.local.entity.SesiNgaji
import javax.inject.Inject

@HiltViewModel
class ListSurahViewModel @Inject constructor(
    private val repository: NgajiRepository
) : ViewModel() {

    // Data asli dari database
    private val _allSurah = MutableStateFlow<List<SurahProgress>>(emptyList())

    // Data yang ditampilkan di UI (bisa berubah kalau disearch)
    private val _uiSurahList = MutableStateFlow<List<SurahProgress>>(emptyList())
    val uiSurahList: StateFlow<List<SurahProgress>> = _uiSurahList.asStateFlow()

    // Kata kunci pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun simpanProgresSurah(surah: SurahProgress, ayatBaru: Int) {
        viewModelScope.launch {
            // 1. Validasi Input
            val ayatValid = ayatBaru.coerceIn(0, surah.totalAyat)

            // 2. Hitung Selisih (Berapa ayat yang baru dibaca?)
            // Misal: Tadi ayat 10, sekarang update jadi 25. Berarti nambah 15 ayat.
            val selisihAyat = ayatValid - surah.ayatTerakhirDibaca

            // 3. Update Tabel Surah (Agar progress bar hijau berubah)
            repository.updateProgressSurah(
                id = surah.nomorSurah,
                ayat = ayatValid,
                total = surah.totalAyat
            )

            // 4. Update Tabel Sesi/Target (PENTING: Agar Target Harian Beranda berkurang)
            if (selisihAyat > 0) {
                // Catat sebagai sesi baru (Durasi 0 karena manual, tapi Ayat dihitung)
                val sesiManual = SesiNgaji(
                    tanggalSesi = System.currentTimeMillis(),
                    durasiDetik = 0, // 0 menandakan ini input manual, bukan timer
                    halamanSelesai = selisihAyat, // Kita simpan jumlah ayat di kolom ini
                    isFokus = false // Bukan mode fokus
                )
                repository.insertSesi(sesiManual)

                // Tambahkan ke Total Progress User (Agar Streak & Level naik)
                repository.addProgressAyat(selisihAyat)
            }
        }
    }

    init {
        ambilDataSurah()
    }

    private fun ambilDataSurah() {
        viewModelScope.launch {
            repository.allSurah.collectLatest { daftarSurah ->
                _allSurah.value = daftarSurah
                filterSurah(_searchQuery.value) // Terapkan filter saat data masuk
            }
        }
    }

    // Logic Pencarian
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