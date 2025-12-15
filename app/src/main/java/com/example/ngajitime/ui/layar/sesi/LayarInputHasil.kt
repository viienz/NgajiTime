package com.example.ngajitime.ui.layar.sesi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LayarInputHasil(
    durasiDetik: Long,
    viewModel: SesiViewModel = hiltViewModel(),
    onSelesaiSimpan: () -> Unit
) {
    var inputAngka by remember { mutableStateOf("") }
    val menit = durasiDetik / 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- HEADER ---
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Alhamdulillah!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Fokus selama $menit menit.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // --- KARTU INPUT ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // UPDATE TEKS PERTANYAAN
                Text(
                    text = "Dapat berapa ayat?", // <-- Ganti jadi Ayat
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = inputAngka,
                    onValueChange = { if (it.all { char -> char.isDigit() }) inputAngka = it },
                    textStyle = MaterialTheme.typography.displayMedium.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- TOMBOL SIMPAN ---
        Button(
            onClick = {
                val jumlahAyat = inputAngka.toIntOrNull() ?: 0
                if (jumlahAyat > 0) {
                    // Panggil ViewModel
                    viewModel.simpanSesi(durasiDetik, jumlahAyat) {
                        onSelesaiSimpan()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(16.dp),
            enabled = inputAngka.isNotEmpty()
        ) {
            Text("Simpan Pencapaian", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}