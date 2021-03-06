package io.github.ghostbuster91.postponeit.result

import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.salomonbrys.kodein.android.KodeinBroadcastReceiver
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.JobService

abstract class SmsResultReceiver : KodeinBroadcastReceiver() {

    private val jobService by instance<JobService>()

    override fun onBroadcastReceived(context: Context, intent: Intent) {
        val delayedJob = jobService.findJob(intent.getStringExtra(JOB_ID))
        val newStatus = mapResultToJobStatus(resultCode, delayedJob, context)
        Log.d(SmsDeliveryResultReceiver::class.java.name, "Job updated to status: $newStatus")
        jobService.updateJob(delayedJob.copy(status = newStatus))
    }

    abstract fun mapResultToJobStatus(resultCode: Int, delayedJob: DelayedJob, context: Context): DelayedJobStatus

    companion object {
        val JOB_ID = "JOB_KEY"
    }
}