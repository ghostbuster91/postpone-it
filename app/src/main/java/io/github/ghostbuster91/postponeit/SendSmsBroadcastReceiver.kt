package io.github.ghostbuster91.postponeit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class SendSmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val smsManager = SmsManager.getDefault()
        val delayedJob = intent.getSerializableExtra(KEY) as DelayedJob
        smsManager.sendTextMessage(delayedJob.number, null, delayedJob.text, null, null)
    }

    companion object {
        const private val KEY = "SMS_DATA_KEY"
        fun intent(context: Context, delayedJob: DelayedJob): Intent {
            return Intent(context, SendSmsBroadcastReceiver::class.java)
                    .putExtra(KEY, delayedJob)
        }
    }
}