package io.github.ghostbuster91.postponeit.job.execute

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.JobListActivity

interface NotificationService {
    fun showNotification(title: String, content: String, notificationCustomizer: NotificationCompat.Builder.() -> NotificationCompat.Builder = { this })
}

class NotificationServiceImpl(private val context: Context) : NotificationService {

    override fun showNotification(title: String, content: String, notificationCustomizer: NotificationCompat.Builder.() -> NotificationCompat.Builder) {
        val notificationClickIntent = PendingIntent.getActivity(context, 0, JobListActivity.intent(context), 0)
        val notification = createNotification(context, notificationClickIntent, title, content, notificationCustomizer)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Default PostponeIt channel", NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(channel)
        }
        mNotificationManager.notify(0, notification)
    }

    private fun createNotification(context: Context, notificationClickIntent: PendingIntent?, title: String, text: String, customize: NotificationCompat.Builder.() -> NotificationCompat.Builder): Notification? {
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.hourglass_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(notificationClickIntent)
                .customize()
                .build()
    }

    companion object {
        private const val CHANNEL_ID = "Postpone"
    }
}