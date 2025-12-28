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

    private val _allSurah = MutableStateFlow<List<SurahProgress>>(emptyList())

    private val _uiSurahList = MutableStateFlow<List<SurahProgress>>(emptyList())
    val uiSurahList: StateFlow<List<SurahProgress>> = _uiSurahList.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun simpanProgresSurah(surah: SurahProgress, ayatBaru: Int) {
        viewModelScope.launch {
            val ayatValid = ayatBaru.coerceIn(0, surah.totalAyat)
            val selisihAyat = ayatValid - surah.ayatTerakhirDibaca

            repository.updateProgressSurah(
                id = surah.nomorSurah,
                ayat = ayatValid,
                total = surah.totalAyat
            )

            if (selisihAyat > 0) {
                val waktuSekarang = System.currentTimeMillis()

                val sesiManual = SesiNgaji(
                    tanggalSesi = waktuSekarang,
                    durasiDetik = 0,
                    halamanSelesai = selisihAyat,
                    isFokus = false
                )
                repository.insertSesi(sesiManual)

                repository.tambahTotalAyat(selisihAyat)
                cekDanUpdateStreak(waktuSekarang)
            }
        }
    }

    private suspend fun cekDanUpdateStreak(waktuSekarang: Long) {
        val user = repository.getUserTargetOneShot() ?: return

        val calToday = Calendar.getInstance()
        calToday.timeInMillis = waktuSekarang

        val calLast = Calendar.getInstance()
        calLast.timeInMillis = user.lastStreakDate

        if (isSameDay(calToday, calLast)) {
            return
        }

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