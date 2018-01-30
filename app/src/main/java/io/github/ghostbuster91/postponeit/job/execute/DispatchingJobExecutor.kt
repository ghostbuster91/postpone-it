package io.github.ghostbuster91.postponeit.job.execute

import android.content.Context
import android.content.Intent
import com.github.salomonbrys.kodein.android.KodeinBroadcastReceiver
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.AppEvent
import io.github.ghostbuster91.postponeit.job.JobService

class DispatchingJobExecutor : KodeinBroadcastReceiver() {

    private val jobService by instance<JobService>()

    override fun onBroadcastReceived(context: Context, intent: Intent) {
        val delayedJobId = intent.getStringExtra(KEY)
        AppEvent.AlarmFired(delayedJobId)
    }

    companion object {
        fun intent(context: Context, delayedJobId: String): Intent {
            return Intent(context, RequiresAcceptanceJobExecutor::class.java)
                    .putExtra(KEY, delayedJobId)
        }

        private const val KEY = "delayedJobId"
    }
}