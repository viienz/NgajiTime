package com.example.ngajitime.ui.layar.profil

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.ui.komponen.MenuAktif
import com.example.ngajitime.ui.komponen.NgajiBottomBar

@Composable
fun LayarProfil(
    viewModel: ProfilViewModel = hiltViewModel(),
    onKeBeranda: () -> Unit,
    onKeSurah: () -> Unit,
    onKeStats: () -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.userTarget.collectAsState()
    var showDialogTarget by remember { mutableStateOf(false) }
    var showDialogNama by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NgajiBottomBar(
                menuAktif = MenuAktif.PROFIL,
                onKeBeranda = onKeBeranda,
                onKeSurah = onKeSurah,
                onKeStats = onKeStats,
                onKeProfil = {}
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //HEADER
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(60.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showDialogNama = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = user?.namaUser ?: "Sobat Ngaji",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Nama",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = if (user?.email != null) "${user?.email}" else "Mode Tamu (Offline)",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("Pengaturan Ngaji", modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))

            Card(
                modifier = Modifier.fillMaxWidth().clickable { showDialogTarget = true },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val modeTeks = if (user?.modeTarget == "WAKTU") "Mode Santai" else "Mode Khatam"
                        val detailTeks = if (user?.modeTarget == "WAKTU") "${user?.waktuLuangMenit} Menit / hari" else "Target ${user?.durasiTargetHari} Hari"
                        Text(text = modeTeks, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = detailTeks, color = Color.Gray, fontSize = 14.sp)
                    }
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2E7D32))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ItemSwitch(
                icon = Icons.Outlined.AcUnit,
                judul = "Mode Cuti (Streak Freeze)",
                desc = "Streak tidak hilang saat absen",
                isChecked = user?.isStreakFreeze ?: false,
                onCheckedChange = { viewModel.toggleStreakFreeze(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (user?.email == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    border = BorderStroke(1.dp, Color(0xFFFF9800)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("⚠️ Akun Belum Diamankan", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        Text("Data kamu hanya ada di HP ini.", fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = { Toast.makeText(context, "Segera Hadir!", Toast.LENGTH_SHORT).show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Hubungkan ke Google", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { viewModel.logout { onLogout() } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar (Logout)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

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

    if (showDialogNama) {
        DialogEditNama(
            namaAwal = user?.namaUser ?: "",
            onDismiss = { showDialogNama = false },
            onSimpan = { namaBaru ->
                viewModel.updateNama(namaBaru)
                showDialogNama = false
            }
        )
    }
}

@Composable
fun DialogEditNama(
    namaAwal: String,
    onDismiss: () -> Unit,
    onSimpan: (String) -> Unit
) {
    var inputNama by remember { mutableStateOf(namaAwal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ganti Nama Panggilan") },
        text = {
            OutlinedTextField(
                value = inputNama,
                onValueChange = { if (it.length <= 20) inputNama = it },
                label = { Text("Nama Kamu") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputNama.isNotBlank()) onSimpan(inputNama)
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun ItemSwitch(
    icon: ImageVector,
    judul: String,
    desc: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF03A9F4))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(judul, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(desc, fontSize = 10.sp, color = Color.Gray)
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF03A9F4), checkedTrackColor = Color(0xFFE1F5FE))
            )
        }
    }
}

@Composable
fun DialogEditTarget(currentMode: String, currentValue: Int, onDismiss: () -> Unit, onSimpan: (String, Int) -> Unit) {
    var selectedMode by remember { mutableStateOf(currentMode) }
    var inputNilai by remember { mutableStateOf(currentValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atur Target Ngaji") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedMode == "WAKTU", onClick = { selectedMode = "WAKTU" })
                    Text("Santai (Waktu Luang)")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedMode == "DEADLINE", onClick = { selectedMode = "DEADLINE" })
                    Text("Khatam (Target Hari)")
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = inputNilai,
                    onValueChange = { if (it.all { c -> c.isDigit() }) inputNilai = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = { Button(onClick = { onSimpan(selectedMode, inputNilai.toIntOrNull() ?: 0) }) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}