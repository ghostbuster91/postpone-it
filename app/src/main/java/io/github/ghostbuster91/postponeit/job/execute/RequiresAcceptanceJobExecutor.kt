package io.github.ghostbuster91.postponeit.job.execute

import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.github.salomonbrys.kodein.android.KodeinBroadcastReceiver
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.JobService

class RequiresAcceptanceJobExecutor : KodeinBroadcastReceiver() {
    private val jobService by instance<JobService>()
    private val notificationService by instance<NotificationService>()

    override fun onBroadcastReceived(context: Context, intent: Intent) {
        val delayedJob = jobService.findJob(intent.getStringExtra(KEY))

        notificationService.showNotification("Message requires acceptance",
                "You are about to send ${delayedJob.text} to ${delayedJob.number}",
                {
                    addAction(createSendAction(context, delayedJob))
                    setDeleteIntent(createDeleteIntent(context, delayedJob))
                })
    }

    private fun createDeleteIntent(context: Context, delayedJob: DelayedJob) =
            AcceptanceNotGrantedJobExecutor.intent(context, delayedJob.id).wrapWithPendingIntent(context)

    private fun createSendAction(context: Context, delayedJob: DelayedJob): NotificationCompat.Action {
        return NotificationCompat.Action(
                R.drawable.ic_send_black_24dp,
                "Send",
                SendSmsJobExecutor.intent(context, delayedJob.id).wrapWithPendingIntent(context))
    }

    companion object {
        fun intent(context: Context, delayedJobId: String): Intent {
            return Intent(context, RequiresAcceptanceJobExecutor::class.java)
                    .putExtra(KEY, delayedJobId)
        }

        private const val KEY = "delayedJobId"
    }
}