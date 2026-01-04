package com.example.ngajitime.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Fungsi ini akan memancarkan (emit) status koneksi secara real-time
    fun observe(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true) // Kirim "TRUE" kalau ada internet
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false) // Kirim "FALSE" kalau internet putus
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Cek status awal saat pertama kali buka
        val currentNetwork = connectivityManager.activeNetwork
        val isConnected = currentNetwork != null &&
                connectivityManager.getNetworkCapabilities(currentNetwork)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        trySend(isConnected)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}