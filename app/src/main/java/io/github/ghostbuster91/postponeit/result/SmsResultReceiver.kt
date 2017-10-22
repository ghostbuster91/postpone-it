package io.github.ghostbuster91.postponeit.result

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.JobService

abstract class SmsResultReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val delayedJob = jobService.findJob(intent.getStringExtra(JOB_ID))
        val newStatus = mapResultToJobStatus(resultCode)
        Log.d(SmsDeliveryResultReceiver::class.java.name, "Job updated to status: $newStatus")
        jobService.updateJob(delayedJob.copy(status = newStatus))
    }

    abstract fun mapResultToJobStatus(resultCode: Int): DelayedJobStatus

    companion object {
        val JOB_ID = "JOB_KEY"
        lateinit var jobService : JobService
    }
}