package com.focussystems.volumecontrolviasms

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi

class MyForegroundService : Service() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {
        val channelId = "ForegroundServiceChannel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Volume Control via SMS")
            .setContentText("Listening for SMS commands to adjust volume.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        // 1 = FOREGROUND_SERVICE_TYPE_DATA_SYNC
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Implement your background tasks here
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
