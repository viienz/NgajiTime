package com.example.ngajitime.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.ngajitime.MainActivity
import com.example.ngajitime.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerService : Service() {
    companion object {
        private val _waktuTersisa = MutableStateFlow(0L)
        val waktuTersisa: StateFlow<Long> = _waktuTersisa

        private val _statusTimer = MutableStateFlow(TimerStatus.IDLE)
        val statusTimer: StateFlow<TimerStatus> = _statusTimer

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_DURASI = "EXTRA_DURASI" // Detik
    }

    private var countdownTimer: CountDownTimer? = null
    private var totalDurasiAwal = 0L

    enum class TimerStatus {
        IDLE, RUNNING, FINISHED
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durasiDetik = intent.getLongExtra(EXTRA_DURASI, 0L)
                mulaiCountdown(durasiDetik)
            }
            ACTION_STOP -> {
                hentikanTimer()
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun mulaiCountdown(durasiDetik: Long) {
        hentikanTimer()
        totalDurasiAwal = durasiDetik
        _statusTimer.value = TimerStatus.RUNNING

        startForeground(1, buatNotifikasi("Fokus Mengaji Dimulai", "Waktu tersisa: ${formatWaktu(durasiDetik)}").build())

        countdownTimer = object : CountDownTimer(durasiDetik * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val detikSisa = millisUntilFinished / 1000
                _waktuTersisa.value = detikSisa
            }

            override fun onFinish() {
                _waktuTersisa.value = 0
                _statusTimer.value = TimerStatus.FINISHED
                bunyikanAlarmSelesai()
                stopForeground(STOP_FOREGROUND_DETACH)
            }
        }.start()
    }

    private fun hentikanTimer() {
        countdownTimer?.cancel()
        _statusTimer.value = TimerStatus.IDLE
    }

    private fun bunyikanAlarmSelesai() {
        try {
            val notifikasi = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notifikasi)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.notify(2, buatNotifikasi("Alhamdulillah Selesai! 🎉", "Target waktu tercapai.").build())
    }


    private fun buatNotifikasi(judul: String, isi: String): NotificationCompat.Builder {
        val channelId = "timer_channel"
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Timer Ngaji", NotificationManager.IMPORTANCE_LOW)
            notifManager.createNotificationChannel(channel)
        }


        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(judul)
            .setContentText(isi)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
    }

    private fun formatWaktu(detik: Long): String {
        val m = detik / 60
        val s = detik % 60
        return "%02d:%02d".format(m, s)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        hentikanTimer()
        super.onDestroy()
    }
}