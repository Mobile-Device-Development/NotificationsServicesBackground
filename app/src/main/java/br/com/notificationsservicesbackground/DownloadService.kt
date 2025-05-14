package br.com.notificationsservicesbackground

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class DownloadService : Service() {
    private val channelId = "download_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Downloading file...")
            .setContentText("Progress: 0%")
            .setSmallIcon(R.drawable.ic_download)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // Use the appropriate foreground service type for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, notification)
        }

        Thread {
            for (i in 1..100 step 10) {
                Thread.sleep(1000)
                val updated = NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Downloading file...")
                    .setContentText("Progress: $i%")
                    .setSmallIcon(R.drawable.ic_download)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()

                if (checkNotificationPermission()) {
                    NotificationManagerCompat.from(this).notify(1, updated)
                }
            }

            stopForeground(true)
            stopSelf()
        }.start()

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Download Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // For Android 12 and below
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}