package io.github.ghostbuster91.postponeit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class SendSmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage("23123123", null, "hello44", null, null)
    }
}