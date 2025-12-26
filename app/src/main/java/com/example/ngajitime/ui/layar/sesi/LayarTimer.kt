package com.example.ngajitime.ui.layar.sesi

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.service.TimerService
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit

@Composable
fun LayarTimer(
    viewModel: SesiViewModel = hiltViewModel(),
    onSelesai: (Long) -> Unit = {},
    onBatal: () -> Unit = {}
) {
    // Ambil Data dari ViewModel
    val waktuTersisa by viewModel.waktuTersisa.collectAsState()
    val status by viewModel.statusTimer.collectAsState()

    // State Lokal untuk Durasi yang Dipilih (Misal: 30 menit)
    var durasiDipilihMenit by remember { mutableIntStateOf(30) }

    // BACKGROUND ESTETIK (Tetap Sama)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20), Color(0xFF003300)),
                    center = androidx.compose.ui.geometry.Offset.Unspecified,
                    radius = 1200f
                )
            )
    ) {

        if (status == TimerService.TimerStatus.IDLE) {
            // TAMPILAN SETUP (Tetap Sama)
            TampilanSetupTimer(
                durasiDipilih = durasiDipilihMenit,
                onPilihDurasi = { durasiDipilihMenit = it },
                onMulai = { viewModel.mulaiTimer(durasiDipilihMenit) },
                onBatal = onBatal
            )
        } else {
            // TAMPILAN COUNTDOWN (UPDATE LOGIKA DISINI) ✅
            TampilanCountdown(
                detikSisa = waktuTersisa,
                status = status,
                onStop = {
                    viewModel.batalkanTimer()

                    // 1. Hitung Total Detik Target (Misal: 1 menit = 60 detik)
                    val targetDetik = durasiDipilihMenit * 60L

                    // 2. Hitung Durasi Real yang Berjalan
                    val durasiReal = if (status == TimerService.TimerStatus.FINISHED) {
                        targetDetik // Kalau selesai normal, ambil full target
                    } else {
                        // Kalau stop tengah jalan: Target - Sisa
                        (targetDetik - waktuTersisa).coerceAtLeast(0)
                    }

                    // 3. Kirim Durasi Asli ke Layar Input
                    onSelesai(durasiReal)
                }
            )
        }
    }
}

// UI 1: PILIH DURASI
@Composable
fun TampilanSetupTimer(
    durasiDipilih: Int,
    onPilihDurasi: (Int) -> Unit,
    onMulai: () -> Unit,
    onBatal: () -> Unit
) {
    // State untuk Dialog Custom
    var showCustomDialog by remember { mutableStateOf(false) }

    // Jika dialog aktif, tampilkan
    if (showCustomDialog) {
        DialogCustomWaktu(
            onDismiss = { showCustomDialog = false },
            onSimpan = { menitCustom ->
                onPilihDurasi(menitCustom)
                showCustomDialog = false
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tombol Batal
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            IconButton(onClick = onBatal, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }

        Text("Mau Fokus Berapa Lama?", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Tampilkan Angka Besar Durasi yang Dipilih
        Text(
            text = "$durasiDipilih Menit",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700), // Emas
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Grid Pilihan Waktu (Tambah Opsi -1 untuk Custom)
        val pilihan = listOf(15, 30, 45, 60, -1)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center, // Rata Tengah
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kita pecah jadi 2 baris atau flow row kalau mau rapi,
            // tapi untuk simpelnya kita pakai Row horizontal scrollable atau wrap
            // Disini saya pakai Row biasa dengan Logika Tampilan Khusus

            // Agar rapi, kita buat tombol pilihan
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(15, 30, 45, 60).forEach { menit ->
                    TimerChip(
                        label = "$menit",
                        isSelected = durasiDipilih == menit,
                        onClick = { onPilihDurasi(menit) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tombol Custom (Lainnya) terpisah biar rapi
        TimerChip(
            label = "Atur Sendiri ✏️",
            isSelected = false, // Tidak perlu highlight permanen
            onClick = { showCustomDialog = true },
            modifier = Modifier.width(150.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Tombol Mulai
        Button(
            onClick = onMulai,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Icon(Icons.Default.PlayArrow, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("MULAI FOKUS", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// KOMPONEN KECIL: CHIP WAKTU
@Composable
fun TimerChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .widthIn(min = 50.dp) // Lebar minimal
            .clip(RoundedCornerShape(25.dp)) // Lonjong
            .background(if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// KOMPONEN BARU: DIALOG INPUT CUSTOM
@Composable
fun DialogCustomWaktu(
    onDismiss: () -> Unit,
    onSimpan: (Int) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atur Waktu (Menit)") },
        text = {
            OutlinedTextField(
                value = textInput,
                onValueChange = { if (it.all { char -> char.isDigit() }) textInput = it },
                label = { Text("Contoh: 20") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val menit = textInput.toIntOrNull() ?: 0
                    if (menit > 0) onSimpan(menit)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Set Waktu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        },
        containerColor = Color.White
    )
}


// UI 2: COUNTDOWN ANIMASI
@Composable
fun TampilanCountdown(
    detikSisa: Long,
    status: TimerService.TimerStatus,
    onStop: () -> Unit
) {
    val jam = detikSisa / 3600
    val menit = (detikSisa % 3600) / 60
    val detik = detikSisa % 60
    val waktuFormat = String.format("%02d : %02d : %02d", jam, menit, detik)

    val pesanStatus = if (status == TimerService.TimerStatus.FINISHED) "Waktu Habis! 🎉" else "Sedang Mengaji..."
    val warnaStatus = if (status == TimerService.TimerStatus.FINISHED) Color(0xFFFFD700) else Color.White

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. HEADER (LABEL FOCUS MODE) - Pojok Kanan Atas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, end = 24.dp) // Jarak dari atas & kanan
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.End // Rata Kanan
        ) {
            Text(
                text = "FOCUS MODE",
                style = TextStyle(
                    color = Color(0xFFFFD700), // Emas
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // 2. CENTER CONTENT (JAM & LINGKARAN)
        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            // Lingkaran Aura
            Canvas(modifier = Modifier.size(280.dp)) { // Ukuran sedikit disesuaikan
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = waktuFormat,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 48.sp, // DIPERKECIL dari 56.sp agar lebih pas
                        fontWeight = FontWeight.Medium,
                        color = warnaStatus,
                        shadow = Shadow(color = warnaStatus, blurRadius = 20f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pesanStatus,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
        }

        // 3. FOOTER (TOMBOL BATALKAN)
        Button(
            onClick = onStop,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (status == TimerService.TimerStatus.FINISHED) Color(0xFFFFD700) else Color(0xFFD32F2F),
                contentColor = if (status == TimerService.TimerStatus.FINISHED) Color.Black else Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(if (status == TimerService.TimerStatus.FINISHED) Icons.Default.Check else Icons.Default.Stop, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (status == TimerService.TimerStatus.FINISHED) "SELESAI & INPUT" else "BATALKAN",
                fontWeight = FontWeight.Bold
            )
        }
    }
}