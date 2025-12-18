package com.example.ngajitime.ui.navigasi

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController // Import ini penting
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ngajitime.ui.layar.beranda.LayarBeranda
import com.example.ngajitime.ui.layar.login.LayarLogin // <-- Pastikan Import Login ada
import com.example.ngajitime.ui.layar.sesi.LayarInputHasil
import com.example.ngajitime.ui.layar.sesi.LayarTimer
import com.example.ngajitime.ui.layar.listsurah.LayarListSurah
import com.example.ngajitime.ui.layar.stats.LayarStats
import com.example.ngajitime.ui.layar.profil.LayarProfil

@Composable
fun NgajiNavGraph(
    // Parameter ini penting agar MainActivity bisa mengirim hasil cek user
    navController: NavHostController = rememberNavController(),
    startDestination: String = Rute.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination // Gunakan variabel dinamis ini
    ) {

        // --- 1. RUTE LOGIN (PINTU GERBANG BARU) ---
        composable(Rute.LOGIN) {
            LayarLogin(
                onMasuk = {
                    // Logika: Setelah Login sukses, masuk Beranda & Hapus Login dari riwayat
                    navController.navigate(Rute.BERANDA) {
                        popUpTo(Rute.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- 2. RUTE BERANDA (Dashboard) ---
        composable(Rute.BERANDA) {
            LayarBeranda(
                onKeSurah = { navController.navigate(Rute.LIST_SURAH) },
                onKeStats = { navController.navigate(Rute.STATS) },
                onKeProfil = { navController.navigate(Rute.PROFIL) },
                onKeTimer = {
                    navController.navigate(Rute.TIMER)
                }
            )
        }

        // --- 3. RUTE LIST SURAH ---
        composable(Rute.LIST_SURAH) {
            LayarListSurah(
                onKeBeranda = { navController.navigate(Rute.BERANDA) },
                onKeStats = { navController.navigate(Rute.STATS) },
                onKeProfil = { navController.navigate(Rute.PROFIL) },
                onKlikSurah = { namaSurah ->
                    // Nanti kita arahkan ke detail baca
                }
            )
        }

        // --- 4. RUTE TIMER (Focus Mode) ---
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
                    navController.popBackStack()
                }
            )
        }

        // --- 5. RUTE STATISTIK ---
        composable(Rute.STATS) {
            LayarStats(
                onKeBeranda = { navController.navigate(Rute.BERANDA) },
                onKeSurah = { navController.navigate(Rute.LIST_SURAH) },
                onKeProfil = { navController.navigate(Rute.PROFIL) }
            )
        }

        // --- 6. RUTE PROFIL ---
        composable(Rute.PROFIL) {
            LayarProfil(
                // Navigasi Bottom Bar
                onKeBeranda = {
                    navController.navigate(Rute.BERANDA) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                onKeSurah = {
                    navController.navigate(Rute.LIST_SURAH) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                onKeStats = {
                    navController.navigate(Rute.STATS) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                // Logika Logout (Reset ke Halaman Awal)
                onLogout = {
                    navController.navigate(Rute.INTRO) { // Atau Rute.LOGIN
                        popUpTo(0) { inclusive = true } // Hapus semua riwayat backstack
                    }
                }
            )
        }

        // --- 7. RUTE INPUT HASIL (Lapor) ---
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