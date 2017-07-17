package io.github.ghostbuster91.postponeit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log

class SendSmsBroadcastReceiver : BroadcastReceiver() {

    private val jobService by lazy { jobServiceProvider }

    override fun onReceive(context: Context?, intent: Intent) {
        val smsManager = SmsManager.getDefault()
        val delayedJobId = intent.getIntExtra(KEY, 0)
        val delayedJob = jobService.findJob(delayedJobId)
        smsManager.sendTextMessage(delayedJob.number, null, delayedJob.text, null, null)
        jobService.updateJob(delayedJob.copy(status = DelayedJobStatus.EXECUTED))
        Log.d(SendSmsBroadcastReceiver::class.java.name, "$delayedJob exeuted")
    }

    companion object {
        const private val KEY = "SMS_DATA_KEY"
        fun intent(context: Context, delayedJobId: Int) =
                Intent(context, SendSmsBroadcastReceiver::class.java)
                        .setData(Uri.parse(delayedJobId.toString()))
                        .putExtra(KEY, delayedJobId)
    }
}