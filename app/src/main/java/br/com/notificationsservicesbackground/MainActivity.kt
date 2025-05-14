package br.com.notificationsservicesbackground

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private val channelId = "notification_example"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "Notification permission denied. Some notifications may not appear.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(       )
        setContentView(R.layout.activity_main)

        createChannelNotification()
        checkNotificationPermission()

        val btnStartservice = findViewById<Button>(R.id.btnStartservice)
        btnStartservice.setOnClickListener {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MyService::class.java)
            startService(intent)
        }

        val btnDownloads = findViewById<Button>(R.id.btnDownloads)
        btnDownloads.setOnClickListener {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DownloadService::class.java)
            startService(intent)
        }
    }

    private fun createChannelNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Chanel Notification"
            val descriptionText = "Description Chanel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val downloadChannelId = "download_channel"
            val downloadChannelName = "Download Channel"
            val downloadChannel = NotificationChannel(
                downloadChannelId,
                downloadChannelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(downloadChannel)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {}
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(
                        this,
                        "Notification permission is required to show progress",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

}