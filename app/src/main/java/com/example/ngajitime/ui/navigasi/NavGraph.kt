package com.example.ngajitime.ui.navigasi

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ngajitime.ui.layar.beranda.LayarBeranda
import com.example.ngajitime.ui.layar.login.LayarLogin
import com.example.ngajitime.ui.layar.sesi.LayarInputHasil
import com.example.ngajitime.ui.layar.sesi.LayarTimer
import com.example.ngajitime.ui.layar.listsurah.LayarListSurah
import com.example.ngajitime.ui.layar.stats.LayarStats
import com.example.ngajitime.ui.layar.profil.LayarProfil

@Composable
fun NgajiNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Rute.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- 1. RUTE LOGIN ---
        composable(Rute.LOGIN) {
            LayarLogin(
                // Pintu 1: User Baru selesai setup -> Masuk Beranda
                onMasuk = {
                    navController.navigate(Rute.BERANDA) {
                        popUpTo(Rute.LOGIN) { inclusive = true }
                    }
                },

                // Pintu 2 (BARU): User Lama terdeteksi -> Langsung Masuk Beranda
                onLoginSuccess = {
                    navController.navigate(Rute.BERANDA) {
                        popUpTo(Rute.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- 2. RUTE BERANDA ---
        composable(Rute.BERANDA) {
            LayarBeranda(
                onKeSurah = { navController.navigate(Rute.LIST_SURAH) },
                onKeStats = { navController.navigate(Rute.STATS) },
                onKeProfil = { navController.navigate(Rute.PROFIL) },
                // Sambungkan Tombol Hijau ke Layar Timer
                onKeTimer = { navController.navigate(Rute.TIMER) }
            )
        }

        // --- 3. RUTE LIST SURAH ---
        composable(Rute.LIST_SURAH) {
            LayarListSurah(
                onKeBeranda = { navController.navigate(Rute.BERANDA) },
                onKeStats = { navController.navigate(Rute.STATS) },
                onKeProfil = { navController.navigate(Rute.PROFIL) },
                onKlikSurah = { /* Nanti untuk detail bacaan */ }
            )
        }

        // --- 4. RUTE TIMER (FOCUS MODE) ⏱️ ---
        composable(Rute.TIMER) {
            LayarTimer(
                // Saat timer selesai, kita terima durasi (Long)
                onSelesai = { durasiDetik ->
                    // Pindah ke layar Input Hasil membawa data durasi
                    navController.navigate(Rute.inputHasil(durasiDetik)) {
                        // Hapus Layar Timer dari backstack agar user tidak bisa kembali ke timer yang sudah 0
                        popUpTo(Rute.TIMER) { inclusive = true }
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
                onLogout = {
                    navController.navigate(Rute.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // --- 7. RUTE INPUT HASIL (INTEGRASI) ✅ ---
        composable(
            route = Rute.INPUT_HASIL,
            arguments = listOf(navArgument("durasi") { type = NavType.LongType })
        ) { backStackEntry ->
            // Tangkap data durasi yang dikirim dari Timer
            val durasi = backStackEntry.arguments?.getLong("durasi") ?: 0L

            LayarInputHasil(
                durasiDetik = durasi,
                onSelesaiSimpan = {
                    // Setelah simpan sukses, Kembali ke Beranda & Refresh
                    navController.navigate(Rute.BERANDA) {
                        // Hapus history agar user tidak bisa back ke form input
                        popUpTo(Rute.BERANDA) { inclusive = true }
                    }
                }
            )
        }
    }
}