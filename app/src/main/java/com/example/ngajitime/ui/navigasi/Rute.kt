package com.example.ngajitime.ui.navigasi

object Rute {
    // --- RUTE UTAMA ---
    const val INTRO = "layar_intro"       // Jika ada onboarding
    const val LOGIN = "layar_login"
    const val BERANDA = "layar_beranda"

    // --- FITUR UTAMA ---
    const val LIST_SURAH = "layar_list_surah"
    const val STATS = "layar_stats"
    const val PROFIL = "layar_profil"

    // --- FITUR TIMER & HASIL (YANG KITA UPDATE) ---
    const val TIMER = "layar_timer"

    // Rute Dinamis: Wajib pakai kurung kurawal { } untuk nama variabel
    // NavGraph akan membacanya sebagai: "Di sini nanti ada data bernama 'durasi'"
    const val INPUT_HASIL = "layar_input_hasil/{durasi}"

    // Helper Function: Fungsi ini dipanggil saat mau PINDAH layar
    // Gunanya untuk menempelkan angka durasi asli ke dalam alamat rute
    // Contoh Hasil: "layar_input_hasil/1800" (Jika durasinya 1800 detik)
    fun inputHasil(durasi: Long): String {
        return "layar_input_hasil/$durasi"
    }
}