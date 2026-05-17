package com.madhum.marga.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.madhum.marga.R
import com.madhum.marga.ui.main.MainActivity

/**
 * AlertNotificationHelper — Sends push notifications for Intervention Alerts.
 * FR-12: Push notifications for Intervention Alerts.
 * FR-05: Intervention Alert — trigger notification on "Low Activity" log.
 */
object AlertNotificationHelper {

    private const val CHANNEL_ID = "madhu_marga_alerts"
    private const val CHANNEL_NAME = "Madhu Marga Alerts"
    private const val CHANNEL_DESC = "Hive intervention and harvest alerts from Madhu Marga"
    private const val NOTIFICATION_ID_BASE = 1000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#F59E0B")
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun sendInterventionAlert(context: Context, hiveName: String, message: String, notifId: Int = NOTIFICATION_ID_BASE) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_bee)
            .setContentTitle("🚨 $hiveName Needs Attention!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(android.graphics.Color.parseColor("#F59E0B"))
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notifId, notification)
        } catch (e: SecurityException) {
            // Permission not granted — notification will be shown in-app only
        }
    }
}
