package io.github.ghostbuster91.postponeit.job.execute

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.NotificationCompat
import android.telephony.SmsManager
import android.util.Log
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.jobServiceProvider
import io.github.ghostbuster91.postponeit.job.list.JobListFragment

class SendSmsJobExecutor : BroadcastReceiver() {

    private val jobService by lazy { jobServiceProvider }

    override fun onReceive(context: Context?, intent: Intent) {
        val smsManager = SmsManager.getDefault()
        val delayedJobId = intent.getIntExtra(KEY, 0)
        val delayedJob = jobService.findJob(delayedJobId)
        smsManager.sendTextMessage(delayedJob.number, null, delayedJob.text, null, null)
        jobService.updateJob(delayedJob.copy(status = DelayedJobStatus.EXECUTED))
        Log.d(SendSmsJobExecutor::class.java.name, "$delayedJob exeuted")
        val notificationClickIntent = PendingIntent.getActivity(context, 0, Intent(context, JobListFragment::class.java), 0)
        val notification = createNotification(context, notificationClickIntent, "Sms sent to ${delayedJob.number}", delayedJob.text)
        val mNotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, notification)
    }

    private fun createNotification(context: Context?, notificationClickIntent: PendingIntent?, title: String, text: String) =
            NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.hourglass_icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setContentIntent(notificationClickIntent)
                    .build()

    companion object {
        private const val KEY = "SMS_DATA_KEY"
        fun intent(context: Context, delayedJobId: Int) =
                Intent(context, SendSmsJobExecutor::class.java)
                        .setData(Uri.parse(delayedJobId.toString()))
                        .putExtra(KEY, delayedJobId)
    }
}