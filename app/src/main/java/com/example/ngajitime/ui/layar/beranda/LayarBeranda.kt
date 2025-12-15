package com.example.ngajitime.ui.layar.beranda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel



@Composable
fun LayarBeranda(
    viewModel: BerandaViewModel = hiltViewModel(),
    onKlikMulai: () -> Unit = {} ,
    onKeListSurah: () -> Unit = {},
    onKeStats: () -> Unit = {},
    onKeProfil: () -> Unit = {}
) {
    // 1. AMBIL DATA DARI VIEWMODEL
    val userTarget by viewModel.userTarget.collectAsState()
    val progressHariIni by viewModel.halamanHariIni.collectAsState() // Kita anggap ini "ayat" dulu sementara

    // --- PERBAIKAN DI SINI (Ganti Halaman jadi Ayat) ---
    // Ambil target ayat (default 50 jika null)
    val targetHarian = userTarget?.targetAyatHarian ?: 50

    // Hitung Sisa Target
    val sisaTarget = (targetHarian - progressHariIni).coerceAtLeast(0)

    // Warna Latar Hijau Segar
    val brushHeader = Brush.verticalGradient(
        colors = listOf(Color(0xFF66BB6A), Color(0xFF43A047))
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(
            onKeListSurah = onKeListSurah,
            onKeStats = onKeStats,
            onKeProfil = onKeProfil
        ) },
        floatingActionButton = {
            TombolMulaiNgaji(onClick = onKlikMulai)

        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // --- HEADER AREA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(brushHeader)
                    .padding(24.dp)
            ) {
                Column {
                    // Sapaan & Streak
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Assalamu'alaikum,",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Text(
                                text = userTarget?.namaUser ?: "Sobat Ngaji",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Chip Streak
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "${userTarget?.currentStreak ?: 0} Hari",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress Bar Khatam (Contoh dummy dulu)
                    Text("Progress Khatam", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.05f }, // Nanti kita ambil rumus (totalAyatDibaca / 6236)
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFD700),
                        trackColor = Color.White.copy(alpha = 0.3f),
                    )
                }
            }

            // --- KARTU TARGET HARIAN ---
            KartuTargetHarian(
                target = targetHarian,
                sisa = sisaTarget,
                progress = progressHariIni
            )

            // --- FITUR SMART ESTIMATION ---
            Text(
                text = "Punya waktu luang berapa lama?",
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ChipWaktu(10, viewModel)
                ChipWaktu(15, viewModel)
                ChipWaktu(30, viewModel)
            }
        }
    }
}

@Composable
fun KartuTargetHarian(target: Int, sisa: Int, progress: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-40).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Target Hari Ini", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))

            // Angka Besar
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$sisa",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                // UBAH TEKS JADI AYAT
                Text(
                    text = "/ $target Ayat",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val pesan = if (sisa == 0) "Alhamdulillah Tuntas! 🎉" else "Yuk, cicil ayatmu!"
            Text(
                text = pesan,
                color = if (sisa == 0) Color(0xFF2E7D32) else Color(0xFFFF9800),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TombolMulaiNgaji(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFFFFD700),
        contentColor = Color.Black,
        modifier = Modifier.size(72.dp).offset(y = (40).dp),
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Mulai Ngaji",
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun ChipWaktu(menit: Int, viewModel: BerandaViewModel) {
    SuggestionChip(
        onClick = {
            viewModel.hitungEstimasi(menit) { hasil ->
                // Hasil estimasi (sementara masih logika halaman, nanti kita update jadi ayat)
                println("Estimasi: $hasil")
            }
        },
        label = { Text("$menit Menit") },
        icon = { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp)) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = Color.White,
            labelColor = Color(0xFF2E7D32)
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = Color(0xFF2E7D32)
        )
    )
}

@Composable
fun BottomNavigationBar(
    onKeListSurah: () -> Unit = {},
    onKeStats: () -> Unit = {},
    onKeProfil: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Tombol Beranda (Aktif)
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Beranda") },
            selected = true,
            onClick = { /* Sudah di beranda, tidak perlu aksi */ },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFC8E6C9))
        )

        Spacer(modifier = Modifier.weight(1f))

        NavigationBarItem(
            icon = { Icon(Icons.Default.MenuBook, contentDescription = null) }, // Ganti Icon jadi Buku
            label = { Text("Surah") },
            selected = false,
            onClick = onKeListSurah // <-- Panggil aksi pindah layar
        )

        Spacer(modifier = Modifier.weight(1f))

        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) }, // Icon Grafik
            label = { Text("Stats") },
            selected = false,
            onClick = onKeStats
        )

        Spacer(modifier = Modifier.weight(1f))

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profil") },
            selected = false,
            onClick = onKeProfil
        )


    }
}