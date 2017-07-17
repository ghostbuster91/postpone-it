package io.github.ghostbuster91.postponeit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log

class SendSmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val smsManager = SmsManager.getDefault()
        val delayedJob = intent.getBundleExtra(KEY).getSerializable(KEY) as DelayedJob
        smsManager.sendTextMessage(delayedJob.number, null, delayedJob.text, null, null)
        Log.d(SendSmsBroadcastReceiver::class.java.name, "$delayedJob exeuted")
    }

    companion object {
        const private val KEY = "SMS_DATA_KEY"
        fun intent(context: Context, delayedJob: DelayedJob): Intent {
            return Intent(context, SendSmsBroadcastReceiver::class.java)
                    .setData(Uri.parse(delayedJob.toString()))
                    .putExtra(KEY, Bundle().apply { putSerializable(KEY, delayedJob) })
        }
    }
}