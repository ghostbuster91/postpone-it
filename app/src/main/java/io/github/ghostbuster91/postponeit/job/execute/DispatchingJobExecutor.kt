package io.github.ghostbuster91.postponeit.job.execute

import android.content.Context
import android.content.Intent
import com.github.salomonbrys.kodein.android.KodeinBroadcastReceiver
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.job.JobService

class DispatchingJobExecutor : KodeinBroadcastReceiver() {

    private val jobService by instance<JobService>()

    override fun onBroadcastReceived(context: Context, intent: Intent) {
        val delayedJob = jobService.findJob(intent.getStringExtra(KEY))
        if (delayedJob.requiresAcceptance) {
            context.sendBroadcast(RequiresAcceptanceJobExecutor.intent(context, delayedJob.id))
        } else {
            context.sendBroadcast(SendSmsJobExecutor.intent(context, delayedJob.id))
        }
    }

    companion object {
        fun intent(context: Context, delayedJobId: String): Intent {
            return Intent(context, RequiresAcceptanceJobExecutor::class.java)
                    .putExtra(KEY, delayedJobId)
        }

        private const val KEY = "delayedJobId"
    }
}