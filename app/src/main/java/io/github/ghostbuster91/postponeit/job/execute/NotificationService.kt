package io.github.ghostbuster91.postponeit.job.execute

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.HomeActivity

interface NotificationService {
    fun showNotification(title: String, content: String, vararg action: NotificationCompat.Action)
}

class NotificationServiceImpl(private val context: Context) : NotificationService {

    override fun showNotification(title: String, content: String, vararg action: NotificationCompat.Action) {
        val notificationClickIntent = PendingIntent.getActivity(context, 0, HomeActivity.intent(context), 0)
        val notification = createNotification(context, notificationClickIntent, title, content, *action)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Default PostponeIt channel", NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(channel)
        }
        mNotificationManager.notify(0, notification)
    }

    private fun createNotification(context: Context, notificationClickIntent: PendingIntent?, title: String, text: String, vararg action: NotificationCompat.Action): Notification? {
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.hourglass_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(notificationClickIntent)
                .apply {
                    action.forEach {
                        addAction(it)
                    }
                }
                .build()
    }

    companion object {
        private const val CHANNEL_ID = "Postpone"
    }
}