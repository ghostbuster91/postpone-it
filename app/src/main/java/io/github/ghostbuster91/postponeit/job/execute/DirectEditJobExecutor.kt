package io.github.ghostbuster91.postponeit.job.execute

import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.github.salomonbrys.kodein.android.KodeinBroadcastReceiver
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.job.JobService

class DirectEditJobExecutor : KodeinBroadcastReceiver() {

    private val jobService by instance<JobService>()

    override fun onBroadcastReceived(context: Context, intent: Intent) {
        val delayedJobId = intent.getStringExtra(DirectEditJobExecutor.KEY)
        val delayedJob = jobService.findJob(delayedJobId)
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val newMessage = remoteInput.getString(REMOTE_INPUT_KEY)
        jobService.updateJob(delayedJob.copy(text = newMessage))
        context.sendBroadcast(SendSmsJobExecutor.intent(context, delayedJobId))
    }

    companion object {
        private const val KEY = "SMS_DATA_KEY"
        private const val REMOTE_INPUT_KEY = "remoteEditKey"

        fun createRemoteInput(label: String) = RemoteInput.Builder(REMOTE_INPUT_KEY)
                .setLabel(label)
                .build()

        fun intent(context: Context, delayedJobId: String) =
                Intent(context, DirectEditJobExecutor::class.java)
                        .putExtra(KEY, delayedJobId)
    }
}