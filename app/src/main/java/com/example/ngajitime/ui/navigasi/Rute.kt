package com.example.ngajitime.ui.navigasi

object Rute {
    const val INTRO = "layar_intro"
    const val BERANDA = "layar_beranda"

    const val LIST_SURAH = "layar_list_surah"
    const val TIMER = "layar_timer"
    const val INPUT_HASIL = "layar_input_hasil/{durasi}"
    const val STATS = "layar_stats"
    const val PROFIL = "layar_profil"

    // Helper untuk membuat alamat dengan data
    fun inputHasil(durasi: Long) = "layar_input_hasil/$durasi"
}