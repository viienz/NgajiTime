package com.example.ngajitime.ui.layar.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.dao.NotifikasiDao // <-- Import Baru
import com.example.ngajitime.data.local.entity.NotifikasiEntity // <-- Import Baru
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
    private val estimasiWaktuUseCase: EstimasiWaktuUseCase,
    private val notifikasiDao: NotifikasiDao // <--- 1. INJECT DAO NOTIFIKASI DISINI
) : ViewModel() {

    // ==========================================
    // BAGIAN 1: FITUR LAMA (User, Progress, Kalender)
    // ==========================================

    // DATA USER
    val userTarget: StateFlow<TargetUser?> = repository.getUserTarget()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // PROGRESS HARIAN
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


    // TERAKHIR DIBACA
    val lastRead: StateFlow<SurahProgress?> = repository.allSurah
        .combine(MutableStateFlow(true)) { list, _ ->
            list.maxByOrNull { it.lastUpdated }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // KALENDER MINGGUAN
    val listKalender: StateFlow<List<KalenderItemData>> = repository.allRiwayatSesi
        .map { listSesi ->
            val calendar = Calendar.getInstance()
            val todayDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val todayYear = calendar.get(Calendar.YEAR)

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

            val hasilList = mutableListOf<KalenderItemData>()
            val formatHari = SimpleDateFormat("EEE", Locale("id", "ID"))
            val formatTanggal = SimpleDateFormat("dd", Locale("id", "ID"))

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
                        isAdaSesi = adaSesi,
                        isHariIni = isToday
                    )
                )

                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            hasilList
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // ==========================================
    // BAGIAN 2: FITUR BARU (NOTIFIKASI)
    // ==========================================

    // List Notifikasi (Untuk ditampilkan di BottomSheet)
    val listNotifikasi: StateFlow<List<NotifikasiEntity>> = notifikasiDao.getAllNotifikasi()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Jumlah Belum Dibaca (Untuk Titik Merah Lonceng)
    val unreadCount: StateFlow<Int> = notifikasiDao.getUnreadCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Fungsi: Reset titik merah saat lonceng dibuka
    fun tandaiSudahDibaca() {
        viewModelScope.launch {
            notifikasiDao.markAllAsRead()
        }
    }

    // Fungsi: Test Tambah Notifikasi Manual (Agar tombol 'Tes +1' di UI jalan)
    fun testAddNotification() {
        viewModelScope.launch {
            notifikasiDao.insertNotifikasi(
                NotifikasiEntity(
                    judul = "Tes Notifikasi",
                    pesan = "Halo! Ini adalah notifikasi percobaan dari sistem lokal.",
                    tipe = "INFO",
                    isRead = false
                )
            )
        }
    }

    // ==========================================
    // BAGIAN 3: INITIALIZATION & LOGIC LAIN
    // ==========================================

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