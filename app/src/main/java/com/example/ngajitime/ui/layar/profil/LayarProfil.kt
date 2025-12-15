package com.example.ngajitime.ui.layar.profil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LayarProfil(
    viewModel: ProfilViewModel = hiltViewModel(),
    onBack: () -> Unit // Opsional jika nanti dipakai
) {
    val user by viewModel.userTarget.collectAsState()
    var showDialogTarget by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER PROFIL ---
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar Bulat Besar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nama User
        Text(
            text = user?.namaUser ?: "Sobat Ngaji",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Teruslah istiqomah!",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- KARTU PENGATURAN TARGET ---
        Text(
            text = "Target Saat Ini",
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialogTarget = true }, // Klik untuk edit
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ikon Gear
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Info Target
                Column(modifier = Modifier.weight(1f)) {
                    val modeTeks = if (user?.modeTarget == "WAKTU") "Mode Santai" else "Mode Khatam"
                    val detailTeks = if (user?.modeTarget == "WAKTU")
                        "${user?.waktuLuangMenit} Menit / hari"
                    else
                        "Target ${user?.durasiTargetHari} Hari"

                    Text(text = modeTeks, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = detailTeks, color = Color.Gray, fontSize = 14.sp)
                }

                // Tombol Edit Kecil
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF2E7D32)
                )
            }
        }
    }

    // --- POPUP DIALOG EDIT TARGET ---
    if (showDialogTarget) {
        DialogEditTarget(
            currentMode = user?.modeTarget ?: "WAKTU",
            currentValue = if (user?.modeTarget == "WAKTU") user?.waktuLuangMenit ?: 15 else user?.durasiTargetHari ?: 30,
            onDismiss = { showDialogTarget = false },
            onSimpan = { mode, nilai ->
                viewModel.updateTarget(mode, nilai)
                showDialogTarget = false
            }
        )
    }
}

// Komponen Popup Dialog
@Composable
fun DialogEditTarget(
    currentMode: String,
    currentValue: Int,
    onDismiss: () -> Unit,
    onSimpan: (String, Int) -> Unit
) {
    var selectedMode by remember { mutableStateOf(currentMode) }
    var inputNilai by remember { mutableStateOf(currentValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atur Target Ngaji") },
        text = {
            Column {
                // Pilihan Radio Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedMode == "WAKTU", onClick = { selectedMode = "WAKTU" })
                    Text("Santai (Waktu Luang)")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedMode == "DEADLINE", onClick = { selectedMode = "DEADLINE" })
                    Text("Khatam (Target Hari)")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Angka
                OutlinedTextField(
                    value = inputNilai,
                    onValueChange = { if (it.all { c -> c.isDigit() }) inputNilai = it },
                    label = { Text(if (selectedMode == "WAKTU") "Menit per hari" else "Ingin khatam berapa hari?") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Info Estimasi
                val nilai = inputNilai.toIntOrNull() ?: 0
                val estimasi = if (selectedMode == "WAKTU") nilai * 3 else if (nilai > 0) 6236 / nilai else 0

                Text(
                    text = "Target Baru: ±$estimasi Ayat / hari",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSimpan(selectedMode, inputNilai.toIntOrNull() ?: 0) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = Color.Gray) }
        }
    )
}