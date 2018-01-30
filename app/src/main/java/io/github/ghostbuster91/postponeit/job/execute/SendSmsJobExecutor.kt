package io.github.ghostbuster91.postponeit.job.execute

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import com.github.salomonbrys.kodein.android.KodeinBroadcastReceiver
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.PostponeItApplication
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.JobService
import io.github.ghostbuster91.postponeit.result.SmsDeliveryResultReceiver
import io.github.ghostbuster91.postponeit.result.SmsSendingResultReceiver

class SendSmsJobExecutor : KodeinBroadcastReceiver(), SendSmsJobExecutorI {
    private val smsManager by lazy { SmsManager.getDefault() }
    private val notificationService by instance<NotificationService>()
    private val jobService by instance<JobService>()

    override fun onBroadcastReceived(context: Context, intent: Intent) {
        val delayedJob = jobService.findJob(intent.getStringExtra(KEY))
        executeDelayedJob(delayedJob)
    }

    override fun executeDelayedJob(delayedJob: DelayedJob) {
        val context : Context? = null
        val deliveryIntent = createDeliveryIntent(context!!, delayedJob)
        val sentIntent = createSentIntent(context, delayedJob)
        smsManager.sendTextMessage(delayedJob.number, null, delayedJob.text, sentIntent, deliveryIntent)
        jobService.updateJob(delayedJob.copy(status = DelayedJobStatus.Executed))
        Log.d(SendSmsJobExecutor::class.java.name, "$delayedJob exeuted")
        notificationService.showNotification("Sms sent to ${delayedJob.number}", delayedJob.text)
    }

    private fun createSentIntent(context: Context, delayedJob: DelayedJob): PendingIntent {
        val sentIntent = SmsSendingResultReceiver.createIntent(context, delayedJob.id)
        return sentIntent.wrapWithPendingIntent(context)
    }

    private fun createDeliveryIntent(context: Context, delayedJob: DelayedJob): PendingIntent {
        val deliveredIntent = SmsDeliveryResultReceiver.createIntent(context, delayedJob.id)
        return deliveredIntent.wrapWithPendingIntent(context)
    }

    companion object {
        private const val KEY = "SMS_DATA_KEY"
        fun intent(context: Context, delayedJobId: String) =
                Intent(context, SendSmsJobExecutor::class.java)
                        .putExtra(KEY, delayedJobId)
    }
}

interface SendSmsJobExecutorI {
    fun executeDelayedJob(delayedJob: DelayedJob)
}