package com.example.ngajitime.ui.layar.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.R
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import com.example.ngajitime.util.GoogleAuthClient

@Composable
fun LayarLogin(
    viewModel: LoginViewModel = hiltViewModel(),
    onMasuk: () -> Unit,       // Callback saat User Baru selesai setup (Step 5)
    onLoginSuccess: () -> Unit // Callback saat User Lama terdeteksi (Step 99 -> Home)
) {
    // Ambil state step dari ViewModel
    val currentStep by viewModel.step.collectAsState()

    // --- 1. TEKNIK BACKGROUND GRADIENT (Agar Tidak Flat) ---
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE8F5E9), // Hijau sangat muda (Top)
            Color(0xFFFFFFFF), // Putih (Tengah)
            Color(0xFFFFFFFF)  // Putih (Bawah)
        )
    )

    // --- 2. LOGIKA PINDAH HALAMAN OTOMATIS (SINKRONISASI) ---
    // Jika Step berubah jadi 99 (Kode User Lama), langsung pindah ke Home
    LaunchedEffect(currentStep) {
        if (currentStep == 99) {
            onLoginSuccess()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            // Tampilkan Navigasi Bawah HANYA jika bukan sedang Loading (Step 99)
            if (currentStep < 99) {
                NavigasiBawah(currentStep = currentStep, onBack = { viewModel.prevStep() })
            }
        }
    ) { padding ->
        // Box ini menjadi wadah background gradasi
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(padding)
        ) {
            // Logika Tampilan Utama
            if (currentStep == 99) {
                // TAMPILAN LOADING (Saat Cek Data User Lama)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                }
            } else {
                // TAMPILAN FORM WIZARD (User Baru)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    when (currentStep) {
                        1 -> StepSatuIntro(viewModel)
                        2 -> StepDuaFrekuensi(viewModel)
                        3 -> StepTigaLevel(viewModel)
                        4 -> StepEmpatTujuan(viewModel)
                        5 -> StepLimaTarget(viewModel, onMasuk)
                    }
                }
            }
        }
    }
}

// --- KOMPONEN NAVIGASI BAWAH ---
@Composable
fun NavigasiBawah(currentStep: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tombol Back (Hanya muncul jika bukan step 1)
        if (currentStep > 1) {
            TextButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kembali", color = Color.Gray)
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp)) // Spacer kosong biar layout seimbang
        }

        // Indikator Halaman (Contoh: 1/5)
        Text(
            text = "Langkah $currentStep dari 5",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E7D32), // Hijau NgajiTime
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(48.dp)) // Penyeimbang kanan
    }
}

// ==========================================
// ISI HALAMAN (STEP 1 - 5)
// ==========================================

// --- LANGKAH 1: PERKENALAN & LOGIN ---
@Composable
fun StepSatuIntro(viewModel: LoginViewModel) {
    var namaInput by remember { mutableStateOf(viewModel.namaUser) }

    // Persiapan Alat Login Google
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Inisialisasi GoogleAuthClient
    val googleAuthClient = remember { GoogleAuthClient(context) }

    // Launcher: Ini yang menangani hasil setelah user memilih akun Google
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            scope.launch {
                val signInResult = googleAuthClient.signInWithIntent(result.data ?: return@launch)
                viewModel.onSignInResult(signInResult) // Kirim hasil ke ViewModel
            }
        }
    }

    Column {
        Text("Assalamualaikum,", fontSize = 20.sp, color = Color.Gray)
        Text("Selamat Datang!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Mari mulai perjalanan hijrahmu dengan kebiasaan baik.", color = Color.Gray)

        Spacer(modifier = Modifier.height(40.dp))

        // Input Nama Manual
        Text("Siapa nama panggilanmu?", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = namaInput,
            onValueChange = { namaInput = it; viewModel.namaUser = it },
            placeholder = { Text("Contoh: Fulan") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF2E7D32)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Lanjut (Manual)
        Button(
            onClick = { viewModel.nextStep() },
            enabled = namaInput.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Lanjut sebagai Tamu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pembatas "ATAU"
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(" ATAU ", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TOMBOL GOOGLE (SUDAH AKTIF SEKARANG! 🚀) ---
        OutlinedButton(
            onClick = {
                scope.launch {
                    // 1. Mulai proses login, dapatkan IntentSender
                    val signInIntentSender = googleAuthClient.signIn()

                    // 2. Luncurkan Popup Pilih Akun
                    if (signInIntentSender != null) {
                        launcher.launch(
                            IntentSenderRequest.Builder(signInIntentSender).build()
                        )
                    } else {
                        Toast.makeText(context, "Gagal memulai Login Google", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google), // Pastikan punya icon ini atau ganti icon lain
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))
            Text("Masuk dengan Google", fontWeight = FontWeight.Bold)
        }
    }
}

// --- LANGKAH 2: FREKUENSI ---
@Composable
fun StepDuaFrekuensi(viewModel: LoginViewModel) {
    val opsi = listOf("Setiap Hari (Istiqomah)", "Beberapa kali seminggu", "Hanya saat Ramadan/Acara", "Sudah lama tidak ngaji")
    // Ambil data lama kalau ada, biar pilihan tidak hilang saat tombol Back ditekan
    var selected by remember { mutableStateOf(viewModel.frekuensi) }

    Column {
        // Konten Pilihan
        ContentPilihan(
            judul = "Seberapa sering biasanya kamu mengaji?",
            deskripsi = "Pilih satu yang paling sesuai kondisi saat ini.",
            opsiList = opsi,
            selectedItem = selected,
            onSelect = { jawaban ->
                selected = jawaban // Cuma ganti warna hijau, JANGAN pindah halaman dulu
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Lanjut Manual
        Button(
            onClick = {
                viewModel.frekuensi = selected // Simpan ke ViewModel
                viewModel.nextStep() // Baru pindah halaman
            },
            enabled = selected.isNotEmpty(), // Tombol mati kalau belum pilih
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Lanjut", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// --- LANGKAH 3: LEVEL BACA ---
@Composable
fun StepTigaLevel(viewModel: LoginViewModel) {
    val opsi = listOf("PEMULA", "LANCAR", "MAHIR")
    val labelOpsi = listOf("Masih terbata-bata / Belajar", "Lancar tapi santai (Tartil)", "Sangat lancar / Cepat (Hadr)")
    var selected by remember { mutableStateOf(viewModel.levelBaca) }

    Column {
        Text("Bagaimana kelancaran bacaanmu?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        Spacer(modifier = Modifier.height(24.dp))

        // List Pilihan
        opsi.forEachIndexed { index, value ->
            PilihanItem(
                text = labelOpsi[index],
                isSelected = selected == value,
                onClick = { selected = value } // Cuma update tampilan
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Lanjut
        Button(
            onClick = {
                viewModel.levelBaca = selected
                viewModel.nextStep()
            },
            // Tidak perlu enabled check karena defaultnya "LANCAR" (pasti ada isi)
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Lanjut", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// --- LANGKAH 4: TUJUAN ---
@Composable
fun StepEmpatTujuan(viewModel: LoginViewModel) {
    val opsi = listOf("Membangun Kebiasaan Rutin", "Khatam Al-Qur'an Secepatnya", "Mencari Ketenangan Hati", "Memperbaiki Bacaan")
    var selected by remember { mutableStateOf(viewModel.tujuan) }

    Column {
        ContentPilihan(
            judul = "Apa tujuan utamamu saat ini?",
            deskripsi = "Kami akan bantu kamu capai target ini.",
            opsiList = opsi,
            selectedItem = selected,
            onSelect = { selected = it } // Cuma ganti warna hijau
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Lanjut
        Button(
            onClick = {
                viewModel.tujuan = selected
                viewModel.nextStep()
            },
            enabled = selected.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Lanjut", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// --- LANGKAH 5: TARGET ---
@Composable
fun StepLimaTarget(viewModel: LoginViewModel, onSelesai: () -> Unit) {
    var mode by remember { mutableStateOf("WAKTU") }
    var inputVal by remember { mutableStateOf("15") }

    Column {
        Text("Terakhir! Ayo buat komitmen.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        Text("Pilih gaya target yang cocok buat kamu.", color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // TAB MODE (Lebih cantik)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                TabButton("Santai (Waktu)", mode == "WAKTU", Modifier.weight(1f)) { mode = "WAKTU"; inputVal = "15" }
                TabButton("Khatam (Hari)", mode == "DEADLINE", Modifier.weight(1f)) { mode = "DEADLINE"; inputVal = "30" }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(if (mode == "WAKTU") "Berapa menit kamu bisa luangkan per hari?" else "Dalam berapa hari kamu ingin khatam?", fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = inputVal,
            onValueChange = { if(it.all { c -> c.isDigit() }) inputVal = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF2E7D32)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.modeTarget = mode
                viewModel.nilaiTarget = inputVal.toIntOrNull() ?: 15
                viewModel.simpanDataUser(onSelesai)
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Bismillah, Mulai Sekarang!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// ==========================================
// KOMPONEN PENDUKUNG (STYLING)
// ==========================================

@Composable
fun ContentPilihan(judul: String, deskripsi: String, opsiList: List<String>, selectedItem: String, onSelect: (String) -> Unit) {
    Column {
        Text(judul, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        Text(deskripsi, color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))

        opsiList.forEach { text ->
            PilihanItem(text, text == selectedItem) { onSelect(text) }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PilihanItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    // KARTU YANG LEBIH TIMBUL (ELEVATION)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(isSelected) Color(0xFFE8F5E9) else Color.White // Hijau muda jika dipilih
        ),
        // Tambahkan border hijau jika dipilih
        border = if(isSelected) BorderStroke(2.dp, Color(0xFF2E7D32)) else BorderStroke(0.dp, Color.Transparent),
        // Efek bayangan agar tidak flat
        elevation = CardDefaults.cardElevation(defaultElevation = if(isSelected) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text,
                modifier = Modifier.weight(1f),
                fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if(isSelected) Color(0xFF2E7D32) else Color.DarkGray
            )
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2E7D32) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = null
    ) {
        Text(text)
    }
}