package com.example.ngajitime.ui.navigasi

object Rute {
    // RUTE UTAMA
    const val INTRO = "layar_intro"
    const val LOGIN = "layar_login"
    const val BERANDA = "layar_beranda"

    // FITUR UTAMA
    const val LIST_SURAH = "layar_list_surah"
    const val STATS = "layar_stats"
    const val PROFIL = "layar_profil"

    // FITUR TIMER & HASIL
    const val TIMER = "layar_timer"
    const val INPUT_HASIL = "layar_input_hasil/{durasi}"

    fun inputHasil(durasi: Long): String {
        return "layar_input_hasil/$durasi"
    }
}