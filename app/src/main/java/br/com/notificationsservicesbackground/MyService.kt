package br.com.notificationsservicesbackground

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MyService : Service (){
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "notification_example")
            .setContentTitle("Service run")
            .setContentText("The service is running in the background.")
            .setSmallIcon(R.drawable.ic_notifications)
            .build()

        startForeground(1, notification)

        Thread {
            for (i in 1..10) {
                Log.d("Service", "Cont: $i")
                Thread.sleep(1000)
            }
            stopSelf()
        }.start()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}