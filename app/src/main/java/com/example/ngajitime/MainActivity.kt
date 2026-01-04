package com.example.ngajitime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.ngajitime.util.NetworkObserver
import com.example.ngajitime.ui.komponen.ConnectivityBanner

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: NgajiRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val networkObserver = NetworkObserver(applicationContext)

        setContent {
            NgajiTimeTheme {
                val statusInternet by networkObserver.observe().collectAsState(initial = true)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        ConnectivityBanner(isOnline = statusInternet)
                        Box(modifier = Modifier.weight(1f)) {

                            var startScreen by remember { mutableStateOf("") }

                            LaunchedEffect(Unit) {
                                val user = repository.getUserTarget().firstOrNull()
                                if (user != null) {
                                    startScreen = Rute.BERANDA
                                } else {
                                    startScreen = Rute.LOGIN
                                }
                            }

                            if (startScreen.isNotEmpty()) {
                                NgajiNavGraph(startDestination = startScreen)
                            } else {
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
    }
}