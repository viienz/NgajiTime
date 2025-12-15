package com.example.ngajitime.ui.layar.sesi

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LayarTimer(
    onSelesai: (Long) -> Unit = {},
    onBatal: () -> Unit = {}
) {
    // --- STATE LOGIC ---
    var detikBerjalan by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(true) }

    // Logic Timer Sederhana
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            detikBerjalan++
        }
    }

    // Format Waktu
    val jam = detikBerjalan / 3600
    val menit = (detikBerjalan % 3600) / 60
    val detik = detikBerjalan % 60
    val waktuFormat = String.format("%02d : %02d : %02d", jam, menit, detik)

    // --- ANIMASI BREATHING (BERNAPAS) ---
    val infiniteTransition = rememberInfiniteTransition(label = "Napas")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f, // Membesar 10%
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing), // 2 detik per napas
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    // --- DESAIN UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Gradient Radial: Terang di tengah, gelap di pinggir (Fokus Mata)
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2E7D32), // Hijau Daun (Tengah)
                        Color(0xFF1B5E20), // Hijau Gelap (Pinggir)
                        Color(0xFF003300)  // Hitam Kehijauan (Pojok)
                    ),
                    center = Offset.Unspecified,
                    radius = 1200f
                )
            )
    ) {
        // Opsional: Jika nanti ada Image Pattern, uncomment baris ini
        /*
        Image(
            painter = painterResource(id = R.drawable.bg_pattern_overlay),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.05f)
        )
        */

        // 1. HEADER (TOMBOL BATAL)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tombol X Kecil Transparan
            IconButton(
                onClick = onBatal,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Batal", tint = Color.White)
            }

            // Label Status
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

        // 2. CENTERPIECE (JAM UTAMA)
        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            // Lingkaran "Aura" (Animasi Bernapas)
            Canvas(modifier = Modifier.size(320.dp)) {
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = alphaAnim), // Emas pudar
                    radius = size.minDimension / 2 * scaleAnim,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Lingkaran Aura Kedua (Lebih kecil)
            Canvas(modifier = Modifier.size(280.dp)) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = size.minDimension / 2
                )
            }

            // Teks Jam Digital
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = waktuFormat,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace, // Agar angka tidak goyang
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Thin,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color(0xFFFFD700),
                            blurRadius = 20f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sedang Mengaji...",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }

        // 3. FOOTER (TOMBOL SELESAI BESAR)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\"Bacalah dengan nama Tuhanmu.\"",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Utama
            Button(
                onClick = {
                    isRunning = false
                    onSelesai(detikBerjalan)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700), // Emas Mewah
                    contentColor = Color(0xFF1B5E20)    // Teks Hijau Gelap
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "SELESAI SESI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewTimerEstetik() {
    LayarTimer()
}