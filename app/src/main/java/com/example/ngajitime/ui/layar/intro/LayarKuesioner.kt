/*
 * ==========================================
 * NIM  : [23523147]
 * Nama : [Devin Pandya Subarkah]
 * ==========================================
 */

package com.example.ngajitime.ui.layar.intro

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ngajitime.R

data class DataPertanyaan(
    val id: Int,
    val teksPertanyaan: String,
    val opsiJawaban: List<OpsiJawaban>
)

data class OpsiJawaban(
    val id: Int,
    val teks: String,
    val emoji: String
)

@Composable
fun LayarKuesioner() {
    val context = LocalContext.current

    var indexPertanyaan by remember { mutableStateOf(0) }

    val petaJawaban = remember { mutableStateMapOf<Int, Int>() }

    val daftarPertanyaan = remember {
        listOf(
            DataPertanyaan(
                id = 1,
                teksPertanyaan = "Sebelum mulai, seberapa sering biasanya kamu mengaji?",
                opsiJawaban = listOf(
                    OpsiJawaban(1, "Setiap Hari (Istiqomah)", "🤲"),
                    OpsiJawaban(2, "Beberapa kali seminggu", "📆"),
                    OpsiJawaban(3, "Hanya saat Ramadhan", "🕌"),
                    OpsiJawaban(4, "Sudah lama tidak mengaji", "😅")
                )
            ),
            DataPertanyaan(
                id = 2,
                teksPertanyaan = "Apakah kamu pernah menamatkan (khatam) 30 Juz Al-Qur'an sebelumnya?",
                opsiJawaban = listOf(
                    OpsiJawaban(1, "Pernah", "🤲"),
                    OpsiJawaban(2, "Belum Pernah", "😅")
                )
            )
        )
    }

    val pertanyaanAktif = daftarPertanyaan.getOrNull(indexPertanyaan)

    val pilihanSaatIni = petaJawaban[indexPertanyaan]

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            //Kode ini membutuhkan file gambar 'bg_ngaji.png' di folder res/drawable
            painter = painterResource(id = R.drawable.bg_ngaji),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selamat datang di NgajiYuk!!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = pertanyaanAktif,
                label = "TransisiPertanyaan",
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                }
            ) { data ->
                if (data != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = data.teksPertanyaan,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        data.opsiJawaban.forEach { opsi ->
                            KartuPilihan(
                                teks = opsi.teks,
                                emoji = opsi.emoji,
                                dipilih = (pilihanSaatIni == opsi.id),
                                padaSaatDiklik = {
                                    petaJawaban[indexPertanyaan] = opsi.id
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                } else {
                    Text("Setup Selesai! Mengalihkan...", color = Color.White)
                }
            }
        }

        Button(
            onClick = {
                if (indexPertanyaan < daftarPertanyaan.size - 1) {
                    indexPertanyaan++
                } else {
                    Toast.makeText(context, "Masuk ke Dashboard...", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .height(56.dp),
            enabled = pilihanSaatIni != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF2E7D32)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (indexPertanyaan < daftarPertanyaan.size - 1) "Lanjut" else "Selesai",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun KartuPilihan(
    teks: String,
    emoji: String,
    dipilih: Boolean,
    padaSaatDiklik: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = if (dipilih) 3.dp else 0.dp,
                color = if (dipilih) Color(0xFFFFD700) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { padaSaatDiklik() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = teks,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontWeight = if (dipilih) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = emoji)
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                .background(if (dipilih) Color(0xFF4CAF50) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (dipilih) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Terpilih",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLayarKuesioner() {
    LayarKuesioner()
}