package com.quoteday.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.quoteday.app.R

object NotificationHelper {
    const val CHANNEL_ID = "daily_quote_channel"
    private const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Daily Quote",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Your daily motivational quote"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showQuoteNotification(context: Context, quote: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val appIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setLargeIcon(appIcon)
            .setContentTitle("Quote of the Day")
            .setContentText(quote)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }
}
