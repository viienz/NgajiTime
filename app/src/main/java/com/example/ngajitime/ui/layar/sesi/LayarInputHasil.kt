package com.example.ngajitime.ui.layar.sesi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.data.local.entity.SurahProgress

@Composable
fun LayarInputHasil(
    durasiDetik: Long,
    viewModel: SesiViewModel = hiltViewModel(),
    onSelesaiSimpan: () -> Unit
) {
    val listSurah by viewModel.listSurah.collectAsState()

    // State Form
    var selectedSurah by remember { mutableStateOf<SurahProgress?>(null) }
    var inputAyat by remember { mutableStateOf("") }
    var showDialogPilihSurah by remember { mutableStateOf(false) }

    // Hitung Estimasi Ayat yang dibaca (Visual Saja)
    val ayatAwal = selectedSurah?.ayatTerakhirDibaca ?: 0
    val ayatAkhir = inputAyat.toIntOrNull() ?: ayatAwal
    val selisih = (ayatAkhir - ayatAwal).coerceAtLeast(0)

    // --- UI DIALOG PILIH SURAH ---
    if (showDialogPilihSurah) {
        DialogPilihSurah(
            listData = listSurah,
            onDismiss = { showDialogPilihSurah = false },
            onPilih = { surah ->
                selectedSurah = surah
                // Otomatis isi input dengan ayat terakhir + 1 (Saran)
                inputAyat = (surah.ayatTerakhirDibaca + 1).coerceAtMost(surah.totalAyat).toString()
                showDialogPilihSurah = false
            }
        )
    }

    // --- UI UTAMA ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Alhamdulillah Selesai! 🎉", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        Text("Durasi Fokus: ${formatWaktu(durasiDetik)}", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // 1. PILIH SURAH (DROPDOWN CLICKABLE)
        Text("Surah yang dibaca:", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialogPilihSurah = true }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedSurah == null) {
                    Text("Pilih Surah...", color = Color.Gray, modifier = Modifier.weight(1f))
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(selectedSurah!!.namaSurah, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Text("Terakhir: Ayat ${selectedSurah!!.ayatTerakhirDibaca}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. INPUT AYAT TERAKHIR
        if (selectedSurah != null) {
            Text("Sampai Ayat Berapa?", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputAyat,
                onValueChange = { if (it.all { char -> char.isDigit() }) inputAyat = it },
                label = { Text("Ayat Terakhir") },
                placeholder = { Text("Contoh: 105") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Total ${selectedSurah!!.totalAyat} Ayat. (Kamu membaca +$selisih ayat)")
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 3. TOMBOL SIMPAN
        Button(
            onClick = {
                if (selectedSurah != null) {
                    val ayatBaru = inputAyat.toIntOrNull() ?: selectedSurah!!.ayatTerakhirDibaca
                    viewModel.simpanSesiTerintegrasi(
                        durasiDetik = durasiDetik,
                        surah = selectedSurah!!,
                        ayatTerakhirBaru = ayatBaru,
                        onSimpanSukses = onSelesaiSimpan
                    )
                } else {
                    // Jika tidak pilih surah (misal cuma mau log waktu saja)
                    // Kita bisa handle logic lain, tapi untuk sekarang kita paksa pilih surah
                    // Atau buat Dummy Surah jika perlu.
                }
            },
            enabled = selectedSurah != null && inputAyat.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Icon(Icons.Default.Check, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("SIMPAN PROGRESS")
        }
    }
}

// --- DIALOG PENCARIAN SURAH ---
@Composable
fun DialogPilihSurah(
    listData: List<SurahProgress>,
    onDismiss: () -> Unit,
    onPilih: (SurahProgress) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = listData.filter {
        it.namaSurah.contains(searchQuery, ignoreCase = true) ||
                it.nomorSurah.toString() == searchQuery
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().height(500.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pilih Surah", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar Kecil
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari Surah...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // List Surah
                LazyColumn {
                    items(filteredList) { surah ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPilih(surah) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFE8F5E9), androidx.compose.foundation.shape.CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${surah.nomorSurah}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(surah.namaSurah, fontWeight = FontWeight.Bold)
                                Text("Ayat Terakhir: ${surah.ayatTerakhirDibaca}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

fun formatWaktu(detik: Long): String {
    val m = detik / 60
    val s = detik % 60
    return "${m}m ${s}d"
}