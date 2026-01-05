package com.example.ngajitime.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.ngajitime.MainActivity
import com.example.ngajitime.R
import com.example.ngajitime.data.local.dao.NotifikasiDao
import com.example.ngajitime.data.local.entity.NotifikasiEntity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Kita inject DAO langsung, bukan Database utuh (lebih rapi)
    @Inject
    lateinit var notifikasiDao: NotifikasiDao

    // Fungsi ini jalan otomatis saat ada notifikasi masuk dari Firebase Cloud
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 1. Ambil data pesan
        val judul = remoteMessage.notification?.title ?: "Info NgajiYuk"
        val pesan = remoteMessage.notification?.body ?: "Cek aplikasimu sekarang."

        // Tentukan tipe ikon berdasarkan isi pesan (Logika sederhana)
        val tipe = if (judul.contains("Target", true)) "ALERT"
        else if (judul.contains("Selamat", true)) "REWARD"
        else "INFO"

        // 2. SIMPAN KE DATABASE (Agar muncul di Lonceng UI)
        // Kita pakai IO Dispatcher karena akses database itu berat
        CoroutineScope(Dispatchers.IO).launch {
            notifikasiDao.insertNotifikasi(
                NotifikasiEntity(
                    judul = judul,
                    pesan = pesan,
                    tipe = tipe,
                    isRead = false // Masuk sebagai belum dibaca
                )
            )
        }

        // 3. TAMPILKAN POP-UP DI HP (System Notification)
        tampilkanNotifikasiDiHP(judul, pesan)
    }

    // Fungsi tambahan untuk memunculkan notifikasi di bar atas Android
    private fun tampilkanNotifikasiDiHP(judul: String, pesan: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "channel_ngajiyuk_utama"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Pastikan icon ini ada, atau ganti R.drawable.ic_...
            .setContentTitle(judul)
            .setContentText(pesan)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Setup Channel untuk Android Oreo ke atas (Wajib)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifikasi Utama",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}