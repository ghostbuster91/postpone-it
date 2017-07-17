package io.github.ghostbuster91.postponeit.job.execute

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.jobServiceProvider

class SendSmsJobExecutor : BroadcastReceiver() {

    private val jobService by lazy { jobServiceProvider }

    override fun onReceive(context: Context?, intent: Intent) {
        val smsManager = SmsManager.getDefault()
        val delayedJobId = intent.getIntExtra(KEY, 0)
        val delayedJob = jobService.findJob(delayedJobId)
        smsManager.sendTextMessage(delayedJob.number, null, delayedJob.text, null, null)
        jobService.updateJob(delayedJob.copy(status = DelayedJobStatus.EXECUTED))
        Log.d(SendSmsJobExecutor::class.java.name, "$delayedJob exeuted")
    }

    companion object {
        const private val KEY = "SMS_DATA_KEY"
        fun intent(context: Context, delayedJobId: Int) =
                Intent(context, SendSmsJobExecutor::class.java)
                        .setData(Uri.parse(delayedJobId.toString()))
                        .putExtra(KEY, delayedJobId)
    }
}