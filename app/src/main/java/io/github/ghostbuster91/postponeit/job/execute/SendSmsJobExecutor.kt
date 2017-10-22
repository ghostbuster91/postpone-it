package io.github.ghostbuster91.postponeit.job.execute

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.JobService
import io.github.ghostbuster91.postponeit.result.SmsDeliveryResultReceiver
import io.github.ghostbuster91.postponeit.result.SmsSendingResultReceiver

class SendSmsJobExecutor : BroadcastReceiver() {

    private val smsManager by lazy { SmsManager.getDefault() }

    override fun onReceive(context: Context, intent: Intent) {
        val delayedJob = jobService.findJob(intent.getStringExtra(KEY))
        val deliveryIntent = createDeliveryIntent(context, delayedJob)
        val sentIntent = createSentIntent(context, delayedJob)
        smsManager.sendTextMessage(delayedJob.number, null, delayedJob.text, sentIntent, deliveryIntent)
        jobService.updateJob(delayedJob.copy(status = DelayedJobStatus.Executed))
        Log.d(SendSmsJobExecutor::class.java.name, "$delayedJob exeuted")
        notificationService.showNotification("Sms sent to ${delayedJob.number}", delayedJob.text)
    }

    private fun createSentIntent(context: Context, delayedJob: DelayedJob): PendingIntent {
        val sentIntent = SmsSendingResultReceiver.createIntent(context, delayedJob.id)
        return wrapWithPendingIntent(context, sentIntent)
    }

    private fun createDeliveryIntent(context: Context, delayedJob: DelayedJob): PendingIntent {
        val deliveredIntent = SmsDeliveryResultReceiver.createIntent(context, delayedJob.id)
        return wrapWithPendingIntent(context, deliveredIntent)
    }

    private fun wrapWithPendingIntent(context: Context, intent: Intent?) =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


    companion object {
        private const val KEY = "SMS_DATA_KEY"
        fun intent(context: Context, delayedJobId: String) =
                Intent(context, SendSmsJobExecutor::class.java)
                        .putExtra(KEY, delayedJobId)

        lateinit var jobService: JobService
        lateinit var notificationService: NotificationService
    }
}