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

        // RUTE LOGIN
        composable(Rute.LOGIN) {
            LayarLogin(
                onMasuk = {
                    navController.navigate(Rute.BERANDA) {
                        popUpTo(Rute.LOGIN) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Rute.BERANDA) {
                        popUpTo(Rute.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // RUTE BERANDA
        composable(Rute.BERANDA) {
            LayarBeranda(
                onKeSurah = { navController.navigate(Rute.LIST_SURAH) },
                onKeStats = { navController.navigate(Rute.STATS) },
                onKeProfil = { navController.navigate(Rute.PROFIL) },
                onKeTimer = { navController.navigate(Rute.TIMER) }
            )
        }

        // RUTE LIST SURAH
        composable(Rute.LIST_SURAH) {
            LayarListSurah(
                onKeBeranda = { navController.navigate(Rute.BERANDA) },
                onKeStats = { navController.navigate(Rute.STATS) },
                onKeProfil = { navController.navigate(Rute.PROFIL) },
                onKlikSurah = { /* Nanti untuk detail bacaan */ }
            )
        }

        // RUTE TIMER (FOCUS MODE) ⏱️
        composable(Rute.TIMER) {
            LayarTimer(
                onSelesai = { durasiDetik ->
                    navController.navigate(Rute.inputHasil(durasiDetik)) {
                        popUpTo(Rute.TIMER) { inclusive = true }
                    }
                },
                onBatal = {
                    navController.popBackStack()
                }
            )
        }

        // RUTE STATISTIK
        composable(Rute.STATS) {
            LayarStats(
                onKeBeranda = { navController.navigate(Rute.BERANDA) },
                onKeSurah = { navController.navigate(Rute.LIST_SURAH) },
                onKeProfil = { navController.navigate(Rute.PROFIL) }
            )
        }

        // RUTE PROFIL
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

        // RUTE INPUT HASIL (INTEGRASI)
        composable(
            route = Rute.INPUT_HASIL,
            arguments = listOf(navArgument("durasi") { type = NavType.LongType })
        ) { backStackEntry ->
            val durasi = backStackEntry.arguments?.getLong("durasi") ?: 0L

            LayarInputHasil(
                durasiDetik = durasi,
                onSelesaiSimpan = {
                    navController.navigate(Rute.BERANDA) {
                        popUpTo(Rute.BERANDA) { inclusive = true }
                    }
                }
            )
        }
    }
}