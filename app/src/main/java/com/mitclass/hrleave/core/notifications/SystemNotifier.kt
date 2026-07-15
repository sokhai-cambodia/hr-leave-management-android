package com.mitclass.hrleave.core.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val CHANNEL_ID = "hr_leave_notifications"
private const val NOTIFICATION_ID = 1001

/**
 * Posts a local system notification when the unread count increases — the app's runtime-permission
 * demonstration (SPEC §8/§11, rubric §9). Gated behind POST_NOTIFICATIONS (API 33+); degrades
 * gracefully to badge-only if denied or on older API levels where the permission doesn't apply.
 */
@Singleton
class SystemNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notifications",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission") // guarded by hasPostNotificationsPermission() below, which
    // lint's static check can't see through since it isn't an inline ActivityCompat call.
    fun notifyUnreadCountIncreased(unreadCount: Int) {
        if (!hasPostNotificationsPermission()) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("HR Leave")
            .setContentText("You have $unreadCount unread notification(s)")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun hasPostNotificationsPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
