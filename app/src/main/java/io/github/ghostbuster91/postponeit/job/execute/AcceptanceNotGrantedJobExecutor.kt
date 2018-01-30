package io.github.ghostbuster91.postponeit.job.execute

import android.content.Context
import android.content.Intent
import com.github.salomonbrys.kodein.android.KodeinBroadcastReceiver
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.JobService

class AcceptanceNotGrantedJobExecutor : KodeinBroadcastReceiver() {

    private val jobService by instance<JobService>()

    override fun onBroadcastReceived(context: Context, intent: Intent) {
        val delayedJob = jobService.findJob(intent.getStringExtra(KEY))
        jobService.updateJob(delayedJob.copy(status = DelayedJobStatus.Canceled))
    }

    companion object {
        fun intent(context: Context, delayedJobId: String): Intent {
            return Intent(context, AcceptanceNotGrantedJobExecutor::class.java)
                    .putExtra(KEY, delayedJobId)
        }

        private const val KEY = "delayedJob"
    }
}