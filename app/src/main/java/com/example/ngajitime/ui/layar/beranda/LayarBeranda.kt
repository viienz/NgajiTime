package com.example.ngajitime.ui.layar.beranda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Import ini penting buat list notifikasi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ngajitime.R
import com.example.ngajitime.ui.komponen.MenuAktif
import com.example.ngajitime.ui.komponen.NgajiBottomBar
import java.text.SimpleDateFormat
import java.util.Calendar

// --- WARNA ---
val HijauStreak = Color(0xFFAEEA5C)
val HijauTombol = Color(0xFF4CAF50)
val BiruMudaBg = Color(0xFFF0F4F8)
val GradienBiru1 = Color(0xFF42A5F5)
val GradienBiru2 = Color(0xFF1976D2)

@OptIn(ExperimentalMaterial3Api::class) // Buat ModalBottomSheet
@Composable
fun LayarBeranda(
    viewModel: BerandaViewModel = hiltViewModel(),
    onKeSurah: () -> Unit,
    onKeStats: () -> Unit,
    onKeProfil: () -> Unit,
    onKeTimer: () -> Unit
) {
    // 1. Ambil data state
    val user by viewModel.userTarget.collectAsState()
    val lastReadData by viewModel.lastRead.collectAsState()
    val halamanHariIni by viewModel.halamanHariIni.collectAsState()
    val listKalender by viewModel.listKalender.collectAsState()

    // 2. Data Notifikasi (REALTIME DARI ROOM)
    val listNotifikasi by viewModel.listNotifikasi.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    // State buat Bottom Sheet
    var showNotificationSheet by remember { mutableStateOf(false) }

    // 3. Hitung Progress & Waktu
    val targetAyat = user?.targetAyatHarian ?: 1
    val ayatHariIni = halamanHariIni
    val progressPersen = ((ayatHariIni.toFloat() / targetAyat.toFloat()) * 100).coerceIn(0f, 100f)
    val isTargetReached = ayatHariIni >= targetAyat

    val calendar = remember { Calendar.getInstance() }
    val jamSekarang = calendar.get(Calendar.HOUR_OF_DAY)
    val teksWaktuSholat = getInfoWaktu(jamSekarang)

    // 4. Data UI String
    val namaUser = user?.namaUser ?: "Sobat Ngaji"
    val streakCount = user?.currentStreak ?: 0
    val lastReadSurah = lastReadData?.namaSurah ?: "Belum ada bacaan"
    val lastReadAyat = lastReadData?.ayatTerakhirDibaca ?: 0
    val lastReadInfo = if (lastReadData != null && lastReadAyat > 0) "Ayat $lastReadAyat" else "(Lanjutkan progressmu)"

    // --- BOTTOM SHEET NOTIFIKASI ---
    if (showNotificationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationSheet = false },
            containerColor = Color(0xFFF5F5F5)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header Sheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifikasi",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Tombol Tes Manual (Buat Demo) - Bisa dihapus nanti
                    TextButton(onClick = { viewModel.testAddNotification() }) {
                        Text("Tes (+1)", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (listNotifikasi.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada notifikasi", color = Color.Gray)
                    }
                } else {
                    LazyColumn {
                        items(listNotifikasi) { notif ->
                            // Tentukan Ikon & Warna berdasarkan Tipe
                            val (icon, color) = when (notif.tipe) {
                                "ALERT" -> Icons.Default.Warning to Color(0xFFD32F2F) // Merah
                                "REWARD" -> Icons.Rounded.EmojiEvents to Color(0xFFFFD700) // Emas
                                else -> Icons.Default.Notifications to Color(0xFF1976D2) // Biru
                            }

                            NotificationItem(
                                judul = notif.judul,
                                pesan = notif.pesan,
                                icon = icon,
                                warnaIcon = color,
                                isRead = notif.isRead
                            )
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onKeTimer,
                containerColor = HijauTombol,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Mulai Timer", modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            NgajiBottomBar(
                menuAktif = MenuAktif.BERANDA,
                onKeBeranda = {},
                onKeSurah = onKeSurah,
                onKeStats = onKeStats,
                onKeProfil = onKeProfil
            )
        },
        containerColor = BiruMudaBg
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                HeaderSection(
                    namaUser = namaUser,
                    infoWaktu = teksWaktuSholat,
                    unreadCount = unreadCount, // Kirim jumlah notif belum dibaca
                    onLoncengKlik = {
                        showNotificationSheet = true
                        viewModel.tandaiSudahDibaca() // Reset badge jadi 0
                    }
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))

                    KartuTargetBesar(
                        ayatDibaca = ayatHariIni,
                        targetAyat = targetAyat,
                        progress = progressPersen / 100f,
                        isReached = isTargetReached
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        KartuStreakKecil(
                            streak = streakCount,
                            modifier = Modifier.weight(1f)
                        )

                        StatusBadgeCard(
                            totalAyat = user?.totalAyatDibaca ?: 0,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    LastReadCard(surah = lastReadSurah, ayatInfo = lastReadInfo)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onKeSurah,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HijauTombol),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Mulai Mengaji", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    KalenderMingguanSection(listData = listKalender)
                }
            }
        }
    }
}

// --- KOMPONEN ITEM NOTIFIKASI ---
@Composable
fun NotificationItem(
    judul: String,
    pesan: String,
    icon: ImageVector,
    warnaIcon: Color,
    isRead: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(if (isRead) Color.White else Color(0xFFE3F2FD), RoundedCornerShape(12.dp)) // Biru tipis kalau belum dibaca
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(warnaIcon.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = warnaIcon, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = judul, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = pesan, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
        }
    }
}

@Composable
fun HeaderSection(
    namaUser: String,
    infoWaktu: String,
    unreadCount: Int, // Terima parameter jumlah
    onLoncengKlik: () -> Unit // Terima aksi klik
) {
    val calendar = Calendar.getInstance()
    val jam = calendar.get(Calendar.HOUR_OF_DAY)
    val menit = calendar.get(Calendar.MINUTE)
    val jamString = String.format("%02d : %02d", jam, menit)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg_header_awan),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier.padding(20.dp).fillMaxSize()
            ) {
                // Bagian Atas: Salam & Notif
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Assalamualaikum,", color = Color.White, fontSize = 14.sp)
                        Text(namaUser, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    // --- ICON LONCENG (UPDATE) ---
                    IconButton(onClick = onLoncengKlik) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }

                            // TITIK MERAH (BADGE)
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                        .border(2.dp, Color.White.copy(alpha=0.2f), CircleShape) // Border halus
                                )
                            }
                        }
                    }
                }

                // Bagian Tengah: Jam & Kapsul
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = jamString,
                            color = Color.White,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    blurRadius = 15f
                                )
                            )
                        )

                        Surface(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (infoWaktu.isNotEmpty()) infoWaktu else "Waktu Ngaji",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraLight,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LastReadCard(surah: String, ayatInfo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_last_read_book),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text("Terakhir dibaca", fontWeight = FontWeight.Normal, fontSize = 12.sp, color = Color.Gray)

                Text(
                    text = surah,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Text(
                    text = ayatInfo,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
@Composable
fun KartuTargetBesar(ayatDibaca: Int, targetAyat: Int, progress: Float, isReached: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(colors = listOf(GradienBiru1, GradienBiru2)))
                .padding(24.dp)
        ) {
            Column {
                Text("Target Harian Kamu", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$ayatDibaca / $targetAyat", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ayat", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(bottom = 6.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${(progress * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFFFFEB3B),
                    trackColor = Color.White.copy(alpha = 0.3f),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (isReached) Icons.Rounded.EmojiEvents else Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isReached) "Hebat! Target tercapai 🎉" else "Semangat kejar targetmu!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun KartuStreakKecil(streak: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(140.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = HijauStreak)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Rounded.LocalFireDepartment, contentDescription = "Streak", tint = Color(0xFFFF5722), modifier = Modifier.size(28.dp))
            Text(streak.toString(), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF33691E))
            Text("Day Streak", fontSize = 12.sp, color = Color(0xFF558B2F), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatusBadgeCard(totalAyat: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(140.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFFE3F2FD), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.MenuBook, contentDescription = "Total Jejak", tint = Color(0xFF1976D2), modifier = Modifier.size(24.dp))
            }
            Text("$totalAyat", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, color = Color(0xFF263238))
            Text("Total Ayat", fontSize = 12.sp, color = Color(0xFF78909C), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun KalenderMingguanSection(listData: List<KalenderItemData>) {
    val titleBulan = remember {
        val fmt = SimpleDateFormat("MMMM yyyy", java.util.Locale("id", "ID"))
        fmt.format(java.util.Date())
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(titleBulan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text("Minggu Ini", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listData.forEach { item ->
                KalenderItem(item.hari, item.tanggal, item.isAdaSesi, item.isHariIni)
            }
            if (listData.isEmpty()) { Text("Memuat kalender...", fontSize = 12.sp, color = Color.Gray) }
        }
    }
}

@Composable
fun KalenderItem(hari: String, tanggal: String, isActive: Boolean, isHariIni: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (isActive) HijauStreak else Color.Transparent).padding(vertical = 8.dp, horizontal = 12.dp)) {
        Text(hari, fontSize = 12.sp, color = if (isHariIni || isActive) Color(0xFF263238) else Color.Gray, fontWeight = if (isHariIni) FontWeight.Bold else FontWeight.Normal)
        Spacer(modifier = Modifier.height(4.dp))
        Text(tanggal, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isActive || isHariIni) Color(0xFF263238) else Color.Black)
    }
}

fun getInfoWaktu(jam: Int): String {
    return when (jam) {
        in 3..4 -> "Waktu Tahajud"
        in 5..5 -> "Waktu Subuh"
        in 6..6 -> "Waktu Syuruq"
        in 7..10 -> "Waktu Dhuha"
        in 11..14 -> "Waktu Dzuhur"
        in 15..17 -> "Waktu Ashar"
        in 18..18 -> "Waktu Maghrib"
        in 19..23 -> "Waktu Isya"
        else -> "Qiyamul Lail"
    }
}
