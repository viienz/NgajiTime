package com.example.ngajitime.ui.navigasi

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ngajitime.ui.layar.beranda.LayarBeranda
import com.example.ngajitime.ui.layar.intro.LayarKuesioner
import com.example.ngajitime.ui.layar.sesi.LayarInputHasil
import com.example.ngajitime.ui.layar.sesi.LayarTimer
import com.example.ngajitime.ui.layar.listsurah.LayarListSurah
import com.example.ngajitime.ui.layar.stats.LayarStats
import com.example.ngajitime.ui.layar.profil.LayarProfil

@Composable
fun NgajiNavGraph() {
    val navController = rememberNavController()

    // Tentukan mau mulai dari mana?
    // Idealnya dicek dulu apakah user baru/lama.
    // Untuk skripsi/demo, kita mulai dari BERANDA saja dulu biar cepat.
    // (Nanti bisa diubah ke Rute.INTRO jika mau demo kuesioner)
    val startDestination = Rute.BERANDA

    NavHost(navController = navController, startDestination = startDestination) {

        // 1. RUTE INTRO (Kuesioner)
        composable(Rute.INTRO) {
            LayarKuesioner()
            // Nanti di sini tambahkan logika: Jika selesai -> navController.navigate(Rute.BERANDA)
        }

        composable(Rute.LIST_SURAH) {
            LayarListSurah(
                onKlikSurah = { surah ->
                    // Nanti kita buat fitur Detail Surah di sini
                    // Sementara kasih pesan dulu atau print log
                    println("User memilih surah: ${surah.namaSurah}")
                }
            )
        }

        // 2. RUTE BERANDA (Dashboard)
        composable(Rute.BERANDA) {
            LayarBeranda(
                onKlikMulai = {
                    // Saat tombol Play diklik, pindah ke Timer
                    navController.navigate(Rute.TIMER)
                },
                onKeListSurah = {
                    navController.navigate(Rute.LIST_SURAH)
                },
                onKeStats = {
                    navController.navigate(Rute.STATS)
                },
                onKeProfil = {
                    navController.navigate(Rute.PROFIL)
                }
            )
        }

        // 3. RUTE TIMER (Focus Mode)
        composable(Rute.TIMER) {
            LayarTimer(
                onSelesai = { durasiDetik ->
                    // Saat timer selesai, pindah ke Input bawa data durasi
                    navController.navigate(Rute.inputHasil(durasiDetik)) {
                        // Hapus history timer agar user gak bisa back ke timer
                        popUpTo(Rute.BERANDA) { inclusive = false }
                    }
                },
                onBatal = {
                    // Jika batal, balik ke Beranda
                    navController.popBackStack()
                }
            )
        }

        composable(Rute.STATS) {
            LayarStats(onBack = { navController.popBackStack() })
        }

        composable(Rute.PROFIL) {
            LayarProfil(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 4. RUTE INPUT HASIL (Lapor)
        composable(
            route = Rute.INPUT_HASIL,
            arguments = listOf(navArgument("durasi") { type = NavType.LongType })
        ) { backStackEntry ->
            // Ambil data durasi dari kiriman Timer
            val durasi = backStackEntry.arguments?.getLong("durasi") ?: 0L

            LayarInputHasil(
                durasiDetik = durasi,
                onSelesaiSimpan = {
                    // Setelah simpan, balik ke Beranda dan hapus semua tumpukan layar sebelumnya
                    navController.navigate(Rute.BERANDA) {
                        popUpTo(Rute.BERANDA) { inclusive = true }
                    }
                }
            )
        }
    }
}