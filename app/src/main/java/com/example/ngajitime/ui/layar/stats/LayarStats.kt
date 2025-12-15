package com.example.ngajitime.ui.layar.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun LayarStats(
    viewModel: StatsViewModel = hiltViewModel(),
    onBack: () -> Unit // Opsional, jaga-jaga kalau butuh
) {
    // 1. Ambil Data dari ViewModel
    val dataGrafik by viewModel.dataMingguan.collectAsState()
    val totalMingguIni by viewModel.totalAyatMingguIni.collectAsState()
    val user by viewModel.userTarget.collectAsState() // Data user untuk Badges

    // 2. Hitung Skala Grafik (Agar batang tertinggi tidak mentok atap)
    // Cari nilai tertinggi, minimal 10 agar tidak error pembagian nol
    val maxAyat = dataGrafik.maxOfOrNull { it.totalAyat } ?: 10
    val skalaMax = if (maxAyat == 0) 10 else maxAyat

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Background Abu Lembut
            .padding(16.dp)
            .padding(top = 36.dp)
            .verticalScroll(rememberScrollState()) // Agar bisa discroll kalau HP kecil
    ) {
        // --- HEADER ---
        Text(
            text = "Statistik Kamu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- BAGIAN 1: GRAFIK MINGGUAN (VERSI RAPI) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp), // Sedikit lebih tinggi biar lega
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp), // Sudut lebih bulat
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Info Total Ayat
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Minggu Ini",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$totalMingguIni Ayat",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- AREA GRAFIK (TRACK STYLE) ---
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween, // Jarak otomatis
                    verticalAlignment = Alignment.Bottom
                ) {
                    dataGrafik.forEach { data ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            // Angka di atas batang (Cuma muncul kalau > 0 biar rapi)
                            if (data.totalAyat > 0) {
                                Text(
                                    text = "${data.totalAyat}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            } else {
                                // Spacer kosong biar tinggi tetap terjaga
                                Spacer(modifier = Modifier.height(18.dp))
                            }

                            // BATANG GRAFIK
                            // Kita pakai Box tumpuk: Latar Abu (Track) + Depan Hijau (Progress)
                            Box(
                                contentAlignment = Alignment.BottomCenter,
                                modifier = Modifier
                                    .width(16.dp) // Lebar batang
                                    .weight(1f)   // Pakai weight biar ngisi tinggi sisa
                                    .clip(RoundedCornerShape(50)) // Bulat penuh (kapsul)
                                    .background(Color(0xFFF1F8E9)) // Latar Abu-Hijau Sangat Muda (Track)
                            ) {
                                // Batang Hijau (Isi)
                                val tinggiPersen = (data.totalAyat.toFloat() / skalaMax.toFloat())

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(fraction = tinggiPersen)
                                        .background(
                                            if (data.totalAyat > 0) Color(0xFF2E7D32)
                                            else Color.Transparent // Kalau 0, transparan aja
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Label Hari
                            Text(
                                text = data.hari,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (data.totalAyat > 0) Color.Black else Color.Gray // Hitam jika ada isi
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- BAGIAN 2: KOLEKSI PIALA (BADGES) ---
        Text(
            text = "Koleksi Piala",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Jarak antar piala rata
        ) {
            // BADGE 1: PEMULA
            // Syarat: Sudah pernah baca minimal 1 ayat
            ItemBadge(
                nama = "Pemula",
                syarat = "Mulai Baca",
                icon = Icons.Default.Star,
                isUnlocked = (user?.totalAyatDibaca ?: 0) > 0
            )

            // BADGE 2: ISTIQOMAH
            // Syarat: Streak minimal 3 hari
            ItemBadge(
                nama = "Istiqomah",
                syarat = "Streak 3 Hari",
                icon = Icons.Default.LocalFireDepartment,
                isUnlocked = (user?.currentStreak ?: 0) >= 3
            )

            // BADGE 3: SULTAN
            // Syarat: Total baca 1000 ayat
            ItemBadge(
                nama = "Sultan",
                syarat = "1000 Ayat",
                icon = Icons.Default.WorkspacePremium,
                isUnlocked = (user?.totalAyatDibaca ?: 0) >= 1000
            )
        }
    }
}

// --- KOMPONEN KECIL: ITEM PIALA ---
@Composable
fun ItemBadge(
    nama: String,
    syarat: String,
    icon: ImageVector,
    isUnlocked: Boolean
) {
    // Tentukan Warna: Emas (Aktif) atau Abu (Terkunci)
    val warnaUtama = if (isUnlocked) Color(0xFFFFD700) else Color.Gray
    val warnaBackground = if (isUnlocked) Color.White else Color.LightGray.copy(alpha = 0.2f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        // Lingkaran Icon
        Card(
            shape = RoundedCornerShape(50), // Bulat Penuh
            colors = CardDefaults.cardColors(containerColor = warnaBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = if(isUnlocked) 4.dp else 0.dp),
            modifier = Modifier
                .size(70.dp)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = warnaUtama,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Teks Nama Badge
        Text(
            text = nama,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if(isUnlocked) Color.Black else Color.Gray
        )

        // Teks Syarat
        Text(
            text = syarat,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}