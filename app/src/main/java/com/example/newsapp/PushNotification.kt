package com.example.newsapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.example.newsapp.ui.theme.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotification : FirebaseMessagingService() {

    private val channel_id = "push News"
    private val notificationId = 1

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.data.let {
            if (!it.isNullOrEmpty()){
                Log.d("Z900", it.toString())
            }
            Log.d("Z900", "No data found")
        }
        message.notification?.let {
            notifyUser(it.title ?: "", it.body ?: "")
        }
    }

    private fun notifyUser(title: String, body: String?) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 100,
            intent, PendingIntent.FLAG_IMMUTABLE
        )

        /**
        * if user is using android version above oreo the we have to create
         * channel to enable user to manage notification.
        * **/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = Notification.Builder(this, channel_id)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .setContentText(title)
                .setSubText(body)
                .setAutoCancel(true)
                .build()

            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channel_id, "Custom Channel",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        } else {
            notification = Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .setContentText(title)
                .setSubText(body)
                .setAutoCancel(true)
                .build()
        }
        notificationManager.notify(notificationId, notification)
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}