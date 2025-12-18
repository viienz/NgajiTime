package com.example.ngajitime.ui.layar.beranda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.R
import com.example.ngajitime.ui.komponen.MenuAktif
import com.example.ngajitime.ui.komponen.NgajiBottomBar
import java.util.Calendar

// --- WARNA ---
val HijauStreak = Color(0xFFAEEA5C)
val HijauTombol = Color(0xFF4CAF50)
val BiruMudaBg = Color(0xFFF0F4F8)

@Composable
fun LayarBeranda(
    viewModel: BerandaViewModel = hiltViewModel(), // Inject ViewModel
    onKeSurah: () -> Unit,
    onKeStats: () -> Unit,
    onKeProfil: () -> Unit,
    onKeTimer: () -> Unit
) {
    // --- 1. AMBIL DATA REAL-TIME DARI DATABASE ---
    val user by viewModel.userTarget.collectAsState()
    val progressPersen by viewModel.progressPersen.collectAsState()
    val lastReadData by viewModel.lastRead.collectAsState()
    val halamanHariIni by viewModel.halamanHariIni.collectAsState()

    val targetAyat = user?.targetAyatHarian ?: 1
    val currentAyat = halamanHariIni

    // --- 2. SIAPKAN VARIABEL UI ---
    val namaUser = user?.namaUser ?: "Sobat Ngaji"
    val streakCount = user?.currentStreak ?: 0

    // Data Last Read
    val lastReadSurah = lastReadData?.namaSurah ?: "Belum ada"
    val lastReadAyat = lastReadData?.ayatTerakhirDibaca ?: 0
    val lastReadInfo = if (lastReadData != null) "Lanjutkan progressmu" else "Ayo mulai mengaji"

    Scaffold(
        // FAB: Tombol Timer (Play)
        floatingActionButton = {
            FloatingActionButton(
                onClick = onKeTimer,
                containerColor = HijauTombol,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Mulai Timer", modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.End,

        // BOTTOM BAR: Navigasi Bawah
        bottomBar = {
            NgajiBottomBar(
                menuAktif = MenuAktif.BERANDA,
                onKeBeranda = {},
                onKeSurah = onKeSurah,
                onKeStats = onKeStats,
                onKeProfil = onKeProfil
            )
        },
        containerColor = BiruMudaBg
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp) // Padding bawah agar tidak ketutup FAB
        ) {
            // A. HEADER SECTION (Kartu Awan)
            item {
                HeaderSection(namaUser = namaUser)
            }

            // B. KONTEN UTAMA
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // Jarak Header ke Streak (Diperkecil agar rapi)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Kartu Streak (Data Asli)
                    StreakCardLarge(streakCount = streakCount)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Baris Progress & Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProgressHarianCard(
                            progressPersen = progressPersen,
                            currentAyat = currentAyat,  // <--- Kirim Ayat Terbaca
                            targetAyat = targetAyat,    // <--- Kirim Target User
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadgeCard(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Kartu Terakhir Dibaca (Data Asli)
                    LastReadCard(
                        surah = lastReadSurah,
                        ayat = lastReadAyat,
                        info = lastReadInfo
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Hijau Panjang (Ke List Surah)
                    Button(
                        onClick = onKeSurah,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HijauTombol),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Mulai Mengaji", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Kalender (Visual Saja)
                    KalenderMingguanSection()
                }
            }
        }
    }
}

// ================= KOMPONEN UI =================

@Composable
fun HeaderSection(namaUser: String) {
    // Ambil Jam Sistem Saat Ini
    val calendar = Calendar.getInstance()
    val jam = calendar.get(Calendar.HOUR_OF_DAY)
    val menit = calendar.get(Calendar.MINUTE)

    // Format Jam (misal 08:05)
    val jamString = String.format("%02d : %02d", jam, menit)
    val infoWaktu = getInfoWaktu(jam)

    // Gunakan CARD agar sudut melengkung sempurna
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp) // Jarak Kiri Kanan
            .padding(top = 24.dp)        // Jarak Atas
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image
            Image(
                painter = painterResource(id = R.drawable.bg_header_awan), // Pastikan file ada
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlay Gelap
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            // 3. Konten Teks
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
            ) {
                // Baris Atas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Assalamualaikum,", color = Color.White, fontSize = 12.sp)
                        Text(namaUser, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                // Jam & Info (Tengah)
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = jamString,
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    blurRadius = 10f
                                )
                            )
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = infoWaktu,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreakCardLarge(streakCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HijauStreak)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Day Streak", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Kamu Hebat, Lanjutkan\nIstiqomahnya 🔥",
                    fontSize = 12.sp,
                    color = Color(0xFF455A64),
                    lineHeight = 16.sp
                )
            }
            Text("$streakCount", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
        }
    }
}

@Composable
fun ProgressHarianCard(
    progressPersen: Int,
    modifier: Modifier = Modifier,
    currentAyat: Int,
    targetAyat: Int,
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Progress", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Outlined.TrendingUp, contentDescription = null, tint = Color.Blue, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    // TAMPILKAN ANGKA TARGET (Koneksi ke Fitur Lama) 🎯
                    Text(
                        text = "$currentAyat / $targetAyat Ayat", // Contoh: "15 / 30 Ayat"
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Teks Status (Berubah sesuai kondisi)
                    val pesanSemangat = if (progressPersen >= 100) "Target Tercapai! 🎉" else "Sedikit lagi!"
                    Text(
                        text = pesanSemangat,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progressPersen / 100f },
                        modifier = Modifier.size(50.dp),
                        color = HijauStreak,
                        trackColor = Color.LightGray.copy(alpha = 0.3f),
                        strokeWidth = 5.dp
                    )
                    Text("$progressPersen%", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatusBadgeCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Status 🏆", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            // Ikon Piala (Safe Fallback)
            Image(
                painter = painterResource(id = R.drawable.ic_trophy_badge), // Pastikan file ada
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "\"Si Paling\nRajin Mengaji\"",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LastReadCard(surah: String, ayat: Int, info: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Ikon Buku (Safe Fallback)
            Image(
                painter = painterResource(id = R.drawable.ic_last_read_book), // Pastikan file ada
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Terakhir dibaca", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val teksAyat = if(ayat > 0) "Ayat $ayat" else ""
                    Text("$surah $teksAyat", fontWeight = FontWeight.Medium)
                }
                Text("($info)", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun KalenderMingguanSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Oktober 2025", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text("← →", fontSize = 18.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            KalenderItem("Min", "01", false)
            KalenderItem("Sen", "02", true) // Ceritanya hari ini
            KalenderItem("Sel", "03", false)
            KalenderItem("Rab", "04", false)
            KalenderItem("Ka", "05", false)
            KalenderItem("Jum", "06", false)
            KalenderItem("Sab", "07", false)
        }
    }
}

@Composable
fun KalenderItem(hari: String, tanggal: String, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) HijauStreak else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(hari, fontSize = 12.sp, color = if (isActive) Color(0xFF263238) else Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(tanggal, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isActive) Color(0xFF263238) else Color.Black)
    }
}

// Helper: Menentukan Waktu Sholat (Sederhana)
fun getInfoWaktu(jam: Int): String {
    return when (jam) {
        in 3..4 -> "Waktu Tahajud"
        in 5..5 -> "Waktu Subuh"
        in 6..6 -> "Waktu Syuruq"
        in 7..10 -> "Waktu Dhuha"
        in 11..14 -> "Waktu Dzuhur"
        in 15..17 -> "Waktu Ashar"
        in 18..18 -> "Waktu Maghrib"
        in 19..23 -> "Waktu Isya"
        else -> "Waktu Istirahat"
    }
}