package com.example.ngajitime.ui.layar.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.ui.komponen.MenuAktif
import com.example.ngajitime.ui.komponen.NgajiBottomBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun LayarStats(
    viewModel: StatsViewModel = hiltViewModel(),
    // --- PARAMETER NAVIGASI BARU ---
    onKeBeranda: () -> Unit,
    onKeSurah: () -> Unit,
    onKeProfil: () -> Unit
    // -------------------------------
) {
    // 1. Ambil Data dari ViewModel (Logika Anda)
    val dataGrafik by viewModel.dataMingguan.collectAsState()
    val totalMingguIni by viewModel.totalAyatMingguIni.collectAsState()
    val user by viewModel.userTarget.collectAsState()
    val rentangTanggal by viewModel.rentangTanggal.collectAsState() // <-- Ambil dari ViewModel
    val offset by viewModel.offsetMinggu.collectAsState()

    // 2. Hitung Skala Grafik
    val maxAyat = dataGrafik.maxOfOrNull { it.totalAyat } ?: 10
    val skalaMax = if (maxAyat == 0) 10 else maxAyat

    val rentangWaktu = remember { getRentangTanggalMingguIni() }

    // --- STRUKTUR BARU DENGAN SCAFFOLD ---
    Scaffold(
        bottomBar = {
            NgajiBottomBar(
                menuAktif = MenuAktif.STATS, // Menu Stats Aktif
                onKeBeranda = onKeBeranda,
                onKeSurah = onKeSurah,
                onKeStats = {}, // Sedang di sini
                onKeProfil = onKeProfil
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

        // Konten Utama
        Column(
            modifier = Modifier
                .padding(padding) // Penting!
                .fillMaxSize()
                .padding(16.dp)
                // Hapus padding(top=36.dp) karena sudah ada dari sistem Scaffold
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER
            Text(
                text = "Statistik Kamu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // BAGIAN 1: GRAFIK MINGGUAN (Isi tetap sama)
            Card(
                modifier = Modifier.fillMaxWidth().height(350.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // --- HEADER GRAFIK DENGAN NAVIGASI < > ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Kiri: Total Ayat & Ikon
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Total Bacaan", fontSize = 12.sp, color = Color.Gray)
                                Text("$totalMingguIni Ayat", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }

                        // Kanan: Navigasi Tanggal
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Tombol Mundur (<)
                            IconButton(onClick = { viewModel.geserMinggu(-1) }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Mundur")
                            }

                            // Teks Tanggal
                            Text(
                                text = rentangTanggal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )

                            // Tombol Maju (>) - Disable kalau sudah di minggu ini (masa depan belum ada)
                            IconButton(
                                onClick = { viewModel.geserMinggu(1) },
                                enabled = offset < 0 // Cuma bisa diklik kalau kita lagi lihat masa lalu
                            ) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Maju",
                                    tint = if (offset < 0) Color.Black else Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // AREA GRAFIK
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dataGrafik.forEach { data ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
                                if (data.totalAyat > 0) {
                                    Text("${data.totalAyat}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.padding(bottom = 6.dp))
                                } else {
                                    Spacer(modifier = Modifier.height(18.dp))
                                }
                                Box(
                                    contentAlignment = Alignment.BottomCenter,
                                    modifier = Modifier.width(16.dp).weight(1f).clip(RoundedCornerShape(50)).background(Color(0xFFF1F8E9))
                                ) {
                                    val tinggiPersen = (data.totalAyat.toFloat() / skalaMax.toFloat())
                                    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(fraction = tinggiPersen).background(if (data.totalAyat > 0) Color(0xFF2E7D32) else Color.Transparent))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(data.hari, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (data.totalAyat > 0) Color.Black else Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BAGIAN 2: KOLEKSI PIALA (Isi tetap sama)
            Text("Koleksi Piala", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.padding(bottom = 16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ItemBadge("Pemula", "Mulai Baca", Icons.Default.Star, (user?.totalAyatDibaca ?: 0) > 0)
                ItemBadge("Istiqomah", "Streak 3 Hari", Icons.Default.LocalFireDepartment, (user?.currentStreak ?: 0) >= 3)
                ItemBadge("Sultan", "1000 Ayat", Icons.Default.WorkspacePremium, (user?.totalAyatDibaca ?: 0) >= 1000)
            }
        }
    }
}

// Komponen ItemBadge (Tetap Sama)
@Composable
fun ItemBadge(nama: String, syarat: String, icon: ImageVector, isUnlocked: Boolean) {
    val warnaUtama = if (isUnlocked) Color(0xFFFFD700) else Color.Gray
    val warnaBackground = if (isUnlocked) Color.White else Color.LightGray.copy(alpha = 0.2f)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Card(
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = warnaBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = if(isUnlocked) 4.dp else 0.dp),
            modifier = Modifier.size(70.dp).padding(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = warnaUtama, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = nama, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(isUnlocked) Color.Black else Color.Gray)
        Text(text = syarat, fontSize = 10.sp, color = Color.Gray)
    }
}

fun getRentangTanggalMingguIni(): String {
    val calendar = Calendar.getInstance()

    // Tanggal Hari Ini (Akhir Periode)
    val formatTanggal = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) // Format Indonesia
    val tanggalAkhir = formatTanggal.format(calendar.time)

    // Mundur 6 hari ke belakang (Awal Periode)
    calendar.add(Calendar.DAY_OF_YEAR, -6)
    val formatTanggalPendek = SimpleDateFormat("dd", Locale("id", "ID")) // Cuma ambil tanggalnya
    val tanggalAwal = formatTanggalPendek.format(calendar.time)

    // Gabungkan
    return "$tanggalAwal - $tanggalAkhir"
}