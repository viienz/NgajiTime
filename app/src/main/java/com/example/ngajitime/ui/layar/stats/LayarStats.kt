package com.example.ngajitime.ui.layar.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.ui.komponen.MenuAktif
import com.example.ngajitime.ui.komponen.NgajiBottomBar

@Composable
fun LayarStats(
    viewModel: StatsViewModel = hiltViewModel(),
    onKeBeranda: () -> Unit,
    onKeSurah: () -> Unit,
    onKeProfil: () -> Unit
) {
    // 1. Data dari ViewModel
    val listPiala by viewModel.koleksiPiala.collectAsState()
    val dataGrafik by viewModel.dataMingguan.collectAsState()
    val totalMingguIni by viewModel.totalAyatMingguIni.collectAsState()
    val rentangTanggal by viewModel.rentangTanggal.collectAsState()
    val offset by viewModel.offsetMinggu.collectAsState()

    // Hitung Persentase Piala
    val unlockedCount = listPiala.count { it.isUnlocked }
    val totalPiala = listPiala.size
    val progressPersen = if (totalPiala > 0) unlockedCount.toFloat() / totalPiala else 0f

    // Skala Grafik
    val maxAyat = dataGrafik.maxOfOrNull { it.totalAyat } ?: 10
    val skalaMax = if (maxAyat == 0) 10 else maxAyat

    Scaffold(
        bottomBar = {
            NgajiBottomBar(
                menuAktif = MenuAktif.STATS,
                onKeBeranda = onKeBeranda,
                onKeSurah = onKeSurah,
                onKeStats = {},
                onKeProfil = onKeProfil
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 Kolom
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = 24.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = padding.calculateBottomPadding() + 20.dp
            ),
            modifier = Modifier.padding(top = padding.calculateTopPadding())
        ) {

            item(span = { GridItemSpan(2) }) {
                Column {
                    Text(
                        text = "Statistik & Prestasi",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Hall of Fame", fontSize = 14.sp, color = Color.Gray)
                                Text("$unlockedCount / $totalPiala Terbuka", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { progressPersen },
                                    modifier = Modifier.size(50.dp),
                                    color = Color(0xFFFFD700),
                                    trackColor = Color.LightGray.copy(alpha = 0.3f),
                                )
                                Text("${(progressPersen * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(380.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Minggu Ini", fontSize = 12.sp, color = Color.Gray)
                                    Text("$totalMingguIni Ayat", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.geserMinggu(-1) }) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "Mundur")
                                }
                                Text(text = rentangTanggal, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                IconButton(onClick = { viewModel.geserMinggu(1) }, enabled = offset < 0) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Maju", tint = if (offset < 0) Color.Black else Color.LightGray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

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
            }

            item(span = { GridItemSpan(2) }) {
                Text(
                    "Daftar Piala",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(listPiala) { piala ->
                ItemPiala(piala = piala)
            }
        }
    }
}

@Composable
fun ItemPiala(piala: Piala) {
    val containerColor = if (piala.isUnlocked) Color.White else Color(0xFFEEEEEE)
    val contentAlpha = if (piala.isUnlocked) 1f else 0.5f
    val iconColor = if (piala.isUnlocked) piala.warna else Color.Gray

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (piala.isUnlocked) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (piala.isUnlocked) {
                    Box(modifier = Modifier.size(50.dp).background(piala.warna.copy(alpha = 0.2f), CircleShape))
                }
                Icon(imageVector = piala.icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
                if (!piala.isUnlocked) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(14.dp).align(Alignment.BottomEnd))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(piala.judul, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center, color = Color.Black)
            Text(piala.syarat, fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}