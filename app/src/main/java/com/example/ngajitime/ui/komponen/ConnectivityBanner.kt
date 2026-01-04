package com.example.ngajitime.ui.komponen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ConnectivityBanner(isOnline: Boolean) {
    // State untuk menampilkan pesan "Kembali Online" sebentar saja lalu hilang
    var showOnlineMessage by remember { mutableStateOf(false) }

    // Efek: Kalau status berubah jadi Online, tampilkan banner hijau 3 detik
    LaunchedEffect(isOnline) {
        if (isOnline) {
            showOnlineMessage = true
            delay(3000) // Tampil selama 3 detik
            showOnlineMessage = false
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        // 1. BANNER MERAH (OFFLINE) - Muncul terus selama mati
        AnimatedVisibility(
            visible = !isOnline,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD32F2F)) // Merah Gelap
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Mode Offline. Data disimpan di HP.",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. BANNER HIJAU (ONLINE) - Muncul sebentar saat nyala lagi
        AnimatedVisibility(
            visible = isOnline && showOnlineMessage,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF388E3C)) // Hijau Sukses
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Terhubung kembali. Sinkronisasi berjalan.",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}