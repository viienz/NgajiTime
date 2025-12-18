package com.example.ngajitime.ui.layar.listsurah

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.ui.komponen.MenuAktif
import com.example.ngajitime.ui.komponen.NgajiBottomBar

@Composable
fun LayarListSurah(
    viewModel: ListSurahViewModel = hiltViewModel(),
    // --- PARAMETER NAVIGASI BARU ---
    onKeBeranda: () -> Unit,
    onKeStats: () -> Unit,
    onKeProfil: () -> Unit,
    // -------------------------------
    onKlikSurah: (SurahProgress) -> Unit = {}
) {
    val listSurah by viewModel.uiSurahList.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val surahSelesai = listSurah.count { it.isKhatam }
    var selectedSurah by remember { mutableStateOf<SurahProgress?>(null) }

    // Logika Dialog Update (Tetap Sama)
    if (selectedSurah != null) {
        DialogUpdateSurah(
            surah = selectedSurah!!,
            onDismiss = { selectedSurah = null },
            onSimpan = { ayatBaru ->
                viewModel.simpanProgresSurah(selectedSurah!!, ayatBaru)
                selectedSurah = null
            }
        )
    }

    // --- STRUKTUR BARU DENGAN SCAFFOLD ---
    Scaffold(
        bottomBar = {
            NgajiBottomBar(
                menuAktif = MenuAktif.SURAH, // Menu Surah Aktif
                onKeBeranda = onKeBeranda,
                onKeSurah = {}, // Sedang di sini
                onKeStats = onKeStats,
                onKeProfil = onKeProfil
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

        // Konten Utama (Logika Anda dipindah ke sini)
        Column(
            modifier = Modifier
                .padding(padding) // Penting! Agar tidak ketutup bottom bar
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- HEADER ---
            Text(
                text = "List Surah",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Khatam : $surahSelesai/114",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )

            // --- SEARCH BAR ---
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Surah apa yang kamu cari?") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF2E7D32)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- LIST SURAH ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp) // Padding bawah tambahan
            ) {
                items(listSurah, key = { it.nomorSurah }) { surah ->
                    ItemSurah(
                        surah = surah,
                        onClick = {
                            selectedSurah = surah // Buka Dialog
                        }
                    )
                }
            }
        }
    }
}

// Komponen ItemSurah (Tetap Sama Persis)
@Composable
fun ItemSurah(surah: SurahProgress, onClick: () -> Unit) {
    val progress = if (surah.totalAyat > 0) surah.ayatTerakhirDibaca.toFloat() / surah.totalAyat else 0f
    val isStarted = surah.ayatTerakhirDibaca > 0
    val cardColor = if (surah.isKhatam) Color(0xFFE8F5E9) else Color.White

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("${surah.nomorSurah}. ${surah.namaSurah}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF424242))
                    Text("${surah.artiSurah} • ${surah.totalAyat} Ayat", fontSize = 12.sp, color = Color.Gray)
                }
                if (isStarted) {
                    Text("${surah.ayatTerakhirDibaca}/${surah.totalAyat}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
            if (isStarted) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Progress Ayat : ${surah.ayatTerakhirDibaca}", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.width(100.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF2E7D32),
                        trackColor = Color.LightGray.copy(alpha = 0.4f),
                    )
                }
            }
        }
    }
}