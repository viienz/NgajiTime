package com.example.ngajitime.ui.layar.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngajitime.data.local.entity.TargetUser
import com.example.ngajitime.data.repository.NgajiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.ngajitime.util.SignInResult
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: NgajiRepository
) : ViewModel() {
    private val _step = MutableStateFlow(1)
    val step: StateFlow<Int> = _step

    var namaUser = ""
    var emailUser: String? = null
    var frekuensi = ""
    var levelBaca = "LANCAR"
    var kendala = ""
    var tujuan = ""

    // Data Target
    var modeTarget = "WAKTU"
    var nilaiTarget = 15

    fun nextStep() { _step.value += 1 }
    fun prevStep() { if (_step.value > 1) _step.value -= 1 }

    fun setLoginGoogle(nama: String, email: String) {
        namaUser = nama
        emailUser = email
        _step.value = 2
    }

    //SIMPAN KE DATABASE
    fun simpanDataUser(onSukses: () -> Unit) {
        viewModelScope.launch {
            val detikPerAyat = when (levelBaca) {
                "PEMULA" -> 25 // Terbata-bata
                "LANCAR" -> 15 // Standar
                "MAHIR" -> 10  // Cepat
                else -> 15
            }

            //Hitung Target Harian
            var targetHarian = 0
            if (modeTarget == "WAKTU") {
                // Rumus: (Menit * 60) / DetikPerAyat
                targetHarian = (nilaiTarget * 60) / detikPerAyat
            } else {
                // Rumus: 6236 Ayat / Hari
                targetHarian = 6236 / (if (nilaiTarget > 0) nilaiTarget else 1)
            }

            //Buat Object User
            val userBaru = TargetUser(
                id = 1,
                namaUser = namaUser,
                email = emailUser,
                frekuensiAwal = frekuensi,
                kendalaUtama = kendala,
                tujuanUtama = tujuan,
                levelBaca = levelBaca,
                modeTarget = modeTarget,
                waktuLuangMenit = if (modeTarget == "WAKTU") nilaiTarget else 0,
                durasiTargetHari = if (modeTarget == "DEADLINE") nilaiTarget else 0,
                targetAyatHarian = targetHarian
            )

            //Simpan
            repository.saveTarget(userBaru)
            onSukses()
        }
    }

    fun onSignInResult(result: SignInResult) {
        result.data?.let { user ->
            viewModelScope.launch {
                val isUserLama = repository.cekUserLamaDanSimpan(user.userId)

                if (isUserLama) {
                    // Jika user lama
                    // Data sudah didownload repository dan disimpan ke Room.
                    // Langsung lompat ke Selesai (Step terakhir/Home)
                    // Kita set ke angka 99 (kode rahasia untuk "Login Beres")
                    _step.value = 99

                } else {
                    // Jika user baru
                    // Siapkan data untuk form personalisasi
                    namaUser = user.username ?: "User"
                    emailUser = user.email

                    // Arahkan ke Personalisasi
                    _step.value = 2
                }
            }
        }
    }
}