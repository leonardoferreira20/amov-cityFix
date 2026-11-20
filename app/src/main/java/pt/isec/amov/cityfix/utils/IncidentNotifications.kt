package pt.isec.amov.cityfix.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import pt.isec.amov.cityfix.MainActivity
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.GeoLocation

object IncidentNotifications {
    const val EXTRA_INCIDENT_ID = "incidentId"
    const val EXTRA_IMPACT_LATITUDE = "impactLatitude"
    const val EXTRA_IMPACT_LONGITUDE = "impactLongitude"
    private const val CHANNEL_ID = "incident_updates"
    private const val IMPACT_CHANNEL_ID = "impact_suggestions"
    private const val IMPACT_NOTIFICATION_ID = 2001

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = context.getString(R.string.notification_channel_description) }
        )
        manager.createNotificationChannel(
            NotificationChannel(
                IMPACT_CHANNEL_ID,
                context.getString(R.string.impact_suggestion_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = context.getString(R.string.impact_suggestion_channel_description) }
        )
    }

    fun show(context: Context, incidentId: String, title: String, text: String) {
        val intent = mainActivityIntent(context).putExtra(EXTRA_INCIDENT_ID, incidentId)
        notify(context, CHANNEL_ID, incidentId.hashCode(), intent, title, text)
    }

    fun showImpactSuggestion(context: Context, location: GeoLocation) {
        val intent = mainActivityIntent(context)
            .putExtra(EXTRA_IMPACT_LATITUDE, location.latitude)
            .putExtra(EXTRA_IMPACT_LONGITUDE, location.longitude)
        notify(
            context,
            IMPACT_CHANNEL_ID,
            IMPACT_NOTIFICATION_ID,
            intent,
            context.getString(R.string.impact_suggestion_title),
            context.getString(R.string.impact_suggestion_text)
        )
    }

    private fun mainActivityIntent(context: Context) = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun notify(
        context: Context,
        channelId: String,
        notificationId: Int,
        intent: Intent,
        title: String,
        text: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
