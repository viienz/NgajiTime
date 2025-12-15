package com.example.ngajitime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ngajitime.data.repository.NgajiRepository
import com.example.ngajitime.ui.navigasi.NgajiNavGraph
import com.example.ngajitime.ui.navigasi.Rute
import com.example.ngajitime.ui.theme.NgajiTimeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: NgajiRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NgajiTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // State untuk menyimpan mau ke mana (Login atau Beranda?)
                    // Awalnya kosong ("") karena kita belum tahu
                    var startScreen by remember { mutableStateOf("") }

                    // Cek Database di Background (Aman, tidak bikin crash)
                    LaunchedEffect(Unit) {
                        val user = repository.getUserTarget().firstOrNull()
                        if (user != null) {
                            startScreen = Rute.BERANDA
                        } else {
                            startScreen = Rute.LOGIN
                        }
                    }

                    // Logika Tampilan:
                    if (startScreen.isNotEmpty()) {
                        // 1. Kalau sudah tahu tujuannya -> Buka Navigasi
                        NgajiNavGraph(startDestination = startScreen)
                    } else {
                        // 2. Kalau belum tahu (lagi loading) -> Tampilkan Loading Putar
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}